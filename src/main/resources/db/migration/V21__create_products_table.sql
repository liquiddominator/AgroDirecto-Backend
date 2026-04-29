CREATE TABLE IF NOT EXISTS agro_directo.products (
    id                          BIGSERIAL PRIMARY KEY,
    producer_user_id            BIGINT NOT NULL REFERENCES agro_directo.users(id),
    name                        VARCHAR(150) NOT NULL,
    description                 TEXT,
    unit_id                     BIGINT NOT NULL REFERENCES agro_directo.product_units(id),
    available_quantity          NUMERIC(12,2) NOT NULL CHECK (available_quantity >= 0),
    unit_price                  NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0),
    is_pre_sale                 BOOLEAN NOT NULL DEFAULT FALSE,
    estimated_available_date    DATE,
    harvest_available_date      DATE,
    status_id                   BIGINT NOT NULL REFERENCES agro_directo.product_statuses(id),

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id          BIGINT REFERENCES agro_directo.users(id),
    updated_by_user_id          BIGINT REFERENCES agro_directo.users(id),

    CONSTRAINT chk_products_presale_date
        CHECK (
            (is_pre_sale = FALSE)
            OR
            (is_pre_sale = TRUE AND estimated_available_date IS NOT NULL)
        )
);

CREATE INDEX IF NOT EXISTS idx_products_producer
    ON agro_directo.products(producer_user_id);

CREATE INDEX IF NOT EXISTS idx_products_status
    ON agro_directo.products(status_id);

CREATE INDEX IF NOT EXISTS idx_products_name
    ON agro_directo.products(name);

CREATE INDEX IF NOT EXISTS idx_products_name_status
    ON agro_directo.products(name, status_id);

CREATE INDEX IF NOT EXISTS idx_products_producer_status
    ON agro_directo.products(producer_user_id, status_id);

CREATE INDEX IF NOT EXISTS idx_products_created_at
    ON agro_directo.products(created_at DESC);

DROP TRIGGER IF EXISTS trg_products_updated_at ON agro_directo.products;

CREATE TRIGGER trg_products_updated_at
BEFORE UPDATE ON agro_directo.products
FOR EACH ROW
EXECUTE FUNCTION agro_directo.set_updated_at();
