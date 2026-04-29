CREATE TABLE IF NOT EXISTS agro_directo.signed_receipts (
    id                  BIGSERIAL PRIMARY KEY,
    shipment_id         BIGINT NOT NULL UNIQUE REFERENCES agro_directo.shipments(id) ON DELETE CASCADE,
    signed_by_name      VARCHAR(150) NOT NULL,
    signature_image_url TEXT NOT NULL,
    signed_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
