INSERT INTO agro_directo.roles (code, name) VALUES
    ('PRODUCER', 'Productor'),
    ('BUYER', 'Comprador'),
    ('CARRIER', 'Transportista'),
    ('ADMIN', 'Administrador')
ON CONFLICT (code) DO NOTHING;