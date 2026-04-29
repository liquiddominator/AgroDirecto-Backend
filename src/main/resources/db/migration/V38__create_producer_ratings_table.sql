CREATE TABLE IF NOT EXISTS agro_directo.producer_ratings (
    id                  BIGSERIAL PRIMARY KEY,
    producer_user_id    BIGINT NOT NULL REFERENCES agro_directo.users(id) ON DELETE CASCADE,
    buyer_user_id       BIGINT NOT NULL REFERENCES agro_directo.users(id) ON DELETE CASCADE,
    order_id            BIGINT NOT NULL REFERENCES agro_directo.orders(id) ON DELETE CASCADE,
    score               INTEGER NOT NULL CHECK (score BETWEEN 1 AND 5),
    comment             TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_rating_order UNIQUE (producer_user_id, buyer_user_id, order_id)
);
