CREATE TABLE IF NOT EXISTS agro_directo.permissions (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(80) NOT NULL UNIQUE,
    name            VARCHAR(120) NOT NULL,
    description     TEXT
);
