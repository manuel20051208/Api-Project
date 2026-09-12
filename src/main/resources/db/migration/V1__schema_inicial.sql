-- Flyway V1: esquema base de ApiProject.
-- Idempotente y NO destructivo: CREATE TABLE/INDEX IF NOT EXISTS, vistas con DROP IF EXISTS + CREATE.
-- Puede correr tanto sobre una BD vacia como sobre una BD ya existente (baseline-on-migrate).

-- ========================================================
-- 1. TABLAS BASE
-- ========================================================

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    phone BIGINT,
    full_name VARCHAR(60),
    email VARCHAR(60) NOT NULL,
    business_name VARCHAR(100),
    profile_photo VARCHAR(255),
    profile_photo_url TEXT,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255),
    email VARCHAR(255),
    username VARCHAR(60),
    password VARCHAR(255),
    phone BIGINT,
    address VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    photo TEXT,
    CONSTRAINT uk_clients_username UNIQUE (username),
    CONSTRAINT uk_clients_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS payment_cards (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    card_holder_name VARCHAR(100),
    brand VARCHAR(40),
    last_four VARCHAR(4) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_payment_cards_last_four
        CHECK (last_four ~ '^[0-9]{4}$'),
    CONSTRAINT fk_payment_cards_clients
        FOREIGN KEY (client_id)
        REFERENCES clients (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL,
    category VARCHAR(255) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    id_users BIGINT,
    CONSTRAINT fk_products_users
        FOREIGN KEY (id_users)
        REFERENCES users (id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS product_image (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255),
    file_path VARCHAR(255),
    url TEXT,
    display_order BIGINT,
    product_id BIGINT,
    user_id BIGINT,
    CONSTRAINT fk_product_image_products
        FOREIGN KEY (product_id)
        REFERENCES products (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_product_image_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE SET NULL,
    CONSTRAINT uk_product_image_product_order UNIQUE (product_id, display_order)
);

CREATE TABLE IF NOT EXISTS sales (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT,
    user_id BIGINT,
    total_amount NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP,
    CONSTRAINT fk_sales_clients
        FOREIGN KEY (client_id)
        REFERENCES clients (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_sales_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS sale_items (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT,
    product_id BIGINT,
    quantity INTEGER,
    client_id BIGINT,
    state VARCHAR(20),
    date TIMESTAMP,
    CONSTRAINT chk_sale_items_state
        CHECK (state IS NULL OR state IN ('COMPLETED', 'HANGING', 'CANCELLED')),
    CONSTRAINT chk_sale_items_quantity
        CHECK (quantity IS NULL OR quantity > 0),
    CONSTRAINT fk_sale_items_sales
        FOREIGN KEY (sale_id)
        REFERENCES sales (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_sale_items_products
        FOREIGN KEY (product_id)
        REFERENCES products (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_sale_items_clients
        FOREIGN KEY (client_id)
        REFERENCES clients (id)
        ON DELETE SET NULL
);

-- ========================================================
-- 2. INDICES (OPTIMIZACION)
-- ========================================================

CREATE INDEX IF NOT EXISTS idx_products_user_id ON products (id_users);
CREATE INDEX IF NOT EXISTS idx_products_active_name ON products (active, name);
CREATE INDEX IF NOT EXISTS idx_products_lower_name ON products (LOWER(name));
CREATE INDEX IF NOT EXISTS idx_products_lower_category ON products (LOWER(category));
CREATE INDEX IF NOT EXISTS idx_product_image_product_order ON product_image (product_id, display_order);
CREATE INDEX IF NOT EXISTS idx_sales_client_id ON sales (client_id);
CREATE INDEX IF NOT EXISTS idx_sales_user_id ON sales (user_id);
CREATE INDEX IF NOT EXISTS idx_sales_created_at ON sales (created_at);
CREATE INDEX IF NOT EXISTS idx_sale_items_sale_id ON sale_items (sale_id);
CREATE INDEX IF NOT EXISTS idx_sale_items_client_date ON sale_items (client_id, date DESC, id DESC);
CREATE INDEX IF NOT EXISTS idx_sale_items_product_date ON sale_items (product_id, date DESC, id DESC);
CREATE INDEX IF NOT EXISTS idx_clients_lower_full_name ON clients (LOWER(full_name));
CREATE INDEX IF NOT EXISTS idx_clients_lower_email ON clients (LOWER(email));
CREATE INDEX IF NOT EXISTS idx_payment_cards_client_active ON payment_cards (client_id, active);

-- ========================================================
-- 3. VISTAS
-- ========================================================

-- Vista de Ventas Detalladas
DROP VIEW IF EXISTS view_of_sales;
CREATE VIEW view_of_sales AS
SELECT
    si.id,
    s.user_id,
    c.full_name AS client_name,
    p.name AS product_name,
    si.quantity,
    (COALESCE(si.quantity, 0)::NUMERIC * COALESCE(p.price, 0)::NUMERIC)::DOUBLE PRECISION AS total_calculated,
    si.state,
    COALESCE(si.date::DATE, s.created_at::DATE) AS date
FROM sale_items si
         LEFT JOIN sales s ON s.id = si.sale_id
         LEFT JOIN clients c ON c.id = COALESCE(si.client_id, s.client_id)
         LEFT JOIN products p ON p.id = si.product_id;

DROP VIEW IF EXISTS view_of_dashboard;
CREATE VIEW view_of_dashboard AS
SELECT
    (s.user_id * 1000000 + EXTRACT(YEAR FROM s.created_at) * 100 + EXTRACT(MONTH FROM s.created_at))::BIGINT AS id,
    s.user_id,
    EXTRACT(MONTH FROM s.created_at)::INTEGER AS month_number,
    EXTRACT(YEAR FROM s.created_at)::INTEGER AS sales_year,
    INITCAP(TRIM(TO_CHAR(MIN(s.created_at), 'TMMonth'))) AS month_name,
    SUM(s.total_amount)::DOUBLE PRECISION AS monthly_total,
    COUNT(DISTINCT p.id)::BIGINT AS number_of_products,
    COUNT(DISTINCT s.client_id)::BIGINT AS count_clients
FROM sales s
         LEFT JOIN users u ON u.id = s.user_id
         LEFT JOIN products p ON p.id_users = u.id
WHERE EXTRACT(YEAR FROM s.created_at) = EXTRACT(YEAR FROM CURRENT_DATE)
GROUP BY s.user_id, EXTRACT(YEAR FROM s.created_at), EXTRACT(MONTH FROM s.created_at)
ORDER BY s.user_id, sales_year, month_number;

DROP VIEW IF EXISTS clients_summary;
CREATE VIEW clients_summary AS
SELECT c.id                                                                               AS id,
       s.user_id                                                                          AS user_id,
       c.full_name                                                                        AS full_name,
       c.email                                                                            AS email,
       COALESCE(sum(si.quantity), 0::bigint)::numeric(38, 0)                              AS total_quantity,
       COALESCE(sum(si.quantity::numeric * p.price::numeric), 0::numeric)::numeric(38, 2) AS total_spent,
       max(COALESCE(si.date, s.created_at))                                               AS latest_sale
FROM clients c
         LEFT JOIN sale_items si ON si.client_id = c.id
         LEFT JOIN sales s ON s.id = si.sale_id
         LEFT JOIN products p ON p.id = si.product_id
GROUP BY c.id, s.user_id, c.full_name, c.email;

DROP VIEW IF EXISTS view_of_client_history;
CREATE VIEW view_of_client_history AS
SELECT
    si.id       AS sale_item_id,
    c.id        AS client_id,
    c.full_name AS client_name,
    c.email     AS client_email,
    c.address   AS client_address,
    s.id        AS sale_id,
    s.user_id   AS user_id,
    p.id        AS product_id,
    p.name      AS product_name,
    p.category  AS product_category,
    si.quantity AS quantity,
    p.price     AS unit_price,
    (COALESCE(si.quantity, 0)::NUMERIC * COALESCE(p.price, 0)::NUMERIC)::DOUBLE PRECISION AS total_amount,
    si.state    AS state,
    COALESCE(si.date, s.created_at) AS occurred_at
FROM sale_items si
         LEFT JOIN sales s   ON s.id = si.sale_id
         LEFT JOIN clients c ON c.id = COALESCE(si.client_id, si.client_id)
         LEFT JOIN products p ON p.id = si.product_id;

-- Vista del reporte de dashboard por usuario (ReportDashboard entity)
DROP VIEW IF EXISTS report_view_dashboard;
CREATE VIEW report_view_dashboard AS
SELECT
    s.user_id,
    c.full_name AS client_name,
    p.name      AS product_name,
    si.quantity,
    (COALESCE(si.quantity, 0)::NUMERIC * COALESCE(p.price, 0)::NUMERIC)::DOUBLE PRECISION AS total_calculated,
    si.state,
    COALESCE(si.date, s.created_at) AS date,
    p.price     AS current_amount
FROM sale_items si
         LEFT JOIN sales s ON s.id = si.sale_id
         LEFT JOIN clients c ON c.id = COALESCE(si.client_id, si.client_id)
         LEFT JOIN products p ON p.id = si.product_id;

-- ========================================================
-- 4. CUPONES (PRODUCTOS NORMALES)
-- ========================================================

CREATE TABLE IF NOT EXISTS cupons (
    id BIGSERIAL PRIMARY KEY,
    cupon_code VARCHAR(15) NOT NULL,
    cupon_date_limit TIMESTAMP NOT NULL,
    discount DOUBLE PRECISION NOT NULL,
    quantity INTEGER,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_cupons_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS product_cupons_applied (
    id BIGSERIAL PRIMARY KEY,
    cupons_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_pca_cupons
        FOREIGN KEY (cupons_id)
        REFERENCES cupons (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_pca_products
        FOREIGN KEY (product_id)
        REFERENCES products (id)
        ON DELETE CASCADE,
    CONSTRAINT uk_product_cupons_applied UNIQUE (cupons_id, product_id)
);

CREATE TABLE IF NOT EXISTS cupons_used_by_clients (
    id BIGSERIAL PRIMARY KEY,
    client_user BIGINT NOT NULL,
    sale_id BIGINT,
    cupon_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cubc_clients
        FOREIGN KEY (client_user)
        REFERENCES clients (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_cubc_sales
        FOREIGN KEY (sale_id)
        REFERENCES sales (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_cubc_cupons
        FOREIGN KEY (cupon_id)
        REFERENCES cupons (id)
        ON DELETE CASCADE
);

-- ========================================================
-- 5. SEGUNDA MANO
-- ========================================================

CREATE TABLE IF NOT EXISTS secondhand_product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL,
    category VARCHAR(255) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    time_of_use TIMESTAMP NOT NULL,
    level_of_secondhand_product BIGINT NOT NULL,
    id_users BIGINT NOT NULL,
    CONSTRAINT fk_sh_products_users
        FOREIGN KEY (id_users)
        REFERENCES users (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS secondhand_product_images (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255),
    file_path VARCHAR(255),
    url TEXT,
    display_order BIGINT,
    product_id BIGINT NOT NULL,
    user_id BIGINT,
    CONSTRAINT fk_sh_images_secondhand_product
        FOREIGN KEY (product_id)
        REFERENCES secondhand_product (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_sh_images_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE SET NULL,
    CONSTRAINT uk_secondhand_product_images_order UNIQUE (product_id, display_order)
);

CREATE TABLE IF NOT EXISTS secondhand_cupons (
    id BIGSERIAL PRIMARY KEY,
    sh_cupon_code VARCHAR(15) NOT NULL,
    cupon_date_limit TIMESTAMP NOT NULL,
    discount DOUBLE PRECISION NOT NULL,
    quantity INTEGER,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_secondhand_cupons_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS secondhand_product_cupons_applied (
    id BIGSERIAL PRIMARY KEY,
    sh_cupons_id BIGINT NOT NULL,
    sh_product_id BIGINT NOT NULL,
    CONSTRAINT fk_spca_secondhand_cupons
        FOREIGN KEY (sh_cupons_id)
        REFERENCES secondhand_cupons (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_spca_secondhand_product
        FOREIGN KEY (sh_product_id)
        REFERENCES secondhand_product (id)
        ON DELETE CASCADE,
    CONSTRAINT uk_secondhand_product_cupons_applied UNIQUE (sh_cupons_id, sh_product_id)
);

CREATE TABLE IF NOT EXISTS sh_sales (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT,
    user_id BIGINT,
    total_amount NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP,
    CONSTRAINT fk_sh_sales_clients
        FOREIGN KEY (client_id)
        REFERENCES clients (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_sh_sales_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS sh_sales_item (
    id BIGSERIAL PRIMARY KEY,
    sh_sale_id BIGINT,
    sh_product_id BIGINT,
    quantity INTEGER,
    client_id BIGINT,
    state VARCHAR(20),
    date TIMESTAMP,
    CONSTRAINT chk_sh_sale_items_state
        CHECK (state IS NULL OR state IN ('COMPLETED', 'HANGING', 'CANCELLED')),
    CONSTRAINT chk_sh_sale_items_quantity
        CHECK (quantity IS NULL OR quantity > 0),
    CONSTRAINT fk_sh_sale_items_sales
        FOREIGN KEY (sh_sale_id)
        REFERENCES sh_sales (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_sh_sale_items_products
        FOREIGN KEY (sh_product_id)
        REFERENCES secondhand_product (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_sh_sale_items_clients
        FOREIGN KEY (client_id)
        REFERENCES clients (id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS sh_cupons_used_by_clients (
    id BIGSERIAL PRIMARY KEY,
    client_user BIGINT NOT NULL,
    sale_id BIGINT,
    cupon_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_shcubc_clients
        FOREIGN KEY (client_user)
        REFERENCES clients (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_shcubc_sh_sales
        FOREIGN KEY (sale_id)
        REFERENCES sh_sales (id)
        ON DELETE SET NULL,
    CONSTRAINT fk_shcubc_secondhand_cupons
        FOREIGN KEY (cupon_id)
        REFERENCES secondhand_cupons (id)
        ON DELETE CASCADE
);

-- ========================================================
-- 6. SERVICIOS OFRECIDOS
-- ========================================================

CREATE TABLE IF NOT EXISTS services_offered (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name_of_service VARCHAR(40) NOT NULL,
    value_of_service DOUBLE PRECISION NOT NULL,
    description_of_service VARCHAR(255) NOT NULL,
    CONSTRAINT fk_services_offered_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS services_cupon (
    id BIGSERIAL PRIMARY KEY,
    service_cupon_code VARCHAR(15) NOT NULL,
    cupon_date_limit TIMESTAMP NOT NULL,
    discount DOUBLE PRECISION NOT NULL,
    quantity INTEGER,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_services_cupon_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

-- ========================================================
-- 7. ASIGNACION DE CUPONES A CLIENTES
-- ========================================================

CREATE TABLE IF NOT EXISTS product_cupon_to_a_client (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    cupon_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_pcac_clients
        FOREIGN KEY (client_id)
        REFERENCES clients (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_pcac_cupons
        FOREIGN KEY (cupon_id)
        REFERENCES cupons (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_pcac_products
        FOREIGN KEY (product_id)
        REFERENCES products (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sh_product_cupo_to_a_client (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    sh_cupons_id BIGINT NOT NULL,
    sh_product_id BIGINT NOT NULL,
    CONSTRAINT fk_spcac_clients
        FOREIGN KEY (client_id)
        REFERENCES clients (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_spcac_secondhand_cupons
        FOREIGN KEY (sh_cupons_id)
        REFERENCES secondhand_cupons (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_spcac_secondhand_product
        FOREIGN KEY (sh_product_id)
        REFERENCES secondhand_product (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS service_cupon_to_a_client (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    service_cupon_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    CONSTRAINT fk_scac_clients
        FOREIGN KEY (client_id)
        REFERENCES clients (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_scac_services_cupon
        FOREIGN KEY (service_cupon_id)
        REFERENCES services_cupon (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_scac_services_offered
        FOREIGN KEY (service_id)
        REFERENCES services_offered (id)
        ON DELETE CASCADE
);

-- ========================================================
-- 8. INDICES NUEVOS
-- ========================================================

CREATE INDEX IF NOT EXISTS idx_cupons_code ON cupons (LOWER(cupon_code));
CREATE INDEX IF NOT EXISTS idx_cupons_user ON cupons (user_id);
CREATE INDEX IF NOT EXISTS idx_product_cupons_applied_product ON product_cupons_applied (product_id);
CREATE INDEX IF NOT EXISTS idx_cupons_used_cupon ON cupons_used_by_clients (cupon_id);
CREATE INDEX IF NOT EXISTS idx_secondhand_product_user_level ON secondhand_product (id_users, level_of_secondhand_product);
CREATE INDEX IF NOT EXISTS idx_secondhand_product_active_name ON secondhand_product (active, name);
CREATE INDEX IF NOT EXISTS idx_secondhand_cupons_code ON secondhand_cupons (LOWER(sh_cupon_code));
CREATE INDEX IF NOT EXISTS idx_secondhand_cupons_user ON secondhand_cupons (user_id);
CREATE INDEX IF NOT EXISTS idx_spca_sh_product ON secondhand_product_cupons_applied (sh_product_id);
CREATE INDEX IF NOT EXISTS idx_sh_sales_client_id ON sh_sales (client_id);
CREATE INDEX IF NOT EXISTS idx_sh_sales_user_id ON sh_sales (user_id);
CREATE INDEX IF NOT EXISTS idx_sh_sales_created_at ON sh_sales (created_at);
CREATE INDEX IF NOT EXISTS idx_sh_sale_items_sale_id ON sh_sales_item (sh_sale_id);
CREATE INDEX IF NOT EXISTS idx_sh_sale_items_client_date ON sh_sales_item (client_id, date DESC, id DESC);
CREATE INDEX IF NOT EXISTS idx_sh_sale_items_product_date ON sh_sales_item (sh_product_id, date DESC, id DESC);
CREATE INDEX IF NOT EXISTS idx_sh_cupons_used_cupon ON sh_cupons_used_by_clients (cupon_id);
CREATE INDEX IF NOT EXISTS idx_services_offered_user_id ON services_offered (user_id);
CREATE INDEX IF NOT EXISTS idx_services_cupon_code ON services_cupon (LOWER(service_cupon_code));
CREATE INDEX IF NOT EXISTS idx_pcac_client ON product_cupon_to_a_client (client_id);
CREATE INDEX IF NOT EXISTS idx_pcac_cupon ON product_cupon_to_a_client (cupon_id);
CREATE INDEX IF NOT EXISTS idx_pcac_product ON product_cupon_to_a_client (product_id);
CREATE INDEX IF NOT EXISTS idx_spcac_client ON sh_product_cupo_to_a_client (client_id);
CREATE INDEX IF NOT EXISTS idx_spcac_cupon ON sh_product_cupo_to_a_client (sh_cupons_id);
CREATE INDEX IF NOT EXISTS idx_spcac_product ON sh_product_cupo_to_a_client (sh_product_id);
CREATE INDEX IF NOT EXISTS idx_scac_client ON service_cupon_to_a_client (client_id);
CREATE INDEX IF NOT EXISTS idx_scac_cupon ON service_cupon_to_a_client (service_cupon_id);
CREATE INDEX IF NOT EXISTS idx_scac_service ON service_cupon_to_a_client (service_id);