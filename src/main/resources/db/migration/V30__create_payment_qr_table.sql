CREATE TABLE IF NOT EXISTS agro_directo.payment_qr (
    id              BIGSERIAL PRIMARY KEY,
    payment_id      BIGINT NOT NULL UNIQUE REFERENCES agro_directo.payments(id) ON DELETE CASCADE,
    qr_code_text    TEXT NOT NULL,
    qr_image_url    TEXT,
    generated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMP
);
