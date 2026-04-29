CREATE TABLE IF NOT EXISTS agro_directo.role_permissions (
    id              BIGSERIAL PRIMARY KEY,
    role_id         BIGINT NOT NULL REFERENCES agro_directo.roles(id) ON DELETE CASCADE,
    permission_id   BIGINT NOT NULL REFERENCES agro_directo.permissions(id) ON DELETE CASCADE,
    assigned_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_role_permission UNIQUE (role_id, permission_id)
);

CREATE INDEX IF NOT EXISTS idx_role_permissions_role
    ON agro_directo.role_permissions(role_id);

CREATE INDEX IF NOT EXISTS idx_role_permissions_permission
    ON agro_directo.role_permissions(permission_id);
