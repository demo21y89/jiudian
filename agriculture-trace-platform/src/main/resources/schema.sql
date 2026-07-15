-- ============================================
-- 农产品溯源智能交易平台 - 数据库初始化脚本
-- SQLite 版本
-- ============================================

-- 商品表
CREATE TABLE IF NOT EXISTS product (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(20) NOT NULL,
    origin VARCHAR(100),
    spec VARCHAR(50),
    price DECIMAL(10,2) NOT NULL DEFAULT 0,
    stock INTEGER NOT NULL DEFAULT 0,
    batch_no VARCHAR(50),
    image_url VARCHAR(255),
    description TEXT,
    trace_level VARCHAR(20),
    deleted INTEGER DEFAULT 0,
    status INTEGER DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 批次表
CREATE TABLE IF NOT EXISTS batch (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    batch_no VARCHAR(50) NOT NULL UNIQUE,
    product_id INTEGER,
    produce_date DATE,
    harvest_date DATE,
    quantity INTEGER,
    farm_address VARCHAR(200),
    farm_area VARCHAR(50),
    soil_type VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 溯源记录表
CREATE TABLE IF NOT EXISTS trace_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    batch_id INTEGER NOT NULL,
    record_type VARCHAR(20) NOT NULL,
    record_time DATETIME,
    operator VARCHAR(50),
    content TEXT,
    detail TEXT,
    attachment VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 农残检测报告表
CREATE TABLE IF NOT EXISTS pesticide_report (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    batch_id INTEGER NOT NULL,
    report_no VARCHAR(50),
    test_date DATE,
    test_organization VARCHAR(100),
    item_name VARCHAR(100),
    result VARCHAR(50),
    standard_limit VARCHAR(50),
    unit VARCHAR(20),
    is_compliant INTEGER DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    total_amount DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'PENDING',
    receiver_name VARCHAR(50),
    receiver_phone VARCHAR(20),
    receiver_address TEXT,
    logistics_no VARCHAR(50),
    logistics_company VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_detail (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL,
    product_id INTEGER,
    product_name VARCHAR(100),
    quantity INTEGER,
    price DECIMAL(10,2)
);

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'CONSUMER',
    avatar VARCHAR(255),
    deleted INTEGER DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 知识库文档表
CREATE TABLE IF NOT EXISTS knowledge_doc (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    category VARCHAR(50),
    source VARCHAR(100),
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- AI对话记录表
CREATE TABLE IF NOT EXISTS agent_dialog (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    session_id VARCHAR(50),
    question TEXT,
    answer TEXT,
    tool_calls TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 索引
-- ============================================
CREATE INDEX IF NOT EXISTS idx_product_category ON product(category);
CREATE INDEX IF NOT EXISTS idx_product_batch_no ON product(batch_no);
CREATE INDEX IF NOT EXISTS idx_batch_no ON batch(batch_no);
CREATE INDEX IF NOT EXISTS idx_trace_record_batch ON trace_record(batch_id);
CREATE INDEX IF NOT EXISTS idx_pesticide_batch ON pesticide_report(batch_id);
CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_knowledge_category ON knowledge_doc(category);
CREATE INDEX IF NOT EXISTS idx_agent_session ON agent_dialog(session_id);
