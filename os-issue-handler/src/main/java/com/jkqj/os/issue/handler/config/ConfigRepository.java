package com.jkqj.os.issue.handler.config;

import java.util.Map;

/**
 * 配置接口
 *
 * @author liuyang
 */
public interface ConfigRepository {

    Map<String, OsIssueConfig> getOsIssueConfig();

}
