# Documentacion del script de base de datos

Este documento describe el script `docs/db/agro-directo-db.txt`, que define el esquema inicial de PostgreSQL para AgroDirecto.

## Objetivo del script

El script crea el esquema `agro_directo` y prepara la estructura relacional base para soportar una plataforma de comercio agropecuario directo. Incluye usuarios, roles, permisos, perfiles por tipo de usuario, productos, carrito, pedidos, pagos, logistica, notificaciones y auditoria.

El script esta envuelto en una transaccion:

```sql
BEGIN;
...
COMMIT;
```

Esto permite aplicar todos los cambios como una unidad. Si ocurre un error antes del `COMMIT`, PostgreSQL revierte la transaccion.

## Como ejecutar el script

Con una base de datos creada previamente, por ejemplo `agro_directo`, se puede ejecutar asi:

```powershell
psql -U postgres -d agro_directo -f docs/db/agro-directo-db.txt
```

El script usa `CREATE IF NOT EXISTS` e `INSERT ... ON CONFLICT DO NOTHING` en varios puntos, por lo que esta preparado para ser ejecutado mas de una vez sin duplicar catalogos o datos semilla.

## Estructura general

| Bloque | Proposito |
| --- | --- |
| Esquema | Crea `agro_directo`. |
| Funcion general | Crea `set_updated_at()` para actualizar marcas de tiempo. |
| Tablas maestras | Catalogos de roles, permisos, estados y unidades. |
| Usuarios e identidad | Usuarios, roles de usuario y refresh tokens. |
| Perfiles por rol | Datos especificos de productores, compradores y transportistas. |
| Documentos y verificacion | Documentos subidos y revisiones administrativas. |
| Productos | Publicaciones, imagenes, precios de referencia y alertas. |
| Carrito | Carritos activos y productos seleccionados. |
| Pedidos | Ordenes e items de pedido. |
| Pagos | Pago asociado a pedido, QR y comprobantes. |
| Logistica | Envios, postulaciones, asignaciones y entregas. |
| Calificaciones | Puntajes de compradores a productores. |
| Notificaciones | Mensajes por usuario con carga JSON opcional. |
| Auditoria | Registro generico de acciones sobre entidades. |
| Indices | Optimizacion de consultas frecuentes. |
| Triggers | Actualizacion automatica de `updated_at`. |
| Datos semilla | Roles, permisos, estados, catalogos y permisos por rol. |

## Esquema y funcion general

El script crea el esquema:

```sql
CREATE SCHEMA IF NOT EXISTS agro_directo;
```

Tambien define la funcion:

```sql
agro_directo.set_updated_at()
```

Esta funcion asigna `CURRENT_TIMESTAMP` a la columna `updated_at` antes de una actualizacion. Se reutiliza mediante triggers en tablas que tienen campos de auditoria temporal.

## Tablas maestras

Estas tablas funcionan como catalogos o listas controladas:

| Tabla | Descripcion |
| --- | --- |
| `roles` | Roles del sistema: productor, comprador, transportista y administrador. |
| `permissions` | Permisos funcionales usados para autorizacion. |
| `role_permissions` | Relacion muchos a muchos entre roles y permisos. |
| `user_statuses` | Estados del usuario, como registrado, verificado o suspendido. |
| `document_types` | Tipos de documentos de verificacion. |
| `product_units` | Unidades comerciales de producto, como quintal, arroba, kg o unidad. |
| `product_statuses` | Estados de publicacion de productos. |
| `order_statuses` | Estados del ciclo de vida de pedidos. |
| `payment_statuses` | Estados del pago. |
| `shipment_statuses` | Estados de envio y entrega. |

La mayoria usa `code` como identificador funcional unico y `name` como texto visible.

## Usuarios e identidad

| Tabla | Descripcion |
| --- | --- |
| `users` | Tabla principal de usuarios. Guarda datos personales, credenciales, estado, verificaciones y bloqueo de login. |
| `user_roles` | Asigna uno o mas roles a un usuario. Permite marcar un rol principal con `is_primary`. |
| `refresh_tokens` | Guarda tokens de refresco mediante hash, fecha de expiracion, revocacion, reemplazo, user agent e IP. |

Reglas importantes:

