CREATE TABLE IF NOT EXISTS agro_directo.order_items (
    id                          BIGSERIAL PRIMARY KEY,
    order_id                    BIGINT NOT NULL REFERENCES agro_directo.orders(id) ON DELETE CASCADE,
    product_id                  BIGINT NOT NULL REFERENCES agro_directo.products(id),
    producer_user_id            BIGINT NOT NULL REFERENCES agro_directo.users(id),
    quantity                    NUMERIC(12,2) NOT NULL CHECK (quantity > 0),
    unit_price                  NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0),
    subtotal                    NUMERIC(14,2) NOT NULL CHECK (subtotal >= 0),
    confirmation_status         VARCHAR(30) NOT NULL DEFAULT 'PENDING'
                                CHECK (confirmation_status IN ('PENDING', 'CONFIRMED', 'REJECTED')),
    confirmed_at                TIMESTAMP,
    CONSTRAINT uq_order_product UNIQUE (order_id, product_id)
);

CREATE INDEX IF NOT EXISTS idx_order_items_order
    ON agro_directo.order_items(order_id);

CREATE INDEX IF NOT EXISTS idx_order_items_producer
    ON agro_directo.order_items(producer_user_id);
