package com.jkqj.os.issue.handler.extract;

import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;

/**
 * JdbcTemplate 扩展
 *
 * @author liuyang
 */
@Slf4j
@Getter
public class JdbcTemplateWrapper {

    private final JdbcTemplate jdbcTemplate;

    private final HikariDataSource dataSource;

    public JdbcTemplateWrapper(JdbcTemplate jdbcTemplate, HikariDataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
        this.jdbcTemplate.setDataSource(dataSource);
    }

    /**
     * 数据查询
     */
    public Map<String, Object> query(String sql) {
        // 时间格式目前会自动转换成时间戳
        return jdbcTemplate.queryForMap(sql);
    }
    /**
     * 数据查询
     */
    public List<Map<String, Object>> queryForList(String sql) {
        // 时间格式目前会自动转换成时间戳
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * sql构造
     */
    public String getQuerySql(String indexName, Long idValue, String tableName, List<String> queryColumns, boolean requireAggregate) {
        if (queryColumns.isEmpty()) {
            throw new RuntimeException("拼接SQL字段为空");
        }
        // 1. 拼接字段
        StringBuilder queryColumnsStr = new StringBuilder();
        queryColumnsStr.append("select ");
        queryColumns.forEach(column -> {
            queryColumnsStr.append(column).append(",");
        });
        queryColumnsStr.deleteCharAt(queryColumnsStr.length() - 1);
        // 2. 拼接SQL
        if (StringUtils.isBlank(indexName)) {
            indexName = "id ";
        }
        queryColumnsStr.append(" from ").append(tableName).append(" where ").append(indexName).append(" = ").append(idValue);
        // 根据是否聚合判断
        if (!requireAggregate) {
            return queryColumnsStr.append(" limit 1;").toString();
        }
        return queryColumnsStr.append(";").toString();
    }

    /**
     * 数据表查询字段获取
     */
    public List<String> getQueryColumns(String tableName,
                                        List<String> includeColumns,
                                        List<String> excludeColumns) {
        if (includeColumns.isEmpty()) {
            throw new RuntimeException("数据库中字段不存在");
        }
        return includeColumns;

    }

    /**
     * 数据表全部字段获取
     */
    public Map<String, ResultSet> getAllColumns(String tableName) {
        Map<String, ResultSet> columnMap = new HashMap<>();
        ResultSet columns;
        try {
            DatabaseMetaData dbMetaData = this.jdbcTemplate.getDataSource().getConnection().getMetaData();
            columns = dbMetaData.getColumns(null, null, tableName, null);
            while (columns.next()) {
                columnMap.put(columns.getString("COLUMN_NAME"), columns);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return columnMap;
    }


}
