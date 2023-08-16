package com.jkqj.os.issue.handler.extract;

import java.util.Map;

public interface IssueExtractor {

    Map<String, Object> extractIssueData(String topic, String tableName, Long idValue);

}
