DROP DATABASE IF EXISTS healthfirst_pims;
CREATE DATABASE healthfirst_pims;
USE healthfirst_pims;
CREATE TABLE users(
user_id INT PRIMARY KEY AUTO_INCREMENT,
username VARCHAR (50) UNIQUE,
password VARCHAR (255),
role ENUM('Admin', 'Cashier'),
full_name VARCHAR (100)
);

-- Suppliers Table
CREATE TABLE suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    address TEXT NOT NULL
);

-- Medicine Table
CREATE TABLE medicines (
    medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    company VARCHAR(100) NOT NULL,
    medicine_type VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity_in_stock INT NOT NULL,
    reorder_level INT NOT NULL,
    expiry_date DATE NOT NULL,
    supplier_id INT NOT NULL,
    CONSTRAINT fk_medicines_suppliers FOREIGN KEY (supplier_id) 
        REFERENCES suppliers(supplier_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- Sales Table
CREATE TABLE sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT fk_sales_users FOREIGN KEY (user_id) 
        REFERENCES users(user_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- Sale Items Table
CREATE TABLE sale_items (
    sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity_sold INT NOT NULL,
    price_at_sale DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_sale_items_sales FOREIGN KEY (sale_id) 
        REFERENCES sales(sale_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT fk_sale_items_medicines FOREIGN KEY (medicine_id) 
        REFERENCES medicines(medicine_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

-- Default Users
INSERT INTO users (username, password, role, full_name) VALUES
('admin', 'admin123', 'Admin', 'System Administrator'),
('cashier', 'cash123', 'Cashier', 'Luvuyo Tshabalala');

-- Suppliers (Samples)
INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
('PharmaCorp SA', 'Sarah Nkosi', '+27 11 555 0101', 'orders@pharmacorp.co.za', '12 Industry Road, Johannesburg'),
('MediSupply Ltd', 'David Ndlovu', '+27 21 555 0202', 'info@medisupply.co.za', '45 Logistics Park, Cape Town'),
('BioHealth Distribution', 'Angel Patel', '+27 31 555 0303', 'sales@biohealth.co.za', '88 Coast Way, Durban');

-- Medicine (Samples)
INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES
('Paracetamol 500mg', 'Adcock Ingram', 'Tablet', 45.00, 150, 30, DATE_ADD(CURDATE(), INTERVAL 12 MONTH), 1),
('Amoxicillin 250mg', 'Aspen Pharmacare', 'Capsule', 120.50, 80, 25, DATE_ADD(CURDATE(), INTERVAL 8 MONTH), 1),
('Benylin Cough Syrup', 'Johnson & Johnson', 'Syrup', 85.00, 12, 20, DATE_ADD(CURDATE(), INTERVAL 6 MONTH), 2),
('Cataflam D 50mg', 'Novartis', 'Tablet', 95.75, 40, 15, DATE_ADD(CURDATE(), INTERVAL 20 DAY), 2),     
('Betadine Ointment', 'Mundipharma', 'Cream', 65.00, 5, 10, DATE_ADD(CURDATE(), INTERVAL 15 DAY), 3);



