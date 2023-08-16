package com.jkqj.opensearch.sdk.json;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
public class JsonHolder {
    public static final ObjectMapper NORMAL_OM = build(false);
    public static final ObjectMapper SNAKE_OM = build(true);
    private JsonHolder(){}

    public static ObjectMapper build(boolean snake) {
        ObjectMapper om = new ObjectMapper()
                .setSerializationInclusion(Include.NON_NULL)
                // 允许字段名不用引号
                .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                // 允许使用单引号
                .configure(Feature.ALLOW_SINGLE_QUOTES, true)
                // 允许数字含有前导0
                .configure(Feature.ALLOW_NUMERIC_LEADING_ZEROS, true)
                .configure(Feature.STRICT_DUPLICATE_DETECTION, true)
                // 允许未知的属性
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // 空字符转null
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //keep Map.Entry as POJO

//        PropertyNamingStrategy old =  objectMapper.getPropertyNamingStrategy();
        if (snake) {
            om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        }

        SimpleModule module = new SimpleModule();
        //adding our custom serializer and deserializer
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        //registering the module with ObjectMapper
        om.registerModule(module);

        return om;
    }

    public static String toNormalJson(Object value) {
        try {
            return JsonHolder.NORMAL_OM.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.info("convert json error:" , e);
            return null;
        }
    }

    public static String toJson(Object value,boolean snake) {
       if(snake) {
           return toJsonSnake(value);
       }
       return toNormalJson(value);
    }
    public static String toJsonSnake(Object value) {
        try {
            return JsonHolder.SNAKE_OM.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.info("convert json error:" , e);
            return null;
        }
    }

    public static Map<String,Object> fromJson(String json) {
        try {
            return JsonHolder.NORMAL_OM.readValue(json,Map.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
