CREATE TABLE IF NOT EXISTS agro_directo.shipment_items (
    id              BIGSERIAL PRIMARY KEY,
    shipment_id     BIGINT NOT NULL REFERENCES agro_directo.shipments(id) ON DELETE CASCADE,
    order_item_id   BIGINT NOT NULL UNIQUE REFERENCES agro_directo.order_items(id) ON DELETE CASCADE
);
