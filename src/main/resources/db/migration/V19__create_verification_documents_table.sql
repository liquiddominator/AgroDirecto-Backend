CREATE TABLE IF NOT EXISTS agro_directo.verification_documents (
    id                          BIGSERIAL PRIMARY KEY,
    user_id                     BIGINT NOT NULL REFERENCES agro_directo.users(id) ON DELETE CASCADE,
    document_type_id            BIGINT NOT NULL REFERENCES agro_directo.document_types(id),
    document_number             VARCHAR(80),
    file_url                    TEXT NOT NULL,
    original_filename           VARCHAR(255),
    mime_type                   VARCHAR(100),
    uploaded_at                 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_verification_documents_user
    ON agro_directo.verification_documents(user_id);
