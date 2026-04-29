CREATE TABLE IF NOT EXISTS agro_directo.product_statuses (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(30) NOT NULL UNIQUE,
    name    VARCHAR(60) NOT NULL
);
