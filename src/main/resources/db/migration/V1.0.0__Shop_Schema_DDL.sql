
-- MySQL-compatible DDL for Shop Management Software with seed data

-- Drop existing tables if needed
DROP TABLE IF EXISTS sale_items, sales, purchase_items, purchases, products, categories, customers, suppliers, expenses, users, gst_reports;

-- Categories
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

INSERT INTO categories (name) VALUES
('Electronics'), ('Groceries'), ('Stationery'), ('Furniture');

-- Products
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100) UNIQUE NOT NULL,
    category_id INT,
    purchase_price DECIMAL(10, 2),
    sale_price DECIMAL(10, 2),
    stock_quantity INT DEFAULT 0,
    tax_rate DECIMAL(5, 2),
    barcode VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

INSERT INTO products (name, sku, category_id, purchase_price, sale_price, stock_quantity, tax_rate, barcode)
VALUES
('Wireless Mouse', 'ELEC001', 1, 250.00, 400.00, 50, 18.00, '123456789012'),
('Notebook Pack', 'STAT001', 3, 100.00, 150.00, 100, 12.00, '123456789013');

-- Customers
CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    gstin VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO customers (name, phone, email, gstin)
VALUES
('Ravi Kumar', '9876543210', 'ravi@example.com', '29ABCDE1234F2Z5');

-- Suppliers
CREATE TABLE suppliers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    address TEXT,
    gstin VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO suppliers (name, phone, email, address, gstin)
VALUES
('ABC Distributors', '9123456780', 'abc@distributors.com', 'Bangalore', '29AACCA1234A1Z5');

-- Sales
CREATE TABLE sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2),
    discount DECIMAL(10, 2),
    tax_amount DECIMAL(10, 2),
    payment_method VARCHAR(50),
    status VARCHAR(50),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Sale items
CREATE TABLE sale_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT,
    product_id INT,
    quantity INT,
    price DECIMAL(10, 2),
    tax_rate DECIMAL(5, 2),
    FOREIGN KEY (sale_id) REFERENCES sales(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Purchases
CREATE TABLE purchases (
    id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id INT,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2),
    tax_amount DECIMAL(10, 2),
    status VARCHAR(50),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

-- Purchase items
CREATE TABLE purchase_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    purchase_id INT,
    product_id INT,
    quantity INT,
    price DECIMAL(10, 2),
    tax_rate DECIMAL(5, 2),
    FOREIGN KEY (purchase_id) REFERENCES purchases(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Expenses
CREATE TABLE expenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(100),
    description TEXT,
    amount DECIMAL(10, 2),
    gst_applicable BOOLEAN,
    date DATE,
    attachment_url VARCHAR(255)
);

INSERT INTO expenses (category, description, amount, gst_applicable, date)
VALUES
('Furniture', 'Office Chair', 4500.00, TRUE, '2025-04-01');

-- Users
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    role VARCHAR(50),
    status VARCHAR(20) DEFAULT 'active'
);

INSERT INTO users (username, password_hash, name, role)
VALUES
('admin', 'hashed_password', 'Admin User', 'admin');

-- GST Reports
CREATE TABLE gst_reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    report_type VARCHAR(50),
    period_start DATE,
    period_end DATE,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_sales DECIMAL(10, 2),
    total_purchases DECIMAL(10, 2),
    total_tax_collected DECIMAL(10, 2),
    total_itc_claimed DECIMAL(10, 2)
);
