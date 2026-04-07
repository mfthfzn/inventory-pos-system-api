CREATE DATABASE manajemen_inventaris;
DROP DATABASE manajemen_inventaris;

USE manajemen_inventaris;

CREATE TABLE stores (
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
) Engine innodb;

CREATE TABLE users (
	email VARCHAR(255) NOT NULL,
    password VARCHAR(255)NOT NULL,
    name VARCHAR(100) NOT NULL,
    role ENUM('CASHIER', 'INVENTORY_STAFF', 'MANAGER') NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    store_id INT NOT NULL,
    CONSTRAINT fk_users_store 
		FOREIGN KEY(store_id) 
		REFERENCES stores(id),
    PRIMARY KEY(email)
) ENGINE innodb;

CREATE TABLE refresh_tokens (
    email VARCHAR(255) NOT NULL,
    token VARCHAR(1000) NOT NULL,
    PRIMARY KEY(email),
    CONSTRAINT fk_token_sessions_user
        FOREIGN KEY (email) 
        REFERENCES users(email)
) ENGINE=InnoDB;

CREATE TABLE reset_password_tokens (
    email VARCHAR(255) NOT NULL,
    token VARCHAR(36) NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    PRIMARY KEY(email),
    CONSTRAINT fk_reset_password_tokens_user
        FOREIGN KEY (email) 
        REFERENCES users(email)
) ENGINE=InnoDB;

CREATE TABLE brands (
	id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY(id)
) Engine innodb;

CREATE TABLE product_targets (
	id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(25) NOT NULL,
    PRIMARY KEY(id)
) Engine innodb;

CREATE TABLE categories (
	id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY(id)
) Engine innodb;

CREATE TABLE products (
	id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
	created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    brand_id INT NOT NULL,
    product_target_id INT NOT NULL,
    category_id INT NOT NULL,
    CONSTRAINT fk_products_brand
		FOREIGN KEY(brand_id) REFERENCES brands(id),
    CONSTRAINT fk_products_product_target
		FOREIGN KEY(product_target_id) REFERENCES product_targets(id),
    CONSTRAINT fk_products_category
		FOREIGN KEY(category_id) REFERENCES categories(id),
    PRIMARY KEY(id)
) Engine innodb;

CREATE TABLE sizes (
	id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(7) NOT NULL,
    type VARCHAR(50) NOT NULL,
    PRIMARY KEY(id)
) Engine innodb;

CREATE TABLE colors (
	id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(25) NOT NULL,
    hex_code VARCHAR(8) NOT NULL,
    PRIMARY KEY(id)
) Engine innodb;

CREATE TABLE product_variants (
	id INT NOT NULL AUTO_INCREMENT,
    sku VARCHAR(16) NOT NULL,
    product_id INT NOT NULL,
    size_id INT NOT NULL,
    color_id INT NOT NULL,
    CONSTRAINT fk_product_variants_product
		FOREIGN KEY(product_id) REFERENCES products(id),
	CONSTRAINT fk_product_variants_size
		FOREIGN KEY(size_id) REFERENCES sizes(id),
	CONSTRAINT fk_products_variants_color
		FOREIGN KEY(color_id) REFERENCES colors(id),
    PRIMARY KEY(id)
) Engine innodb;

CREATE TABLE stocks (
	id INT NOT NULL AUTO_INCREMENT,
    quantity INT NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    store_id INT NOT NULL,
    product_variant_id INT NOT NULL,
    CONSTRAINT fk_stocks_store
		FOREIGN KEY(store_id) REFERENCES stores(id),
    CONSTRAINT fk_stocks_product_variant
		FOREIGN KEY(product_variant_id) REFERENCES product_variants(id),
    PRIMARY KEY(id)
) Engine innodb;

CREATE TABLE stock_movements (
	id INT NOT NULL AUTO_INCREMENT,
    type ENUM('IN', 'OUT', 'ADJUSTMENT') NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    stock_id INT NOT NULL,
    email_inventory VARCHAR(255) NOT NULL,
    CONSTRAINT fk_stock_movements_user
		FOREIGN KEY(email_inventory) REFERENCES users(email),
    CONSTRAINT fk_stock_movements_stock
		FOREIGN KEY(stock_id) REFERENCES stocks(id),
    PRIMARY KEY(id)
) Engine innodb;

CREATE TABLE transactions (
	id INT NOT NULL AUTO_INCREMENT,
    customer_phone_number VARCHAR(15) NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    email_cashier VARCHAR(255) NOT NULL,
    CONSTRAINT fk_transactions_user
		FOREIGN KEY(email_cashier) REFERENCES users(email),
    PRIMARY KEY(id)
) Engine innodb;

CREATE TABLE transaction_items (
	id INT NOT NULL AUTO_INCREMENT,
    current_price INT NOT NULL,
    quantity INT NOT NULL,
    transaction_id INT NOT NULL,
    product_variant_id INT NOT NULL,
    CONSTRAINT fk_transaction_items_transaction
		FOREIGN KEY(transaction_id) REFERENCES transactions(id),
	CONSTRAINT fk_transaction_items_product_variant
		FOREIGN KEY(product_variant_id) REFERENCES product_variants(id),
    PRIMARY KEY(id)
) Engine innodb;

DESC stores;
DESC users;
DESC refresh_tokens;

SELECT * FROM stores;
SELECT * FROM users;
SELECT * FROM refresh_tokens;
SELECT * FROM reset_password_tokens;