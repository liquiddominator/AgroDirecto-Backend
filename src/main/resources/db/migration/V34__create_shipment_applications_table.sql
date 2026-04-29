CREATE TABLE IF NOT EXISTS agro_directo.shipment_applications (
    id                      BIGSERIAL PRIMARY KEY,
    shipment_id             BIGINT NOT NULL REFERENCES agro_directo.shipments(id) ON DELETE CASCADE,
    carrier_user_id         BIGINT NOT NULL REFERENCES agro_directo.users(id),
    application_status      VARCHAR(20) NOT NULL DEFAULT 'APPLIED'
                            CHECK (application_status IN ('APPLIED', 'ACCEPTED', 'REJECTED')),
    applied_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_shipment_carrier_application UNIQUE (shipment_id, carrier_user_id)
);
