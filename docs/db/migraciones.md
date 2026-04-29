# Migraciones Flyway

Este documento describe las migraciones creadas en `src/main/resources/db/migration/` para construir la base de datos de AgroDirecto.

## Convencion usada

Las migraciones siguen el formato de Flyway:

```text
V<numero>__<descripcion>.sql
```

Ejemplo:

```text
V13__create_users_table.sql
```

El orden numerico es importante porque las tablas tienen claves foraneas entre si. Por eso primero se crean el esquema, la funcion compartida, los catalogos base y despues las tablas transaccionales.

## Reglas aplicadas

- Hay un archivo de migracion por cada tabla creada.
- Los indices especificos de una tabla viven en el mismo archivo donde se crea esa tabla.
- Los triggers de `updated_at` viven en el archivo de la tabla correspondiente.
- Los datos semilla se separan en migraciones finales.
- Todas las tablas se crean dentro del esquema `agro_directo`.

## Migraciones de infraestructura

| Archivo | Responsabilidad |
| --- | --- |
| `V1__create_schema.sql` | Crea el esquema `agro_directo`. |
| `V2__create_updated_at_function.sql` | Crea la funcion `agro_directo.set_updated_at()` usada por triggers. |

## Migraciones por tabla

| Version | Archivo | Tabla | Descripcion |
| --- | --- | --- | --- |
| V3 | `V3__create_roles_table.sql` | `roles` | Roles funcionales del sistema. |
| V4 | `V4__create_permissions_table.sql` | `permissions` | Permisos de autorizacion. |
| V5 | `V5__create_role_permissions_table.sql` | `role_permissions` | Relacion muchos a muchos entre roles y permisos. |
| V6 | `V6__create_user_statuses_table.sql` | `user_statuses` | Catalogo de estados de usuario. |
| V7 | `V7__create_document_types_table.sql` | `document_types` | Catalogo de tipos de documento. |
| V8 | `V8__create_product_units_table.sql` | `product_units` | Catalogo de unidades comerciales. |
| V9 | `V9__create_product_statuses_table.sql` | `product_statuses` | Catalogo de estados de producto. |
| V10 | `V10__create_order_statuses_table.sql` | `order_statuses` | Catalogo de estados de pedido. |
| V11 | `V11__create_payment_statuses_table.sql` | `payment_statuses` | Catalogo de estados de pago. |
| V12 | `V12__create_shipment_statuses_table.sql` | `shipment_statuses` | Catalogo de estados logisticos. |
| V13 | `V13__create_users_table.sql` | `users` | Usuarios, credenciales, verificaciones y bloqueo de login. |
| V14 | `V14__create_user_roles_table.sql` | `user_roles` | Asignacion de roles a usuarios. |
| V15 | `V15__create_refresh_tokens_table.sql` | `refresh_tokens` | Tokens de refresco, expiracion y revocacion. |
| V16 | `V16__create_producer_profiles_table.sql` | `producer_profiles` | Perfil extendido de productores. |
| V17 | `V17__create_buyer_profiles_table.sql` | `buyer_profiles` | Perfil extendido de compradores. |
| V18 | `V18__create_carrier_profiles_table.sql` | `carrier_profiles` | Perfil extendido de transportistas. |
| V19 | `V19__create_verification_documents_table.sql` | `verification_documents` | Documentos subidos para verificacion. |
| V20 | `V20__create_verification_reviews_table.sql` | `verification_reviews` | Revisiones de documentos. |
| V21 | `V21__create_products_table.sql` | `products` | Publicaciones de productos agropecuarios. |
| V22 | `V22__create_product_images_table.sql` | `product_images` | Imagenes de productos. |
| V23 | `V23__create_market_reference_prices_table.sql` | `market_reference_prices` | Precios de referencia por producto y fecha. |
| V24 | `V24__create_seasonal_alert_subscriptions_table.sql` | `seasonal_alert_subscriptions` | Suscripciones de compradores a alertas por producto. |
| V25 | `V25__create_carts_table.sql` | `carts` | Carritos de compra. |
| V26 | `V26__create_cart_items_table.sql` | `cart_items` | Productos dentro de un carrito. |
| V27 | `V27__create_orders_table.sql` | `orders` | Pedidos de compradores. |
| V28 | `V28__create_order_items_table.sql` | `order_items` | Productos dentro de un pedido. |
| V29 | `V29__create_payments_table.sql` | `payments` | Pagos asociados a pedidos. |
| V30 | `V30__create_payment_qr_table.sql` | `payment_qr` | QR generado para un pago. |
| V31 | `V31__create_payment_receipts_table.sql` | `payment_receipts` | Comprobantes de pago y verificacion. |
| V32 | `V32__create_shipments_table.sql` | `shipments` | Envios asociados a pedidos. |
| V33 | `V33__create_shipment_items_table.sql` | `shipment_items` | Items de pedido incluidos en un envio. |
| V34 | `V34__create_shipment_applications_table.sql` | `shipment_applications` | Postulaciones de transportistas. |
| V35 | `V35__create_shipment_assignments_table.sql` | `shipment_assignments` | Asignacion de transportista a envio. |
| V36 | `V36__create_delivery_confirmations_table.sql` | `delivery_confirmations` | Confirmacion de entrega por comprador. |
| V37 | `V37__create_signed_receipts_table.sql` | `signed_receipts` | Recibos firmados de entrega. |
| V38 | `V38__create_producer_ratings_table.sql` | `producer_ratings` | Calificaciones de productores. |
| V39 | `V39__create_notifications_table.sql` | `notifications` | Notificaciones por usuario. |
| V40 | `V40__create_audit_log_table.sql` | `audit_log` | Auditoria general de acciones. |

