CREATE TABLE IF NOT EXISTS agro_directo.document_types (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(40) NOT NULL UNIQUE,
    name    VARCHAR(100) NOT NULL
);
