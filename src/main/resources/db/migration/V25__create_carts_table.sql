CREATE TABLE IF NOT EXISTS agro_directo.carts (
    id                          BIGSERIAL PRIMARY KEY,
    buyer_user_id               BIGINT NOT NULL REFERENCES agro_directo.users(id) ON DELETE CASCADE,
    status                      VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'ABANDONED', 'CONVERTED')),
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TRIGGER IF EXISTS trg_carts_updated_at ON agro_directo.carts;

CREATE TRIGGER trg_carts_updated_at
BEFORE UPDATE ON agro_directo.carts
FOR EACH ROW
EXECUTE FUNCTION agro_directo.set_updated_at();
