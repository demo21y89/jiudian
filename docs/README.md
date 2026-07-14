# AI Agent 农产品溯源智能交易平台

## 项目概述

农产品线上交易 + 全链路溯源 AI 系统，基于 Java 21 + Spring Boot 3.x + PostgreSQL(pgvector) 构建。

### 技术栈
- **Java 21** (虚拟线程)
- **Spring Boot 3.3.x**
- **PostgreSQL** (主库 + pgvector 向量引擎)
- **SQLite** (边缘缓存/离线容灾)
- **Spring AI** (大模型集成)
- **FISCO BCOS** (区块链存证)

### 核心功能模块
1. Web 商城 (商品管理、订单管理)
2. AI 智能对话 (自然语言溯源查询)
3. RAG 知识库 (法规/标准检索)
4. MCP Skill 服务 (6个可插拔工具)
5. 区块链溯源 (FISCO BCOS)

## 项目结构
```
src/main/java/com/agritrace/
├── AgriTraceApplication.java    # 主程序入口
├── config/                       # 配置层
├── common/                       # 公共组件
│   ├── constant/                 # 常量
│   ├── exception/                # 异常处理
│   ├── response/                 # 响应封装
│   └── util/                     # 工具类
├── agent/                        # AI Agent 模块
│   ├── AgentOrchestrator.java    # Agent 编排器
│   ├── AgentController.java      # SSE 流式接口
│   └── memory/                   # 会话记忆
├── module/
│   ├── mall/                     # 商城模块
│   ├── order/                    # 订单模块
│   ├── trace/                    # 溯源模块
│   └── user/                     # 用户模块
├── knowledge/                    # RAG 知识库
├── mcp/                          # MCP Skill 模块
│   ├── core/                     # 核心调度
│   ├── skill/                    # 6个业务技能
│   └── spi/                      # SPI 接口
└── blockchain/                   # 区块链模块
```

## 快速开始

### 环境要求
- JDK 21+
- Maven 3.9+
- PostgreSQL 15+ (带 pgvector 扩展)
- Redis 7+ (缓存)

### 运行步骤
1. 创建数据库
```sql
CREATE DATABASE agritrace;
CREATE EXTENSION vector;
```

2. 配置 application.yml

3. 构建运行
```bash
mvn clean install
mvn spring-boot:run
```

## 核心 API 接口

### Agent 对话
```bash
POST /api/v1/agent/chat
Content-Type: application/json

{"session_id": "sess_xxx", "query": "山东苹果农残是否达标？", "user_id": 1}
```

### 溯源查询
```bash
POST /api/v1/trace/query
Content-Type: application/json

{"trace_code": "TRC20260714001"}
```

### RAG 检索
```bash
POST /api/v1/rag/retrieve
Content-Type: application/json

{"query": "苹果种植农药使用标准", "top_k": 5}
```

### MCP Skill 调用
```bash
POST /mcp/skill/invoke
Content-Type: application/json

{"skill": "trace_query", "params": {"batch_no": "B20260701"}}
```
