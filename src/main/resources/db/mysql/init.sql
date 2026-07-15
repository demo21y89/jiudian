-- ═══════════════════════════════════════════════
-- AgriTrace MySQL 初始化脚本
-- ═══════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS agritrace DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE agritrace;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CONSUMER',
    phone VARCHAR(20),
    email VARCHAR(100),
    enabled BOOLEAN DEFAULT TRUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    origin VARCHAR(100),
    price DECIMAL(10,2) NOT NULL,
    unit VARCHAR(20) DEFAULT '斤',
    stock INT DEFAULT 0,
    description TEXT,
    image_url VARCHAR(500),
    farmer_id BIGINT,
    enabled BOOLEAN DEFAULT TRUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(30) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(100),
    quantity INT NOT NULL DEFAULT 1,
    total_price DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'PENDING',
    shipping_address VARCHAR(255),
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 溯源记录表
CREATE TABLE IF NOT EXISTS trace_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trace_code VARCHAR(30) NOT NULL UNIQUE,
    product_id BIGINT,
    batch_no VARCHAR(30),
    product_name VARCHAR(100),
    origin VARCHAR(100),
    cert_valid BOOLEAN DEFAULT FALSE,
    scan_count INT DEFAULT 0,
    tx_hash VARCHAR(100),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 溯源环节表
CREATE TABLE IF NOT EXISTS trace_stages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trace_record_id BIGINT NOT NULL,
    stage_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    operator VARCHAR(100),
    record_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_trace (trace_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 知识库文档表
CREATE TABLE IF NOT EXISTS knowledge_docs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    category VARCHAR(50),
    source VARCHAR(200),
    tags VARCHAR(200),
    enabled BOOLEAN DEFAULT TRUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ═══ 种子数据 ═══
INSERT IGNORE INTO users (username, password, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'ADMIN'),
('farmer1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'FARMER'),
('consumer1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'CONSUMER');

INSERT IGNORE INTO products (name, category, origin, price, unit, stock, description) VALUES
('红富士苹果', '水果', '山东烟台', 8.99, '斤', 500, '烟台有机种植红富士，果径80mm以上，甜脆多汁'),
('五常大米', '谷物', '黑龙江五常', 15.90, '袋', 200, '五常原产地地理标志产品，有机种植，人工除草'),
('有机西红柿', '蔬菜', '山东寿光', 5.99, '斤', 300, '寿光绿色食品认证，熊蜂授粉，零农残'),
('库尔勒香梨', '水果', '新疆库尔勒', 12.80, '斤', 150, '新疆库尔勒地理标志，皮薄肉细，清甜多汁'),
('土鸡蛋', '禽蛋', '河北保定', 19.90, '盒', 100, '林下散养土鸡蛋，30枚/盒，无抗生素');

INSERT IGNORE INTO trace_records (trace_code, product_id, batch_no, product_name, origin, cert_valid, scan_count) VALUES
('TRC20260714001', 1, 'B20260701', '红富士苹果', '山东烟台', TRUE, 128),
('TRC20260714002', 2, 'B20260702', '五常大米', '黑龙江五常', TRUE, 56),
('TRC20260714003', 3, 'B20260703', '有机西红柿', '山东寿光', TRUE, 89);

INSERT IGNORE INTO trace_stages (trace_record_id, stage_name, description, operator, record_time) VALUES
(1, '种植', '烟台栖霞果园，有机种植，使用生物防虫', '张农户', '2026-03-15'),
(1, '施肥', '施用有机肥，经检测符合GB/T 19630标准', '张农户', '2026-04-20'),
(1, '采摘', '人工采摘，果径80mm以上精选', '李工人', '2026-06-10'),
(1, '检测', '农残检测合格，符合GB 2763-2021标准', '质检中心', '2026-06-12'),
(1, '包装', '冷链包装，贴溯源码', '王包装', '2026-06-13'),
(1, '出库', '冷链物流发货', '物流部', '2026-06-14');

INSERT IGNORE INTO knowledge_docs (title, content, category, source, tags) VALUES
('GB 2763-2021 食品中农药最大残留限量', '本标准规定了食品中564种农药在376种食品中10092项最大残留限量。适用于与限量相关的食品。农产品生产者应确保使用的农药种类和用量符合标准要求。', '法规标准', '国家卫健委/农业农村部', '农残,标准,国标'),
('有机产品认证管理办法', '有机产品认证是指认证机构依照有机产品认证标准，对有机产品生产和加工过程进行评价的活动。有机产品生产过程中不得使用化学合成的农药、化肥、生长调节剂等物质。', '法规标准', '国家市场监管总局', '有机认证,标准'),
('农产品质量安全法', '农产品质量安全法规定了农产品质量安全标准、农产品产地、农产品生产、农产品包装和标识、监督检查等内容。农产品生产企业应当建立农产品生产记录。', '法律法规', '全国人大', '法律,质量安全'),
('绿色食品标准 NY/T 391', '绿色食品产地环境技术条件，规定了绿色食品产地的环境空气质量、农田灌溉水质、土壤环境质量的各项指标及浓度限值。', '种植标准', '农业农村部', '绿色食品,种植');
