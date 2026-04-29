CREATE TABLE IF NOT EXISTS agro_directo.audit_log (
    id              BIGSERIAL PRIMARY KEY,
    actor_user_id   BIGINT REFERENCES agro_directo.users(id),
    entity_name     VARCHAR(100) NOT NULL,
    entity_id       BIGINT NOT NULL,
    action          VARCHAR(50) NOT NULL,
    old_values      JSONB,
    new_values      JSONB,
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_log_actor
    ON agro_directo.audit_log(actor_user_id);

CREATE INDEX IF NOT EXISTS idx_audit_log_entity
    ON agro_directo.audit_log(entity_name, entity_id);

CREATE INDEX IF NOT EXISTS idx_audit_log_created_at
    ON agro_directo.audit_log(created_at DESC);
