package com.agritrace.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    private static final Logger log = LoggerFactory.getLogger(VectorStoreConfig.class);

    // 向量存储功能默认使用 RAG 知识库 API
    // 生产环境可集成 pgvector 或 Elasticsearch
}
