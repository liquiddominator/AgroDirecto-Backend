CREATE TABLE IF NOT EXISTS agro_directo.cart_items (
    id                          BIGSERIAL PRIMARY KEY,
    cart_id                     BIGINT NOT NULL REFERENCES agro_directo.carts(id) ON DELETE CASCADE,
    product_id                  BIGINT NOT NULL REFERENCES agro_directo.products(id),
    quantity                    NUMERIC(12,2) NOT NULL CHECK (quantity > 0),
    unit_price_snapshot         NUMERIC(12,2) NOT NULL CHECK (unit_price_snapshot >= 0),
    added_at                    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_cart_product UNIQUE (cart_id, product_id)
);
