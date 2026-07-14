-- V1: 初始化数据库 Schema
-- 使用 H2 数据库语法

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'CONSUMER',
    phone VARCHAR(20) UNIQUE,
    avatar VARCHAR(255),
    address VARCHAR(200),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 商品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100),
    origin VARCHAR(200),
    specifications VARCHAR(50),
    unit VARCHAR(50) DEFAULT '斤',
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    description VARCHAR(2000),
    image_url VARCHAR(500),
    batch_no VARCHAR(50),
    farming_record VARCHAR(2000),
    test_report VARCHAR(2000),
    cert_no VARCHAR(50),
    cert_status VARCHAR(20),
    certification_label VARCHAR(100),
    extra_info TEXT,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    farmer_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    shipping_address VARCHAR(500),
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 订单项表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200),
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 溯源记录表
CREATE TABLE IF NOT EXISTS trace_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trace_code VARCHAR(50) NOT NULL UNIQUE,
    batch_no VARCHAR(50),
    product_name VARCHAR(200),
    origin VARCHAR(200),
    cert_no VARCHAR(50),
    cert_valid BOOLEAN DEFAULT FALSE,
    stages TEXT,
    tx_hash VARCHAR(100),
    scan_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 知识库表
CREATE TABLE IF NOT EXISTS knowledge_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(100),
    source VARCHAR(200),
    embedding TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 生产记录表
CREATE TABLE IF NOT EXISTS production_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    batch_no VARCHAR(50),
    record_type VARCHAR(50),
    content TEXT,
    user_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 合格证表
CREATE TABLE IF NOT EXISTS certificates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cert_no VARCHAR(50) NOT NULL UNIQUE,
    product_name VARCHAR(200),
    batch_no VARCHAR(50),
    producer VARCHAR(200),
    origin VARCHAR(200),
    statement TEXT,
    status VARCHAR(20) DEFAULT 'VALID',
    user_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 会话表
CREATE TABLE IF NOT EXISTS chat_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(100) NOT NULL UNIQUE,
    user_id BIGINT,
    messages TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);