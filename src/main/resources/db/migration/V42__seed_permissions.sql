INSERT INTO agro_directo.permissions (code, name, description) VALUES
    ('AUTH_LOGIN', 'Iniciar sesion', 'Permite iniciar sesion en el sistema'),
    ('AUTH_REFRESH_TOKEN', 'Renovar token', 'Permite renovar el token de acceso'),

    ('USER_READ', 'Consultar usuarios', 'Permite consultar informacion de usuarios'),
    ('USER_CREATE', 'Crear usuarios', 'Permite registrar nuevos usuarios'),
    ('USER_UPDATE', 'Actualizar usuarios', 'Permite actualizar informacion de usuarios'),
    ('USER_DISABLE', 'Deshabilitar usuarios', 'Permite deshabilitar usuarios'),

    ('ROLE_READ', 'Consultar roles', 'Permite consultar roles'),
    ('ROLE_ASSIGN', 'Asignar roles', 'Permite asignar roles a usuarios'),

    ('PRODUCT_READ', 'Consultar productos', 'Permite consultar productos publicados'),
    ('PRODUCT_CREATE', 'Crear productos', 'Permite crear publicaciones de productos'),
    ('PRODUCT_UPDATE', 'Actualizar productos', 'Permite modificar publicaciones de productos'),
    ('PRODUCT_DELETE', 'Eliminar productos', 'Permite eliminar o desactivar publicaciones de productos'),

    ('CART_MANAGE', 'Gestionar carrito', 'Permite agregar, modificar y eliminar productos del carrito'),

    ('ORDER_CREATE', 'Crear pedidos', 'Permite crear pedidos'),
    ('ORDER_READ', 'Consultar pedidos', 'Permite consultar pedidos'),
    ('ORDER_UPDATE_STATUS', 'Actualizar estado de pedido', 'Permite cambiar el estado de un pedido'),
    ('ORDER_CONFIRM_ITEM', 'Confirmar producto de pedido', 'Permite confirmar o rechazar items de pedidos'),

    ('PAYMENT_CREATE', 'Crear pagos', 'Permite generar pagos'),
    ('PAYMENT_READ', 'Consultar pagos', 'Permite consultar pagos'),
    ('PAYMENT_VERIFY', 'Verificar pagos', 'Permite aprobar o rechazar comprobantes de pago'),

    ('SHIPMENT_READ', 'Consultar envios', 'Permite consultar envios'),
    ('SHIPMENT_APPLY', 'Postular a envio', 'Permite a transportistas postular a envios'),
    ('SHIPMENT_ASSIGN', 'Asignar envio', 'Permite asignar transportista a un envio'),
    ('SHIPMENT_UPDATE_STATUS', 'Actualizar estado de envio', 'Permite cambiar el estado logistico de un envio'),
    ('SHIPMENT_CONFIRM_DELIVERY', 'Confirmar entrega', 'Permite confirmar la recepcion de un envio'),

    ('DOCUMENT_UPLOAD', 'Subir documentos', 'Permite subir documentos de verificacion'),
    ('DOCUMENT_REVIEW', 'Revisar documentos', 'Permite aprobar o rechazar documentos de verificacion'),

    ('RATING_CREATE', 'Crear calificacion', 'Permite registrar calificaciones'),
    ('RATING_READ', 'Consultar calificaciones', 'Permite consultar calificaciones'),

    ('NOTIFICATION_READ', 'Consultar notificaciones', 'Permite consultar notificaciones'),
    ('NOTIFICATION_UPDATE', 'Actualizar notificaciones', 'Permite marcar notificaciones como leidas'),

    ('AUDIT_READ', 'Consultar auditoria', 'Permite consultar registros de auditoria'),

    ('ADMIN_DASHBOARD', 'Panel administrativo', 'Permite acceder al panel administrativo')
ON CONFLICT (code) DO NOTHING;