- `users.email` es unico.
- `user_roles` evita duplicar el mismo rol para un usuario.
- El indice unico parcial `uq_user_primary_role` permite solo un rol principal por usuario.
- `refresh_tokens.expires_at` debe ser posterior a `issued_at`.
- `refresh_tokens.revoked_at`, si existe, no puede ser anterior a `issued_at`.

## Perfiles por rol

Los perfiles extienden a `users` mediante `user_id` como llave primaria y foranea.

| Tabla | Descripcion |
| --- | --- |
| `producer_profiles` | Datos del productor: tipo, finca, ubicacion, experiencia, geolocalizacion y puntaje de confianza. |
| `buyer_profiles` | Datos del comprador: tipo, razon comercial opcional y zona de compra. |
| `carrier_profiles` | Datos del transportista: tipo de transporte, capacidad, zona de operacion, licencia y placa. |

Validaciones relevantes:

- `producer_type` solo acepta `INDIVIDUAL`, `ASSOCIATION` o `COOPERATIVE`.
- `buyer_type` solo acepta `INDIVIDUAL`, `BUSINESS` o `COMPANY`.
- `transport_type` solo acepta `TRUCK`, `PICKUP`, `MOTORBIKE` u `OTHER`.
- `operation_zone` solo acepta `LOCAL`, `REGIONAL` o `DEPARTMENTAL`.
- Las coordenadas se validan por rango: latitud entre `-90` y `90`, longitud entre `-180` y `180`.
- Las coordenadas deben guardarse en pares: no se permite latitud sin longitud ni longitud sin latitud.
- Licencia y placa del transportista son unicas.

## Documentos y verificacion

| Tabla | Descripcion |
| --- | --- |
| `verification_documents` | Documentos subidos por usuarios para validacion. |
| `verification_reviews` | Revision de un documento por un usuario revisor. |

`verification_reviews.decision` solo permite:

- `APPROVED`
- `REJECTED`

Este bloque soporta flujos de validacion de identidad, productor o transportista.

## Productos y publicaciones

| Tabla | Descripcion |
| --- | --- |
| `products` | Publicaciones creadas por productores. Incluye cantidad, precio, unidad, estado y fechas de disponibilidad. |
| `product_images` | Imagenes asociadas a un producto. |
| `market_reference_prices` | Precios de referencia por producto, mercado, unidad y fecha observada. |
| `seasonal_alert_subscriptions` | Alertas de compradores por nombre de producto. |

Reglas importantes:

- `available_quantity` no puede ser negativa.
- `unit_price` no puede ser negativo.
- Si `is_pre_sale` es `TRUE`, `estimated_available_date` debe estar definida.
- Un comprador no puede duplicar una alerta para el mismo `product_name`.

## Carrito

| Tabla | Descripcion |
| --- | --- |
| `carts` | Carrito de un comprador con estado `ACTIVE`, `ABANDONED` o `CONVERTED`. |
| `cart_items` | Productos agregados al carrito, con cantidad y precio capturado al momento de agregar. |

Reglas importantes:

- `cart_items.quantity` debe ser mayor que cero.
- `unit_price_snapshot` no puede ser negativo.
- `uq_cart_product` evita duplicar el mismo producto dentro del mismo carrito.

## Pedidos

| Tabla | Descripcion |
| --- | --- |
| `orders` | Pedido realizado por un comprador. Guarda estado, monto total, direccion, zona y notas. |
| `order_items` | Productos incluidos en el pedido, productor responsable, cantidad, precio y subtotal. |

Reglas importantes:

- `orders.total_amount` no puede ser negativo.
- `order_items.quantity` debe ser mayor que cero.
- `order_items.unit_price` y `order_items.subtotal` no pueden ser negativos.
- `confirmation_status` solo permite `PENDING`, `CONFIRMED` o `REJECTED`.
- `uq_order_product` evita duplicar el mismo producto dentro del mismo pedido.

## Pagos

| Tabla | Descripcion |
| --- | --- |
| `payments` | Pago unico asociado a un pedido. |
| `payment_qr` | QR unico asociado a un pago. |
| `payment_receipts` | Comprobantes subidos por usuarios y verificados por administracion. |

Reglas importantes:

