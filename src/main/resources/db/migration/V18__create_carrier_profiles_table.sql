CREATE TABLE IF NOT EXISTS agro_directo.carrier_profiles (
    user_id                      BIGINT PRIMARY KEY REFERENCES agro_directo.users(id) ON DELETE CASCADE,
    transport_type               VARCHAR(30) NOT NULL CHECK (transport_type IN ('TRUCK', 'PICKUP', 'MOTORBIKE', 'OTHER')),
    load_capacity_kg             NUMERIC(12,2) NOT NULL CHECK (load_capacity_kg > 0),
    operation_zone               VARCHAR(50) NOT NULL CHECK (operation_zone IN ('LOCAL', 'REGIONAL', 'DEPARTMENTAL')),
    driver_license_number        VARCHAR(50) NOT NULL,
    vehicle_plate                VARCHAR(20) NOT NULL,

    created_at                   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id           BIGINT REFERENCES agro_directo.users(id),
    updated_by_user_id           BIGINT REFERENCES agro_directo.users(id),

    CONSTRAINT uq_carrier_license UNIQUE (driver_license_number),
    CONSTRAINT uq_carrier_plate UNIQUE (vehicle_plate)
);

DROP TRIGGER IF EXISTS trg_carrier_profiles_updated_at ON agro_directo.carrier_profiles;

CREATE TRIGGER trg_carrier_profiles_updated_at
BEFORE UPDATE ON agro_directo.carrier_profiles
FOR EACH ROW
EXECUTE FUNCTION agro_directo.set_updated_at();
