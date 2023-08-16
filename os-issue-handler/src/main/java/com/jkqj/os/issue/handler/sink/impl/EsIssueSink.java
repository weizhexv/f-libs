package com.jkqj.os.issue.handler.sink.impl;

import com.jkqj.opensearch.sdk.client.ObjectIndexer;
import com.jkqj.opensearch.sdk.client.OpenSearchClientInitializer;
import com.jkqj.os.issue.handler.sink.IssueSink;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


/**
 * ES数据录入服务
 *
 * @author liuyang
 */
@Slf4j
public class EsIssueSink implements IssueSink {

    private final String host;

    private final int port;

    private final String env;

    private ObjectIndexer objectIndex;

    public EsIssueSink(String host, int port, String env) {
        this.host = host;
        this.port = port;
        this.env = env;
        this.objectIndex = new ObjectIndexer(OpenSearchClientInitializer.buildRestClient(host, port));
    }

    /**
     * ES索引名称规则：环境名_应用名_表名
     */
    @Override
    public boolean sink(String appName, String tableName, Long id, Map<String, Object> data) {
        data.remove("id");
        if (!data.containsKey(tableName + "_id")) {
            data.put(tableName + "_id", id);
        }
        log.info("ES索引入库数据, {}", data);
        return objectIndex.index(data, env + "." + appName + "." + tableName, data1 -> id, true);
    }

}
