CREATE TABLE IF NOT EXISTS agro_directo.users (
    id                          BIGSERIAL PRIMARY KEY,
    full_name                   VARCHAR(150) NOT NULL,
    email                       VARCHAR(150) NOT NULL UNIQUE,
    password_hash               TEXT NOT NULL,
    phone                       VARCHAR(30) NOT NULL,
    status_id                   BIGINT NOT NULL REFERENCES agro_directo.user_statuses(id),
    accepted_terms              BOOLEAN NOT NULL DEFAULT FALSE,
    accepted_privacy_policy     BOOLEAN NOT NULL DEFAULT FALSE,

    email_verified              BOOLEAN NOT NULL DEFAULT FALSE,
    phone_verified              BOOLEAN NOT NULL DEFAULT FALSE,
    failed_login_attempts       INTEGER NOT NULL DEFAULT 0 CHECK (failed_login_attempts >= 0),
    locked_until                TIMESTAMP,
    last_login_at               TIMESTAMP,
    password_changed_at         TIMESTAMP,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id          BIGINT REFERENCES agro_directo.users(id),
    updated_by_user_id          BIGINT REFERENCES agro_directo.users(id)
);

CREATE INDEX IF NOT EXISTS idx_users_email
    ON agro_directo.users(email);

CREATE INDEX IF NOT EXISTS idx_users_status
    ON agro_directo.users(status_id);

CREATE INDEX IF NOT EXISTS idx_users_email_status
    ON agro_directo.users(email, status_id);

DROP TRIGGER IF EXISTS trg_users_updated_at ON agro_directo.users;

CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON agro_directo.users
FOR EACH ROW
EXECUTE FUNCTION agro_directo.set_updated_at();
