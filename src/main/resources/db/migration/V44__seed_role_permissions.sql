INSERT INTO agro_directo.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM agro_directo.roles r
CROSS JOIN agro_directo.permissions p
WHERE r.code = 'ADMIN'
ON CONFLICT (role_id, permission_id) DO NOTHING;

INSERT INTO agro_directo.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM agro_directo.roles r
JOIN agro_directo.permissions p ON p.code IN (
    'AUTH_LOGIN',
    'AUTH_REFRESH_TOKEN',
    'USER_READ',
    'USER_UPDATE',
    'PRODUCT_READ',
    'PRODUCT_CREATE',
    'PRODUCT_UPDATE',
    'PRODUCT_DELETE',
    'ORDER_READ',
    'ORDER_CONFIRM_ITEM',
    'SHIPMENT_READ',
    'SHIPMENT_UPDATE_STATUS',
    'DOCUMENT_UPLOAD',
    'RATING_READ',
    'NOTIFICATION_READ',
    'NOTIFICATION_UPDATE'
)
WHERE r.code = 'PRODUCER'
ON CONFLICT (role_id, permission_id) DO NOTHING;

INSERT INTO agro_directo.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM agro_directo.roles r
JOIN agro_directo.permissions p ON p.code IN (
    'AUTH_LOGIN',
    'AUTH_REFRESH_TOKEN',
    'USER_READ',
    'USER_UPDATE',
    'PRODUCT_READ',
    'CART_MANAGE',
    'ORDER_CREATE',
    'ORDER_READ',
    'PAYMENT_CREATE',
    'PAYMENT_READ',
    'SHIPMENT_READ',
    'SHIPMENT_CONFIRM_DELIVERY',
    'DOCUMENT_UPLOAD',
    'RATING_CREATE',
    'RATING_READ',
    'NOTIFICATION_READ',
    'NOTIFICATION_UPDATE'
)
WHERE r.code = 'BUYER'
ON CONFLICT (role_id, permission_id) DO NOTHING;

INSERT INTO agro_directo.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM agro_directo.roles r
JOIN agro_directo.permissions p ON p.code IN (
    'AUTH_LOGIN',
    'AUTH_REFRESH_TOKEN',
    'USER_READ',
    'USER_UPDATE',
    'SHIPMENT_READ',
    'SHIPMENT_APPLY',
    'SHIPMENT_UPDATE_STATUS',
    'DOCUMENT_UPLOAD',
    'NOTIFICATION_READ',
    'NOTIFICATION_UPDATE'
)
WHERE r.code = 'CARRIER'
ON CONFLICT (role_id, permission_id) DO NOTHING;
