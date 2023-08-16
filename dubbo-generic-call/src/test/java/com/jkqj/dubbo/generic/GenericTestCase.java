package com.jkqj.dubbo.generic;


import com.jkqj.dubbo.generic.helper.JsonHelper;
import com.jkqj.dubbo.generic.http.GenericCallMapping;
import com.jkqj.dubbo.generic.helper.MappingLineParser;
import com.jkqj.dubbo.generic.model.OrderItem;
import com.jkqj.dubbo.generic.model.OrderRequest;
import com.jkqj.dubbo.generic.reference.ApiModeDubboGenericFactory;
import com.jkqj.dubbo.generic.reference.BaseAppModeDubboGenericFactory;
import com.jkqj.dubbo.generic.reference.DubboGenericFactory;
import com.jkqj.dubbo.generic.reference.GenericConsumerConfig;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericTestCase {

    @Test
    public void callTest(){
        GenericConsumerConfig consumerConfig = new GenericConsumerConfig();
        consumerConfig.setAppName("dubbo-client");
        consumerConfig.setRegisterAddress("nacos://127.0.0.1:8848");
        consumerConfig.setTimeout(1000);
        consumerConfig.setThreads(32);

        DubboGenericFactory dubboGenericFactory = new ApiModeDubboGenericFactory(consumerConfig);
//        DubboGenericFactory dubboGenericFactory = new BaseAppModeDubboGenericFactory();


        // http mapping dubbo config style, e.g.
        // path: group/interfaceName/method/paramClassName/retries[/version]
        String mappingLine = "/zm/v1/hello:local/com.github.rolandhe.api.HelloWorldService/order/com.github.rolandhe.api.model.OrderRequest/2";

        GenericCallMapping mapping = MappingLineParser.parseLine(mappingLine);

        GenericCallContext ctx = mapping.getCallContext();


        // 构建泛化参数
        Map<String, Object> requestMap = build();

        // 构建泛化调用上下文

        Object ret = GenericCallExecutor.execute(dubboGenericFactory, ctx, new Object[]{requestMap});

        // 清理资源
        // 如果在spring中使用，可以在构建时指定destroy方法, e.g:
        // @Bean(destroyMethod = "destroy")
        // public DubboGenericFactory dubboGenericFactory() {
        //      return new DubboGenericFactory();
        // }
        dubboGenericFactory.destroy();

        System.out.println(ret);
    }

    private static Map<String, Object> build() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setNumber("009992");
        List<OrderItem> itemList = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setName("a");
        item.setCount(10L);
        itemList.add(item);

        item = new OrderItem();
        item.setName("b");
        item.setCount(15L);
        itemList.add(item);

        orderRequest.setItemList(itemList);

        String json = JsonHelper.toJson(orderRequest);

        // 转换成map，用于泛化调用
        Map<String, Object> map = JsonHelper.fromJson(json);
        return map;

    }
}
