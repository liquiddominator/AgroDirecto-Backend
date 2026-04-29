CREATE TABLE IF NOT EXISTS agro_directo.shipments (
    id                          BIGSERIAL PRIMARY KEY,
    order_id                    BIGINT NOT NULL REFERENCES agro_directo.orders(id) ON DELETE CASCADE,
    producer_user_id            BIGINT NOT NULL REFERENCES agro_directo.users(id),
    buyer_user_id               BIGINT NOT NULL REFERENCES agro_directo.users(id),
    status_id                   BIGINT NOT NULL REFERENCES agro_directo.shipment_statuses(id),
    pickup_latitude             NUMERIC(10,7),
    pickup_longitude            NUMERIC(10,7),
    delivery_latitude           NUMERIC(10,7),
    delivery_longitude          NUMERIC(10,7),
    route_url                   TEXT,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id          BIGINT REFERENCES agro_directo.users(id),
    updated_by_user_id          BIGINT REFERENCES agro_directo.users(id),

    CONSTRAINT chk_ship_pickup_pair
        CHECK (
            (pickup_latitude IS NULL AND pickup_longitude IS NULL)
            OR
            (pickup_latitude IS NOT NULL AND pickup_longitude IS NOT NULL)
        ),
    CONSTRAINT chk_ship_delivery_pair
        CHECK (
            (delivery_latitude IS NULL AND delivery_longitude IS NULL)
            OR
            (delivery_latitude IS NOT NULL AND delivery_longitude IS NOT NULL)
        ),
    CONSTRAINT chk_ship_pickup_lat_range
        CHECK (pickup_latitude IS NULL OR (pickup_latitude >= -90 AND pickup_latitude <= 90)),
    CONSTRAINT chk_ship_pickup_lon_range
        CHECK (pickup_longitude IS NULL OR (pickup_longitude >= -180 AND pickup_longitude <= 180)),
    CONSTRAINT chk_ship_delivery_lat_range
        CHECK (delivery_latitude IS NULL OR (delivery_latitude >= -90 AND delivery_latitude <= 90)),
    CONSTRAINT chk_ship_delivery_lon_range
        CHECK (delivery_longitude IS NULL OR (delivery_longitude >= -180 AND delivery_longitude <= 180))
);

CREATE INDEX IF NOT EXISTS idx_shipments_order
    ON agro_directo.shipments(order_id);

CREATE INDEX IF NOT EXISTS idx_shipments_status
    ON agro_directo.shipments(status_id);

CREATE INDEX IF NOT EXISTS idx_shipments_producer
    ON agro_directo.shipments(producer_user_id);

CREATE INDEX IF NOT EXISTS idx_shipments_buyer
    ON agro_directo.shipments(buyer_user_id);

DROP TRIGGER IF EXISTS trg_shipments_updated_at ON agro_directo.shipments;

CREATE TRIGGER trg_shipments_updated_at
BEFORE UPDATE ON agro_directo.shipments
FOR EACH ROW
EXECUTE FUNCTION agro_directo.set_updated_at();
