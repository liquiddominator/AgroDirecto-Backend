CREATE TABLE IF NOT EXISTS agro_directo.buyer_profiles (
    user_id                      BIGINT PRIMARY KEY REFERENCES agro_directo.users(id) ON DELETE CASCADE,
    buyer_type                   VARCHAR(30) NOT NULL CHECK (buyer_type IN ('INDIVIDUAL', 'BUSINESS', 'COMPANY')),
    business_name                VARCHAR(150),
    city_or_purchase_zone        VARCHAR(150) NOT NULL,

    created_at                   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id           BIGINT REFERENCES agro_directo.users(id),
    updated_by_user_id           BIGINT REFERENCES agro_directo.users(id)
);

DROP TRIGGER IF EXISTS trg_buyer_profiles_updated_at ON agro_directo.buyer_profiles;

CREATE TRIGGER trg_buyer_profiles_updated_at
BEFORE UPDATE ON agro_directo.buyer_profiles
FOR EACH ROW
EXECUTE FUNCTION agro_directo.set_updated_at();
