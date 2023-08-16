package com.jkqj.os.issue.handler.extract;


import com.jkqj.common.utils.JsonUtils;
import com.jkqj.os.issue.handler.config.ConfigRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 简历事件抓取
 */
@Slf4j
public class CvIssueExtractor extends DefaultIssueExtractor {

    public CvIssueExtractor(JdbcTemplateInitializer jdbcTemplateInitializer, ConfigRepository configRepository) {
        super(jdbcTemplateInitializer, configRepository);
    }

    /**
     * 合并结果
     */
    @Override
    protected void mergeMap(Map<String, Object> res, JdbcTemplateWrapper jdbcTemplateWrapper, String sql,
                            String tableName) {
        // 1. 整体聚合成一个字段
        Map<String, Object> tempMap = jdbcTemplateWrapper.query(sql);
        log.info("CvIssueExtractor.mergeMap 数据库查询结果: {}", JsonUtils.toJson(tempMap));
        if (tempMap == null) {
            return;
        }
        // 2. 散列多个字段
        for (String key : tempMap.keySet()) {
            // 主键处理
            if ("id".equals(key)) {
                res.put(tableName + "_id", tempMap.get(key));
            }
            // 主键处理
            if ("experience".equals(key)) {
                if(tempMap.get(key) == null || "未知".equals(tempMap.get(key))){
                    res.put(tableName + "_experience", -1L);
                }
                res.put(tableName + "_experience", Long.parseLong(String.valueOf(tempMap.get(key))));
            }
            res.put(key, tempMap.get(key));
        }
    }
}
