CREATE TABLE IF NOT EXISTS agro_directo.orders (
    id                          BIGSERIAL PRIMARY KEY,
    buyer_user_id               BIGINT NOT NULL REFERENCES agro_directo.users(id),
    status_id                   BIGINT NOT NULL REFERENCES agro_directo.order_statuses(id),
    total_amount                NUMERIC(14,2) NOT NULL DEFAULT 0 CHECK (total_amount >= 0),
    delivery_address            TEXT,
    delivery_zone               VARCHAR(150),
    notes                       TEXT,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id          BIGINT REFERENCES agro_directo.users(id),
    updated_by_user_id          BIGINT REFERENCES agro_directo.users(id)
);

CREATE INDEX IF NOT EXISTS idx_orders_buyer
    ON agro_directo.orders(buyer_user_id);

CREATE INDEX IF NOT EXISTS idx_orders_status
    ON agro_directo.orders(status_id);

CREATE INDEX IF NOT EXISTS idx_orders_buyer_status
    ON agro_directo.orders(buyer_user_id, status_id);

CREATE INDEX IF NOT EXISTS idx_orders_created_at
    ON agro_directo.orders(created_at DESC);

DROP TRIGGER IF EXISTS trg_orders_updated_at ON agro_directo.orders;

CREATE TRIGGER trg_orders_updated_at
BEFORE UPDATE ON agro_directo.orders
FOR EACH ROW
EXECUTE FUNCTION agro_directo.set_updated_at();
