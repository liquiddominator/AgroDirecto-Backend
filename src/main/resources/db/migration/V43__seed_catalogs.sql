INSERT INTO agro_directo.user_statuses (code, name) VALUES
    ('REGISTERED', 'Registrado'),
    ('PENDING_VERIFICATION', 'Pendiente de Verificacion'),
    ('VERIFIED', 'Verificado'),
    ('REJECTED', 'Rechazado'),
    ('INCOMPLETE', 'Incompleto'),
    ('SUSPENDED', 'Suspendido'),
    ('LOCKED', 'Bloqueado')
ON CONFLICT (code) DO NOTHING;

INSERT INTO agro_directo.document_types (code, name) VALUES
    ('CI', 'Carnet de Identidad'),
    ('PRODUCER_REGISTRY', 'Registro de Productor'),
    ('COMMUNITY_CERTIFICATE', 'Certificado Comunitario'),
    ('DRIVER_LICENSE', 'Licencia de Conducir'),
    ('SOAT', 'SOAT'),
    ('VEHICLE_REGISTRY', 'Registro de Vehiculo')
ON CONFLICT (code) DO NOTHING;

INSERT INTO agro_directo.product_units (code, name) VALUES
    ('QUINTAL', 'Quintal'),
    ('ARROBA', 'Arroba'),
    ('KG', 'Kilogramo'),
    ('UNIT', 'Unidad')
ON CONFLICT (code) DO NOTHING;

INSERT INTO agro_directo.product_statuses (code, name) VALUES
    ('DRAFT', 'Borrador'),
    ('PUBLISHED', 'Publicado'),
    ('RESERVED', 'Reservado'),
    ('SOLD_OUT', 'Agotado'),
    ('INACTIVE', 'Inactivo')
ON CONFLICT (code) DO NOTHING;

INSERT INTO agro_directo.order_statuses (code, name) VALUES
    ('CREATED', 'Creado'),
    ('PENDING_CONFIRMATION', 'Pendiente de Confirmacion'),
    ('CONFIRMED', 'Confirmado'),
    ('PARTIALLY_CONFIRMED', 'Parcialmente Confirmado'),
    ('PAID', 'Pagado'),
    ('IN_TRANSIT', 'En Transito'),
    ('DELIVERED', 'Entregado'),
    ('CANCELLED', 'Cancelado')
ON CONFLICT (code) DO NOTHING;

INSERT INTO agro_directo.payment_statuses (code, name) VALUES
    ('PENDING', 'Pendiente'),
    ('QR_GENERATED', 'QR Generado'),
    ('RECEIPT_UPLOADED', 'Comprobante Subido'),
    ('VERIFIED', 'Verificado'),
    ('REJECTED', 'Rechazado')
ON CONFLICT (code) DO NOTHING;

INSERT INTO agro_directo.shipment_statuses (code, name) VALUES
    ('OPEN', 'Abierto'),
    ('APPLIED', 'Postulado'),
    ('ASSIGNED', 'Asignado'),
    ('PICKED_UP', 'Recogido'),
    ('IN_TRANSIT', 'En Transito'),
    ('DELIVERED', 'Entregado'),
    ('CLOSED', 'Cerrado'),
    ('CANCELLED', 'Cancelado')
ON CONFLICT (code) DO NOTHING;
