CREATE TABLE IF NOT EXISTS agro_directo.user_statuses (
    id      BIGSERIAL PRIMARY KEY,
    code    VARCHAR(40) NOT NULL UNIQUE,
    name    VARCHAR(80) NOT NULL
);
