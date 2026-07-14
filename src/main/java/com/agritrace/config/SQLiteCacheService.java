package com.agritrace.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.sql.*;
import java.util.*;

/**
 * SQLite 边缘缓存服务
 * 适用于乡村振兴弱网场景下的离线容灾
 */
@Service
public class SQLiteCacheService {

    private static final Logger log = LoggerFactory.getLogger(SQLiteCacheService.class);

    @Value("${sqlite.cache.path:${user.dir}/sqlite-cache/edge-cache.db}")
    private String dbPath;

    @Value("${sqlite.cache.enabled:true}")
    private boolean enabled;

    private Connection connection;

    @PostConstruct
    public void init() {
        if (!enabled) return;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTableIfNotExists();
            log.info("SQLite 边缘缓存初始化成功: {}", dbPath);
        } catch (Exception e) {
            log.warn("SQLite 缓存初始化失败（不影响主系统运行）: {}", e.getMessage());
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS edge_cache (
                cache_key TEXT PRIMARY KEY,
                cache_value TEXT NOT NULL,
                hit_count INTEGER DEFAULT 0,
                update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void put(String key, String value) {
        if (!enabled || connection == null) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR REPLACE INTO edge_cache (cache_key, cache_value, update_time) VALUES (?, ?, datetime('now'))")) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.warn("SQLite 缓存写入失败: {}", e.getMessage());
        }
    }

    public Optional<String> get(String key) {
        if (!enabled || connection == null) return Optional.empty();
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE edge_cache SET hit_count = hit_count + 1 WHERE cache_key = ?")) {
            ps.setString(1, key);
            ps.executeUpdate();
        } catch (SQLException ignored) {}

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT cache_value FROM edge_cache WHERE cache_key = ?")) {
            ps.setString(1, key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getString("cache_value"));
            }
        } catch (SQLException e) {
            log.warn("SQLite 缓存读取失败: {}", e.getMessage());
        }
        return Optional.empty();
    }
}
