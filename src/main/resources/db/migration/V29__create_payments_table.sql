CREATE TABLE IF NOT EXISTS agro_directo.payments (
    id                          BIGSERIAL PRIMARY KEY,
    order_id                    BIGINT NOT NULL UNIQUE REFERENCES agro_directo.orders(id) ON DELETE CASCADE,
    status_id                   BIGINT NOT NULL REFERENCES agro_directo.payment_statuses(id),
    payment_method              VARCHAR(30) NOT NULL CHECK (payment_method IN ('QR_TRANSFER')),
    amount                      NUMERIC(14,2) NOT NULL CHECK (amount >= 0),

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id          BIGINT REFERENCES agro_directo.users(id),
    updated_by_user_id          BIGINT REFERENCES agro_directo.users(id)
);

DROP TRIGGER IF EXISTS trg_payments_updated_at ON agro_directo.payments;

CREATE TRIGGER trg_payments_updated_at
BEFORE UPDATE ON agro_directo.payments
FOR EACH ROW
EXECUTE FUNCTION agro_directo.set_updated_at();