- `payments.order_id` es unico, por lo que un pedido tiene un solo pago principal.
- `payment_method` solo permite `QR_TRANSFER`.
- `payments.amount` no puede ser negativo.
- `payment_qr.payment_id` es unico.
- `verification_result` solo permite `APPROVED` o `REJECTED`.

## Logistica y entrega

| Tabla | Descripcion |
| --- | --- |
| `shipments` | Envio asociado a un pedido, productor, comprador y estado logistico. Puede guardar coordenadas de recojo y entrega. |
| `shipment_items` | Relacion entre envio e items del pedido. |
| `shipment_applications` | Postulaciones de transportistas a un envio. |
| `shipment_assignments` | Transportista asignado a un envio. |
| `delivery_confirmations` | Confirmacion de recepcion por parte del comprador. |
| `signed_receipts` | Recibos firmados con imagen de firma. |

Reglas importantes:

- Las coordenadas de recojo y entrega deben guardarse en pares.
- Las coordenadas se validan por rango geografico.
- Un transportista no puede postular dos veces al mismo envio.
- Un envio solo puede tener una asignacion activa en `shipment_assignments`.
- Un envio solo puede tener una confirmacion de entrega.
- Un envio solo puede tener un recibo firmado.

## Calificaciones y confianza

| Tabla | Descripcion |
| --- | --- |
| `producer_ratings` | Calificacion que un comprador asigna a un productor por un pedido. |

Reglas importantes:

- `score` debe estar entre `1` y `5`.
- `uq_rating_order` evita que el mismo comprador califique al mismo productor mas de una vez por el mismo pedido.

## Notificaciones

| Tabla | Descripcion |
| --- | --- |
| `notifications` | Notificaciones dirigidas a usuarios. |

Campos relevantes:

- `type`: clasificacion funcional de la notificacion.
- `title` y `message`: contenido visible.
- `is_read`: marca de lectura.
- `payload_json`: datos adicionales en formato `JSONB`.

## Auditoria

| Tabla | Descripcion |
| --- | --- |
| `audit_log` | Registro generico de cambios o acciones ejecutadas sobre entidades del sistema. |

Campos relevantes:

- `actor_user_id`: usuario que ejecuto la accion.
- `entity_name`: nombre de la entidad afectada.
- `entity_id`: identificador del registro afectado.
- `action`: tipo de accion.
- `old_values` y `new_values`: valores previos y nuevos en `JSONB`.
- `ip_address` y `user_agent`: contexto tecnico de la solicitud.

## Relaciones principales

```text
users
  -> user_statuses
  -> user_roles -> roles -> role_permissions -> permissions
  -> refresh_tokens
  -> producer_profiles
  -> buyer_profiles
  -> carrier_profiles
  -> verification_documents -> verification_reviews

products
  -> users como productor
  -> product_units
  -> product_statuses
  -> product_images

carts
  -> users como comprador
  -> cart_items -> products

orders
  -> users como comprador
  -> order_statuses
  -> order_items -> products
  -> order_items -> users como productor

payments
  -> orders
  -> payment_statuses
  -> payment_qr
  -> payment_receipts

shipments
  -> orders
  -> users como productor
  -> users como comprador
  -> shipment_statuses
  -> shipment_items -> order_items
  -> shipment_applications -> users como transportista
  -> shipment_assignments -> users como transportista
  -> delivery_confirmations
  -> signed_receipts
```

## Indices

El script crea indices para consultas frecuentes sobre:

| Area | Indices principales |
| --- | --- |
| Usuarios | Email, estado y combinacion email/estado. |
| Tokens | Usuario y fecha de expiracion. |
| Roles/permisos | Rol y permiso. |
| Productos | Productor, estado, nombre, productor/estado y fecha de creacion. |
| Pedidos | Comprador, estado, comprador/estado y fecha de creacion. |
| Items de pedido | Pedido y productor. |
| Envios | Pedido, estado, productor y comprador. |
| Notificaciones | Usuario/lectura y fecha de creacion. |
| Documentos | Usuario propietario. |
| Pagos | Comprobantes por pago. |
| Precios de referencia | Producto y fecha observada. |
| Geolocalizacion | Latitud y longitud de productores. |
| Auditoria | Actor, entidad/id y fecha de creacion. |

