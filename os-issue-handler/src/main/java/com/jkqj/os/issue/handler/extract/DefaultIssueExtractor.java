package com.jkqj.os.issue.handler.extract;


import com.jkqj.common.utils.JsonUtils;
import com.jkqj.os.issue.handler.config.ConfigRepository;
import com.jkqj.os.issue.handler.config.OsIssueConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件数据抓取
 */
@Slf4j
public class DefaultIssueExtractor implements IssueExtractor {

    private final JdbcTemplateInitializer jdbcTemplateInitializer;

    private final ConfigRepository configRepository;

    public DefaultIssueExtractor(JdbcTemplateInitializer jdbcTemplateInitializer, ConfigRepository configRepository) {
        this.jdbcTemplateInitializer = jdbcTemplateInitializer;
        this.configRepository = configRepository;
    }

    @Override
    public Map<String, Object> extractIssueData(String topic, String tableName, Long idValue) {
        // 1. 数据库是否需要处理
        Map<String, OsIssueConfig> map = configRepository.getOsIssueConfig();
        OsIssueConfig osIssueConfig = map.get(topic);
        if (osIssueConfig == null) {
            return null;
        }
        // 2. 表是否需要处理
        Map<String, Object> tableDataRes = extractTableData(osIssueConfig, topic, tableName, idValue);
        if (tableDataRes == null) {
            // 3. 进入多表聚合数据拉取
            return extractMultiTableData(osIssueConfig, topic, tableName, idValue);
        }
        return tableDataRes;
    }

    /**
     * 单表处理
     */
    protected Map<String, Object> extractTableData(OsIssueConfig osIssueConfig, String topic, String tableName, Long idValue) {
        // 1. 表是否需要处理
        Map<String, OsIssueConfig.TableConfig> tableConfigMap = osIssueConfig.getTablesMap();
        if (tableConfigMap == null) {
            return null;
        }
        OsIssueConfig.TableConfig tableConfig = tableConfigMap.get(tableName);
        if (tableConfig == null) {
            return null;
        }
        // 2. 库JdbcTemplate获取
        JdbcTemplateWrapper jdbcTemplateWrapper = jdbcTemplateInitializer.getDataSource(topic);
        // 3. 关注字段获取
        List<String> queryColumns = jdbcTemplateWrapper.getQueryColumns(tableName, tableConfig.getIncludes(), tableConfig.getExcludes());
        // 4. SQL拼接
        String sql = jdbcTemplateWrapper.getQuerySql(null, idValue, tableName, queryColumns, false);
        log.info("单表查询SQL：{}", sql);
        // 5. 查询结果处理
        return jdbcTemplateWrapper.query(sql);
    }

    /**
     * 多表处理
     */
    protected Map<String, Object> extractMultiTableData(OsIssueConfig osIssueConfig, String topic, String tableName, Long idValue) {
        // 1. 表是否需要处理
        Map<String, List<OsIssueConfig.TableConfig>> tableConfigMap = osIssueConfig.getMultiTablesMap();
        if (tableConfigMap == null) {
            return null;
        }
        List<OsIssueConfig.TableConfig> tableConfigList = tableConfigMap.get(tableName);
        if (tableConfigList == null || tableConfigList.isEmpty()) {
            return null;
        }
        Map<String, Object> res = new HashMap<>(tableConfigList.size() * 4 / 3 + 1);
        // 2. 批量表处理
        for (OsIssueConfig.TableConfig tableConfig : tableConfigList) {
            // 2.1. 库JdbcTemplate获取
            JdbcTemplateWrapper jdbcTemplateWrapper = jdbcTemplateInitializer.getDataSource(topic);
            // 2.2. 关注字段获取
            List<String> queryColumns = jdbcTemplateWrapper.getQueryColumns(tableConfig.getTableName(), tableConfig.getIncludes(), tableConfig.getExcludes());
            // 2.3. SQL拼接
            String sql = jdbcTemplateWrapper.getQuerySql(tableConfig.getDependencyKey(), idValue, tableConfig.getTableName(), queryColumns, tableConfig.isRequireAggregation());
            // 2.4. 查询结果处理
            log.info("多表查询SQL：{}", sql);
            // 2.5 合并至结果
            if (tableConfig.isRequireAggregation()) {
                mergeList(res, jdbcTemplateWrapper, sql, tableConfig.getTableName());
            } else {
                mergeMap(res, jdbcTemplateWrapper, sql, tableConfig.getTableName());
            }
        }
        return res;
    }

    /**
     * 合并结果
     */
    protected void mergeMap(Map<String, Object> res, JdbcTemplateWrapper jdbcTemplateWrapper, String sql,
                          String tableName) {
        // 1. 整体聚合成一个字段
        Map<String, Object> tempMap = jdbcTemplateWrapper.query(sql);
        log.info("DefaultIssueExtractor.mergeMap 数据库查询结果: {}", JsonUtils.toJson(tempMap));
        if (tempMap == null) {
            return;
        }
        // 2. 散列多个字段
        for (String key : tempMap.keySet()) {
            // 主键处理
            if ("id".equals(key)) {
                res.put(tableName + "_id", tempMap.get(key));
            }
            res.put(key, tempMap.get(key));
        }
    }

    /**
     * 合并列表结果
     */
    protected void mergeList(Map<String, Object> res, JdbcTemplateWrapper jdbcTemplateWrapper, String sql,
                           String tableName) {
        // 1. 整体聚合成一个字段
        List<Map<String, Object>> tempMapList = jdbcTemplateWrapper.queryForList(sql);
        log.info("DefaultIssueExtractor.mergeList 数据库查询结果: {}", JsonUtils.toJson(tempMapList));
        if (tempMapList == null || tempMapList.isEmpty()) {
            return;
        }
        // 2. 以表名为key设置结果集
        res.put(tableName, JsonUtils.toJson(tempMapList));
    }


}
