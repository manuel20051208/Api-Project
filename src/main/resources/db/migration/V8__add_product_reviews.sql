-- ========================================================
-- V8: Reseñas de productos
-- Una reseña por cliente y producto, estrellas 1..5.
-- ========================================================

CREATE TABLE IF NOT EXISTS product_reviews (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    stars INTEGER NOT NULL,
    reviews VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_product_reviews_stars CHECK (stars BETWEEN 1 AND 5),
    CONSTRAINT uk_product_reviews_product_client UNIQUE (product_id, client_id),
    CONSTRAINT fk_product_reviews_products
        FOREIGN KEY (product_id)
        REFERENCES products (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_product_reviews_clients
        FOREIGN KEY (client_id)
        REFERENCES clients (id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_product_reviews_product_date
    ON product_reviews (product_id, created_at DESC, id DESC);

CREATE INDEX IF NOT EXISTS idx_product_reviews_client_date
    ON product_reviews (client_id, created_at DESC, id DESC);