## Migraciones de datos semilla

| Archivo | Contenido |
| --- | --- |
| `V41__seed_roles.sql` | Inserta roles base: `PRODUCER`, `BUYER`, `CARRIER`, `ADMIN`. |
| `V42__seed_permissions.sql` | Inserta permisos funcionales del sistema. |
| `V43__seed_catalogs.sql` | Inserta estados de usuario, documentos, unidades, estados de producto, pedido, pago y envio. |
| `V44__seed_role_permissions.sql` | Asigna permisos iniciales por rol. |
| `V45__seed_admin_user.sql` | Inserta o actualiza el usuario administrador inicial y le asigna el rol `ADMIN`. |

Las semillas usan manejo de conflictos para evitar duplicados o normalizar datos existentes. Las semillas de catalogos usan `ON CONFLICT DO NOTHING`; la semilla del administrador usa `ON CONFLICT (email) DO UPDATE`.

## Usuario administrador inicial

La migracion `V45__seed_admin_user.sql` crea el usuario administrativo de desarrollo:

| Campo | Valor |
| --- | --- |
| Nombre | `Administrador AgroDirecto` |
| Email | `admin@agrodirecto.local` |
| Password | `123456` |
| Password almacenado | Hash BCrypt |
| Telefono | `00000000` |
| Estado | `VERIFIED` |
| Email verificado | `true` |
| Telefono verificado | `true` |
| Rol principal | `ADMIN` |

La migracion usa `ON CONFLICT (email) DO UPDATE`, por lo que si el usuario ya existe lo actualiza con la informacion administrativa esperada. Antes de asignar el rol `ADMIN` como principal, marca como no principales otros roles que pudiera tener ese mismo usuario.

## Orden de dependencias

La secuencia evita errores de claves foraneas:

```text
schema
  -> funcion updated_at
  -> catalogos base
  -> users
  -> roles de usuario y tokens
  -> perfiles
  -> documentos
  -> productos
  -> carrito
  -> pedidos
  -> pagos
  -> envios
  -> calificaciones, notificaciones y auditoria
  -> semillas
  -> usuario admin inicial
```

## Indices y triggers

Los indices quedaron junto a su tabla para que cada archivo sea autocontenido. Por ejemplo:

- `V13__create_users_table.sql` crea indices de email y estado, ademas del trigger `trg_users_updated_at`.
- `V21__create_products_table.sql` crea indices por productor, estado, nombre y fecha de creacion, ademas del trigger `trg_products_updated_at`.
- `V32__create_shipments_table.sql` crea indices por pedido, estado, productor y comprador, ademas del trigger `trg_shipments_updated_at`.

Los triggers de actualizacion automatica dependen de `V2__create_updated_at_function.sql`.

## Ejecucion

Flyway ejecuta estas migraciones automaticamente al iniciar Spring Boot porque `application.properties` define:

```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.schemas=${DB_SCHEMA:agro_directo}
spring.flyway.default-schema=${DB_SCHEMA:agro_directo}
```

Con Docker, basta con levantar la base y ejecutar la aplicacion:

```powershell
docker compose up --build
```

O solo la base de datos:

```powershell
docker compose up -d db
.\mvnw.cmd spring-boot:run
```

## Relacion con el script original

El archivo `docs/db/agro-directo-db.txt` queda como referencia monolitica del modelo. La fuente ejecutable para Spring Boot y Flyway ahora es `src/main/resources/db/migration/`.
