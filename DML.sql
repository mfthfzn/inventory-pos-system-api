CREATE DATABASE manajemen_inventaris;
DROP DATABASE manajemen_inventaris;

USE manajemen_inventaris;

INSERT INTO stores (name, address, created_at, updated_at)
VALUES ('Matahari Big Mall', 'Jl. Untung Suropati No.08, Karang Asam Ulu, Kec. Sungai Kunjang, Kota Samarinda, Kalimantan Timur 75243', NOW(), NOW());

INSERT INTO product_targets (name)
VALUES ('Pria Dewasa');

INSERT INTO categories (name)
VALUES ('Kemeja Formal');

INSERT INTO sizes (name, type)
VALUES ('L', 'Atasan');

INSERT INTO colors (name, hex_code)
VALUES ('Navy Blue', '#000080');

INSERT INTO users (email, password, name, role, created_at, updated_at, store_id) VALUES
	('dinpixdinpix@gmail.com', '$2a$12$NMhZsMICE1nR7zL4lYL.mOgkh0Bvckc1zFdik8JGHUQ6VJYaWo3hS', 'Eko Kurniawan Khannedy', 'CASHIER',  NOW(), NOW(), 1),
    ('classroomfauzan@gmail.com', '$2a$12$LwXYiNv6bRvINOIDLQLZ9eTXdwijFU2tl1e5vtsaXqbZLULhxYYk2', 'Sandhika Galih', 'INVENTORY_STAFF',  NOW(), NOW(), 1),
    ('budi@gmail.com', '$2a$12$P9GmKMNR/0gHll0g1qVM1eFMcoJ8QzsIx7NbyZ8P0vWPqCGtCMSVi', 'Budi Nugarah', 'MANAGER',  NOW(), NOW(), 1);

INSERT INTO brands (name)
VALUES ("Uniqlo");

INSERT INTO products (name, price, created_at, updated_at, brand_id, product_target_id, category_id)
VALUES ('Kemeja Oxford Slim Fit', 399000, NOW(), NOW(), 1, 1, 1);

INSERT INTO refresh_tokens (email, token)
VALUES ('eko@gmail.com', 'abc-123-uuid-token-sec');

INSERT INTO product_variants (sku, product_id, size_id, color_id)
VALUES ('UQ-OXFORD-NVY-L', 1, 1, 1);

INSERT INTO stocks (quantity, updated_at, store_id, product_variant_id)
VALUES (50, NOW(), 1, 1);

INSERT INTO transactions (customer_phone_number, customer_name, created_at, email_cashier)
VALUES ('081299887766', 'Siti Aminah', NOW(), 'dika@gmail.com');

INSERT INTO stock_movements (type, quantity, created_at, stock_id, email_inventory)
VALUES ('IN', 50, NOW(), 1, 'dika@gmail.com');

INSERT INTO transaction_items (current_price, quantity, transaction_id, product_variant_id)
VALUES (399000, 2, 1, 1);