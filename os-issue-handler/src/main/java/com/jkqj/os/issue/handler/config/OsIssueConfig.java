package com.jkqj.os.issue.handler.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * OSS配置处理
 *
 * @author liuyang
 */
@Setter
@Getter
public class OsIssueConfig {

    /**
     * 数据库连接配置
     */
    private JdbcConfig jdbcConfig;

    /**
     * 表配置
     */
    private Map<String, TableConfig> tablesMap;

    /**
     * 表配置
     */
    private Map<String,List<TableConfig>> multiTablesMap;


    /**
     * 数据库连接配置
     */
    @Getter
    @Setter
    public static class JdbcConfig {
        private String driverClassName;
        private boolean autoCommit;
        private long connectionTimeout;
        private long idleTimeout;
        private long maxLifetime;
        private int minimumIdle;
        private int maximumPoolSize;
        private String password;
        private String username;
        private String url;
        private int fetchSize;
    }

    /**
     * 数据表配置
     *
     * 1. includes 不为空则仅查询此数据内column
     * 2. excludes 不为空则仅排除此数组内column
     */
    @Getter
    @Setter
    public static class TableConfig {
        /**
         * 表名
         */
        private String tableName;
        /**
         * 需要聚合
         */
        private boolean requireAggregation;
        /**
         * 查询键名
         */
        private String dependencyKey;
        /**
         * 包含字段
         */
        private List<String> includes;
        /**
         * 排除字段
         */
        private List<String> excludes;
    }
}
