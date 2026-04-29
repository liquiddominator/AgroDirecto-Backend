CREATE TABLE IF NOT EXISTS agro_directo.producer_profiles (
    user_id                      BIGINT PRIMARY KEY REFERENCES agro_directo.users(id) ON DELETE CASCADE,
    producer_type                VARCHAR(30) NOT NULL CHECK (producer_type IN ('INDIVIDUAL', 'ASSOCIATION', 'COOPERATIVE')),
    farm_name                    VARCHAR(150) NOT NULL,
    municipality                 VARCHAR(100) NOT NULL,
    province                     VARCHAR(100) NOT NULL,
    department                   VARCHAR(100) NOT NULL,
    experience_years             INTEGER NOT NULL CHECK (experience_years >= 0),
    geo_latitude                 NUMERIC(10,7),
    geo_longitude                NUMERIC(10,7),
    geolocation_completed        BOOLEAN NOT NULL DEFAULT FALSE,
    trust_score                  NUMERIC(3,2) CHECK (trust_score IS NULL OR (trust_score >= 0 AND trust_score <= 5)),

    created_at                   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id           BIGINT REFERENCES agro_directo.users(id),
    updated_by_user_id           BIGINT REFERENCES agro_directo.users(id),

    CONSTRAINT chk_producer_geo_pair
        CHECK (
            (geo_latitude IS NULL AND geo_longitude IS NULL)
            OR
            (geo_latitude IS NOT NULL AND geo_longitude IS NOT NULL)
        ),
    CONSTRAINT chk_producer_lat_range
        CHECK (geo_latitude IS NULL OR (geo_latitude >= -90 AND geo_latitude <= 90)),
    CONSTRAINT chk_producer_lon_range
        CHECK (geo_longitude IS NULL OR (geo_longitude >= -180 AND geo_longitude <= 180))
);

CREATE INDEX IF NOT EXISTS idx_producer_profiles_geo_lat
    ON agro_directo.producer_profiles(geo_latitude);

CREATE INDEX IF NOT EXISTS idx_producer_profiles_geo_lon
    ON agro_directo.producer_profiles(geo_longitude);

DROP TRIGGER IF EXISTS trg_producer_profiles_updated_at ON agro_directo.producer_profiles;

CREATE TRIGGER trg_producer_profiles_updated_at
BEFORE UPDATE ON agro_directo.producer_profiles
FOR EACH ROW
EXECUTE FUNCTION agro_directo.set_updated_at();
