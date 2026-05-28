-- ========== ECommerceApi Database Setup Script ==========
-- 
-- This script creates the ecommerce_db database and its user.
-- Run this script with MySQL admin credentials before starting the Spring Boot application.
-- 
-- Example:
--   mysql -u root -p < db_setup.sql
--

-- Create the database
CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

-- Create the user (if needed)
-- GRANT ALL PRIVILEGES ON ecommerce_db.* TO 'ecommerce_user'@'localhost' IDENTIFIED BY 'ecommerce_password';
-- FLUSH PRIVILEGES;

-- ========== Table Creation (handled by Hibernate) ==========
-- 
-- Hibernate will automatically create the following tables when the application starts:
-- - categories
-- - products
-- - orders
-- - order_items
--
-- With relationships:
-- - products.category_id -> categories.id (foreign key)
-- - order_items.order_id -> orders.id (foreign key)
-- - order_items.product_id -> products.id (foreign key)
--

-- ========== Verify Database Creation ==========
SHOW DATABASES;
SELECT VERSION();
