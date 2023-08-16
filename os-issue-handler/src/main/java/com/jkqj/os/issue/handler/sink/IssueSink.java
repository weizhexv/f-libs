package com.jkqj.os.issue.handler.sink;

import java.util.Map;

public interface IssueSink {

    boolean sink(String appName, String tableName, Long id, Map<String, Object> data);
}
