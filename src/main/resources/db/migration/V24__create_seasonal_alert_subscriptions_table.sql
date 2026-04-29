CREATE TABLE IF NOT EXISTS agro_directo.seasonal_alert_subscriptions (
    id                          BIGSERIAL PRIMARY KEY,
    buyer_user_id               BIGINT NOT NULL REFERENCES agro_directo.users(id) ON DELETE CASCADE,
    product_name                VARCHAR(150) NOT NULL,
    is_active                   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_buyer_product_alert UNIQUE (buyer_user_id, product_name)
);
