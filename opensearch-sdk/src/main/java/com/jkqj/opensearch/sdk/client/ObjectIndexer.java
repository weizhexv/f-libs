package com.jkqj.opensearch.sdk.client;

import com.jkqj.opensearch.sdk.json.JsonHolder;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.DocWriteResponse.Result;
import org.opensearch.action.bulk.BulkItemResponse;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.rest.RestStatus;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ObjectIndexer implements Indexer {
    private final RestHighLevelClient restHighLevelClient;

    public ObjectIndexer(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @PreDestroy
    public synchronized void destroy() {
        try {
            restHighLevelClient.close();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    @Override
    public <T> boolean index(T data, String tableName, IdTaker<T> idTaker, boolean snakeStyle) {
        IndexRequest request = new IndexRequest(tableName);
        request.id(idTaker.taker(data) + "");
        request.source(JsonHolder.toJson(data, snakeStyle), XContentType.JSON);

        try {
            IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            if(indexResponse.status() == RestStatus.CREATED){
                return true;
            }
            if (indexResponse.status() != RestStatus.OK
                    || (indexResponse.getResult() != Result.CREATED
                    && indexResponse.getResult() != Result.DELETED
                    && indexResponse.getResult() != Result.UPDATED)) {
                log.error("index error,tableName:{},reason:{},data:{}", tableName, indexResponse.status().name(), JsonHolder.toNormalJson(data));
                return false;
            }
            return true;
        } catch (IOException e) {
            log.error("error:" + tableName, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> boolean bulk(List<T> data, String tableName, IdTaker<T> idTaker, boolean snakeStyle) {
        BulkRequest bulkRequest = new BulkRequest();


        bulkRequest.add(data.stream().map(data1 -> {
            IndexRequest request = new IndexRequest(tableName);
            request.id(idTaker.taker(data1) + "");
            request.source(JsonHolder.toJson(data1, snakeStyle), XContentType.JSON);
            return request;
        }).collect(Collectors.toList()));


        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse.status() != RestStatus.OK) {
                log.error("index error,tableName:{},reason:{},data:{}", tableName, bulkResponse.status().name(), JsonHolder.toNormalJson(data));
                return false;
            } else {
                List<String> badIdList = new ArrayList<>();
                for (BulkItemResponse itemResponse : bulkResponse.getItems()) {
                    if (itemResponse.isFailed()) {
                        badIdList.add(itemResponse.getId());
                    }
                }
                if (badIdList.size() > 0) {
                    log.error("part index error,tableName:{},ids:{}", tableName, JsonHolder.toNormalJson(badIdList));
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            log.error("error:" + tableName, e);
            throw new RuntimeException(e);
        }
    }

}
