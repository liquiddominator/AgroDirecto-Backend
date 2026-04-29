CREATE TABLE IF NOT EXISTS agro_directo.payment_receipts (
    id                          BIGSERIAL PRIMARY KEY,
    payment_id                  BIGINT NOT NULL REFERENCES agro_directo.payments(id) ON DELETE CASCADE,
    uploaded_by_user_id         BIGINT NOT NULL REFERENCES agro_directo.users(id),
    file_url                    TEXT NOT NULL,
    original_filename           VARCHAR(255),
    uploaded_at                 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verified_by_user_id         BIGINT REFERENCES agro_directo.users(id),
    verification_result         VARCHAR(20) CHECK (verification_result IN ('APPROVED', 'REJECTED')),
    verification_note           TEXT,
    verified_at                 TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_payment_receipts_payment
    ON agro_directo.payment_receipts(payment_id);
