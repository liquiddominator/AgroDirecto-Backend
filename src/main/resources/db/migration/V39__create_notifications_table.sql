CREATE TABLE IF NOT EXISTS agro_directo.notifications (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES agro_directo.users(id) ON DELETE CASCADE,
    type            VARCHAR(50) NOT NULL,
    title           VARCHAR(150) NOT NULL,
    message         TEXT NOT NULL,
    is_read         BOOLEAN NOT NULL DEFAULT FALSE,
    payload_json    JSONB,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_read
    ON agro_directo.notifications(user_id, is_read);

CREATE INDEX IF NOT EXISTS idx_notifications_created_at
    ON agro_directo.notifications(created_at DESC);
