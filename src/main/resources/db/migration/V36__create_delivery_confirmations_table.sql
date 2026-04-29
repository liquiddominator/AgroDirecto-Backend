CREATE TABLE IF NOT EXISTS agro_directo.delivery_confirmations (
    id                          BIGSERIAL PRIMARY KEY,
    shipment_id                 BIGINT NOT NULL UNIQUE REFERENCES agro_directo.shipments(id) ON DELETE CASCADE,
    confirmed_by_buyer_user_id  BIGINT NOT NULL REFERENCES agro_directo.users(id),
    received_at                 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes                       TEXT
);
