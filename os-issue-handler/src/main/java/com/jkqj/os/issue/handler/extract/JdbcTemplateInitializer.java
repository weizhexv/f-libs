package com.jkqj.os.issue.handler.extract;

import com.jkqj.os.issue.handler.config.OsIssueConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JdbcTemplate
 *
 * @author liuyang
 */
@Slf4j
public class JdbcTemplateInitializer {

    /**
     * JdbcTemplate缓存
     */
    private Map<String, JdbcTemplateWrapper> jdbcTemplateMap;

    /**
     * JdbcTemplate配置
     */
    private final Map<String, OsIssueConfig> osIssueConfigMap;

    /**
     * JdbcTemplate对象初始化
     */
    public JdbcTemplateInitializer(Map<String, OsIssueConfig> osIssueConfigs) {
        this.osIssueConfigMap = osIssueConfigs;
    }

    /**
     * JdbcTemplate构造
     */
    @PostConstruct
    public void init() {
        jdbcTemplateMap = new ConcurrentHashMap<>(osIssueConfigMap.size() * 4 / 3 + 1);
        for (String key : osIssueConfigMap.keySet()) {
            OsIssueConfig osIssueConfig = osIssueConfigMap.get(key);
            if (osIssueConfig.getJdbcConfig() == null) {
                throw new RuntimeException("HikariResourceInitializer.init 数据源配置为空");
            }
            HikariDataSource dataSource = buildDatasource(osIssueConfig.getJdbcConfig());
            jdbcTemplateMap.put(key, new JdbcTemplateWrapper(new JdbcTemplate(), dataSource));
        }
    }

    /**
     * 数据库连接池销毁
     */
    @PreDestroy
    public synchronized void destroy() {
        if (jdbcTemplateMap == null || jdbcTemplateMap.isEmpty()) {
            return;
        }
        for (JdbcTemplateWrapper jdbcTemplate : jdbcTemplateMap.values()) {
            HikariDataSource dataSource = jdbcTemplate.getDataSource();
            dataSource.close();
        }
        jdbcTemplateMap = null;
    }

    /**
     * 数据源获取
     */
    public JdbcTemplateWrapper getDataSource(String dbName) {
        return jdbcTemplateMap.get(dbName);
    }

    /**
     * 数据源构造
     */
    private HikariDataSource buildDatasource(OsIssueConfig.JdbcConfig jdbcConfig) {
        if (jdbcConfig == null) {
            return null;
        }
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(jdbcConfig.getDriverClassName());
        config.setReadOnly(true);
        config.setAutoCommit(true);
        config.setJdbcUrl(jdbcConfig.getUrl());
        config.setUsername(jdbcConfig.getUsername());
        config.setPassword(jdbcConfig.getPassword());
        config.setConnectionTimeout(jdbcConfig.getConnectionTimeout());
        config.setIdleTimeout(jdbcConfig.getIdleTimeout());
        config.setMaximumPoolSize(jdbcConfig.getMaximumPoolSize());
        config.setMinimumIdle(jdbcConfig.getMinimumIdle());
        config.setMaxLifetime(jdbcConfig.getMaxLifetime());
        config.addDataSourceProperty("fetchSize", jdbcConfig.getFetchSize());
        config.addDataSourceProperty("trustSelfSigned", "true");
        config.setConnectionTestQuery("select 1");
        return new HikariDataSource(config);
    }




}