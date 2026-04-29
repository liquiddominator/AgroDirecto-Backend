CREATE TABLE IF NOT EXISTS agro_directo.verification_reviews (
    id                          BIGSERIAL PRIMARY KEY,
    document_id                 BIGINT NOT NULL REFERENCES agro_directo.verification_documents(id) ON DELETE CASCADE,
    reviewed_by_user_id         BIGINT NOT NULL REFERENCES agro_directo.users(id),
    decision                    VARCHAR(20) NOT NULL CHECK (decision IN ('APPROVED', 'REJECTED')),
    reason                      TEXT,
    evidence_url                TEXT,
    reviewed_at                 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
