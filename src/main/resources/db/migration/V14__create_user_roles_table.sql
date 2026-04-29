CREATE TABLE IF NOT EXISTS agro_directo.user_roles (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES agro_directo.users(id) ON DELETE CASCADE,
    role_id         BIGINT NOT NULL REFERENCES agro_directo.roles(id),
    is_primary      BOOLEAN NOT NULL DEFAULT FALSE,
    assigned_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_role UNIQUE (user_id, role_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_user_primary_role
    ON agro_directo.user_roles(user_id)
    WHERE is_primary = TRUE;
