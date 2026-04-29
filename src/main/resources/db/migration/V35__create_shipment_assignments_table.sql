CREATE TABLE IF NOT EXISTS agro_directo.shipment_assignments (
    id                  BIGSERIAL PRIMARY KEY,
    shipment_id         BIGINT NOT NULL UNIQUE REFERENCES agro_directo.shipments(id) ON DELETE CASCADE,
    carrier_user_id     BIGINT NOT NULL REFERENCES agro_directo.users(id),
    assigned_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