Estos indices estan orientados a listados, busquedas por estado, consultas historicas y pantallas operativas.

## Triggers de `updated_at`

El script elimina y vuelve a crear triggers `BEFORE UPDATE` para refrescar `updated_at` en:

| Tabla |
| --- |
| `users` |
| `producer_profiles` |
| `buyer_profiles` |
| `carrier_profiles` |
| `products` |
| `carts` |
| `orders` |
| `payments` |
| `shipments` |

Todos ejecutan la funcion `agro_directo.set_updated_at()`.

## Datos semilla

### Roles

| Codigo | Nombre |
| --- | --- |
| `PRODUCER` | Productor |
| `BUYER` | Comprador |
| `CARRIER` | Transportista |
| `ADMIN` | Administrador |

### Estados de usuario

| Codigo | Nombre |
| --- | --- |
| `REGISTERED` | Registrado |
| `PENDING_VERIFICATION` | Pendiente de verificacion |
| `VERIFIED` | Verificado |
| `REJECTED` | Rechazado |
| `INCOMPLETE` | Incompleto |
| `SUSPENDED` | Suspendido |
| `LOCKED` | Bloqueado |

### Tipos de documento

| Codigo | Nombre |
| --- | --- |
| `CI` | Carnet de Identidad |
| `PRODUCER_REGISTRY` | Registro de Productor |
| `COMMUNITY_CERTIFICATE` | Certificado Comunitario |
| `DRIVER_LICENSE` | Licencia de Conducir |
| `SOAT` | SOAT |
| `VEHICLE_REGISTRY` | Registro de Vehiculo |

### Unidades de producto

| Codigo | Nombre |
| --- | --- |
| `QUINTAL` | Quintal |
| `ARROBA` | Arroba |
| `KG` | Kilogramo |
| `UNIT` | Unidad |

### Estados de producto

| Codigo | Nombre |
| --- | --- |
| `DRAFT` | Borrador |
| `PUBLISHED` | Publicado |
| `RESERVED` | Reservado |
| `SOLD_OUT` | Agotado |
| `INACTIVE` | Inactivo |

### Estados de pedido

| Codigo | Nombre |
| --- | --- |
| `CREATED` | Creado |
| `PENDING_CONFIRMATION` | Pendiente de confirmacion |
| `CONFIRMED` | Confirmado |
| `PARTIALLY_CONFIRMED` | Parcialmente confirmado |
| `PAID` | Pagado |
| `IN_TRANSIT` | En transito |
| `DELIVERED` | Entregado |
| `CANCELLED` | Cancelado |

### Estados de pago

| Codigo | Nombre |
| --- | --- |
| `PENDING` | Pendiente |
| `QR_GENERATED` | QR generado |
| `RECEIPT_UPLOADED` | Comprobante subido |
| `VERIFIED` | Verificado |
| `REJECTED` | Rechazado |

### Estados de envio

| Codigo | Nombre |
| --- | --- |
| `OPEN` | Abierto |
| `APPLIED` | Postulado |
| `ASSIGNED` | Asignado |
| `PICKED_UP` | Recogido |
| `IN_TRANSIT` | En transito |
| `DELIVERED` | Entregado |
| `CLOSED` | Cerrado |
| `CANCELLED` | Cancelado |

## Permisos por rol

El script asigna permisos iniciales de esta forma:

| Rol | Alcance |
| --- | --- |
| `ADMIN` | Recibe todos los permisos. |
| `PRODUCER` | Login, usuario, productos, lectura/confirmacion de pedidos, envios, documentos, calificaciones y notificaciones. |
| `BUYER` | Login, usuario, productos, carrito, pedidos, pagos, envios, documentos, calificaciones y notificaciones. |
| `CARRIER` | Login, usuario, lectura/postulacion/actualizacion de envios, documentos y notificaciones. |

## Consideraciones para Flyway

Actualmente el script esta documentado como archivo `.txt`. Para que Flyway lo ejecute automaticamente en Spring Boot, debe estar dentro de:

```text
src/main/resources/db/migration/
```

Y debe seguir la convencion de nombre de Flyway, por ejemplo:

```text
V1__initial_schema.sql
```

Si el script se usa como primera migracion, conviene mover o copiar su contenido a ese archivo y mantener este documento como referencia tecnica.
