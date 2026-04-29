CREATE TABLE IF NOT EXISTS agro_directo.refresh_tokens (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES agro_directo.users(id) ON DELETE CASCADE,
    token_hash      TEXT NOT NULL UNIQUE,
    issued_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMP NOT NULL,
    revoked_at      TIMESTAMP,
    replaced_by_id  BIGINT REFERENCES agro_directo.refresh_tokens(id),
    user_agent      TEXT,
    ip_address      VARCHAR(45),
    CONSTRAINT chk_refresh_token_dates
        CHECK (expires_at > issued_at),
    CONSTRAINT chk_refresh_token_revoked
        CHECK (revoked_at IS NULL OR revoked_at >= issued_at)
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user
    ON agro_directo.refresh_tokens(user_id);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at
    ON agro_directo.refresh_tokens(expires_at);
