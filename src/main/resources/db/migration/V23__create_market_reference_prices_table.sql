CREATE TABLE IF NOT EXISTS agro_directo.market_reference_prices (
    id                          BIGSERIAL PRIMARY KEY,
    product_name                VARCHAR(150) NOT NULL,
    market_name                 VARCHAR(120) NOT NULL DEFAULT 'Mercado Abasto',
    reference_unit              VARCHAR(40) NOT NULL,
    reference_price             NUMERIC(12,2) NOT NULL CHECK (reference_price >= 0),
    observed_on                 DATE NOT NULL,
    source_note                 TEXT,
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_market_reference_prices_name_date
    ON agro_directo.market_reference_prices(product_name, observed_on DESC);
