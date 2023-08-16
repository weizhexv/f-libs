package com.jkqj.dubbo.generic.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.LinkedHashMap;
import java.util.Map;
//import com.qiangjing.json.ext.LocalDateTimeSerializer;
//import com.qiangjing.json.ext.LocalDatetimeDeserializer;

//import java.time.LocalDateTime;

public class JsonHelper {
    private static final ObjectMapper om = new ObjectMapper();

    static {
        //creating a module
        SimpleModule module = new SimpleModule();
        //adding our custom serializer and deserializer
//        module.addSerializer(LocalDateTime .class ,new LocalDateTimeSerializer());
//        module.addDeserializer(LocalDateTime .class,new LocalDatetimeDeserializer());
        //registering the module with ObjectMapper
        om.registerModule(module);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }


    public static String toJson(Object vo) {
        try {
            return om.writeValueAsString(vo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json,Class<T> tClass) {
        try {
            return om.readValue(json,tClass);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String,Object> fromJson(String json) {
        try {
            return (Map<String,Object>)om.readValue(json, LinkedHashMap.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
