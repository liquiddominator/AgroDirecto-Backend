CREATE TABLE IF NOT EXISTS agro_directo.product_units (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(20) NOT NULL UNIQUE,
    name    VARCHAR(40) NOT NULL
);
