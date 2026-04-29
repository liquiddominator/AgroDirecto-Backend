CREATE TABLE IF NOT EXISTS agro_directo.product_images (
    id                          BIGSERIAL PRIMARY KEY,
    product_id                  BIGINT NOT NULL REFERENCES agro_directo.products(id) ON DELETE CASCADE,
    image_url                   TEXT NOT NULL,
    sort_order                  INTEGER NOT NULL DEFAULT 0,
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
