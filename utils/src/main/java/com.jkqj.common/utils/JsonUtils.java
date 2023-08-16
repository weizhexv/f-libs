package com.jkqj.common.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * JSON与Java对象相互转换的工具类
 *
 * @author cb
 * @date 2020-10-24
 */
@Slf4j
public final class JsonUtils {

    private static ObjectMapper objectMapper;

    /**
     * 构造函数,使用默认的ObjectMapper实例, 拥有以下特性:<br>
     * 1) 允许字段名不使用引号<br>
     * 2) 允许字段名和字符串使用单引号<br>
     * 3) 允许数字含有前导符0<br>
     * 4) 允许有不存在的属性<br>
     * 5) 支持以下日期格式:<br>
     * "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "MM-dd","HH:mm:ss", "HH:mm"<br/>
     */
    static {
        objectMapper = new ObjectMapper()
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
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        ;
        //keep Map.Entry as POJO
        objectMapper.configOverride(Map.Entry.class)
                .setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.OBJECT));
        setDatePatterns(new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "MM-dd HH:mm"});

        SimpleModule module = new SimpleModule();
        //adding our custom serializer and deserializer
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        //registering the module with ObjectMapper
        objectMapper.registerModule(module);

    }

    /**
     * 获取当前ObjectMapper对象
     *
     * @return
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        JsonUtils.objectMapper = objectMapper;
    }

    /**
     * 设置识别的日期格式集合
     *
     * @param datePatterns
     * @return
     */
    private static void setDatePatterns(final String[] datePatterns) {
        objectMapper.setDateFormat(new SimpleDateFormat(datePatterns[0]) {
            @Override
            public Date parse(String source) {
                try {
                    return DateUtils.parseDate(source, datePatterns);
                } catch (Exception e) {
                    throw new IllegalArgumentException("date [" + source + "] should comply with one the formats:" + Arrays.toString(datePatterns), e);
                }
            }
        });
    }


    /**
     * json转T对象
     * <pre>
     *     String json="{\"key\":[1,2,3]}";
     *     TypeReference ref = new TypeReference<Map<String,String[]>>() { };
     *     Map<String,String[]> map=toBean(json,ref)
     * </pre>
     *
     * @param json
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T toBean(String json, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(json)) {
            return null;
        }

        return readValue(json, objectMapper.getTypeFactory().constructType(typeReference));
    }

    /**
     * json转Object对象, 根据json字符串的结构自动调整为对应的数据类型, 具体对应关系如下：<br>
     * 1)字符串->String类型<br>
     * 2)整数->int类型<br>
     * 3)长整数->long类型<br>
     * 4)实数->double类型 <br>
     * 5)键值对->(LinkedHash)Map类型<br>
     * 6)数组->(Array)List类型<br>
     *
     * @param json
     * @return
     */
    public static Object toObject(String json) {
        return toBean(json, Object.class);
    }

    /**
     * json转T对象
     *
     * @param <T>
     * @param json
     * @param beanType
     * @return
     */
    public static <T> T toBean(String json, Class<T> beanType) {
        if (StringUtils.isBlank(json)) {
            return null;
        }

        return readValue(json, objectMapper.getTypeFactory().constructType(beanType));
    }

    public static <T> T convertObject(Object obj, TypeReference<T> typeReference) {
        if (Objects.isNull(obj)) {
            return null;
        }
        return objectMapper.convertValue(obj, typeReference);
    }

    /**
     * json转T对象
     *
     * @param json
     * @param field
     * @param newClass
     * @param <T>
     * @return
     */
    public static <T> T toBean(String json, String field, Class<T> newClass) {
        if (StringUtils.isBlank(json)) {
            return null;
        }

        ObjectNode objectNode = toBean(json, ObjectNode.class);
        if (objectNode == null || !objectNode.has(field)) {
            return null;
        }

        JsonNode jsonNode = objectNode.get(field);

        return toBean(jsonNode.toString(), newClass);
    }

    /**
     * byte数组转T对象
     *
     * @param bytes
     * @param beanType
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T toBean(byte[] bytes, Class<T> beanType) {
        return objectMapper.readValue(bytes, objectMapper.getTypeFactory().constructType(beanType));
    }

    /**
     * json转T对象数组
     *
     * @param json
     * @param elementType
     * @return
     */
    public static <T> T[] toArray(String json, Class<T> elementType) {
        return readValue(json, objectMapper.getTypeFactory().constructArrayType(elementType));
    }

    /**
     * json转List&lt;T>对象
     *
     * @param <T>
     * @param json
     * @param elementType
     * @return
     */
    public static <T> List<T> toList(String json, Class<T> elementType) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }

        return readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, elementType));
    }

    /**
     * json转List&lt;T>对象
     *
     * @param json
     * @param field
     * @param newClass
     * @param <T>
     * @return
     */
    public static <T> List<T> toList(String json, String field, Class<T> newClass) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }

        ObjectNode objectNode = toBean(json, ObjectNode.class);
        if (objectNode == null || !objectNode.has(field)) {
            return null;
        }

        JsonNode jsonNode = objectNode.get(field);

        return toList(jsonNode.toString(), newClass);
    }

    /**
     * json转Set&lt;T>对象
     *
     * @param <T>
     * @param json
     * @param elementType
     * @return
     */
    public static <T> Set<T> toSet(String json, Class<T> elementType) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptySet();
        }

        return readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(HashSet.class, elementType));
    }

    public static <T> SortedSet<T> toSortedSet(String json, Class<T> elementType) {
        return readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(TreeSet.class, elementType));
    }

    /**
     * json转Map<String,Object>对象
     *
     * @param json
     * @return
     */
    public static Map<String, Object> toSimpleMap(String json) {
        return toMap(json, String.class, Object.class);
    }

    /**
     * json转Map<K,V>对象
     *
     * @param json
     * @param keyType
     * @param valueType
     * @return
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> keyType, Class<V> valueType) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyMap();
        }

        return readValue(json, objectMapper.getTypeFactory().constructMapType(LinkedHashMap.class, keyType, valueType));
    }

    public static <K, V> SortedMap<K, V> toSortedMap(String json, Class<K> keyType, Class<V> valueType) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptySortedMap();
        }

        return readValue(json, objectMapper.getTypeFactory().constructMapType(TreeMap.class, keyType, valueType));
    }

    /**
     * 对象类型转JSON字符串
     *
     * @param obj
     * @return
     */
    @SneakyThrows
    public static String toJson(Object obj) {
        return obj == null ? null : objectMapper.writeValueAsString(obj);
    }


    /**
     * 对象类型转美化后的JSON字符串
     *
     * @param obj
     * @return
     */
    @SneakyThrows
    public static String toPrettyJson(Object obj) {
        return obj == null ? null : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    /**
     * 把一个对象转化成另一个类型的对象
     *
     * @param obj
     * @param newClass
     * @param <T>
     * @return
     */
    public static <T> T toNewBean(Object obj, Class<T> newClass) {
        if (obj == null) {
            return null;
        }

        return toBean(toJson(obj), newClass);
    }

    /**
     * 把一个列表转化成另一个类型的列表
     *
     * @param list
     * @param newClass
     * @param <T>
     * @return
     */
    public static <T> List<T> toNewList(List<?> list, Class<T> newClass) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        return toList(toJson(list), newClass);
    }

    /**
     * 更新某个字段值
     *
     * @param json
     * @param field
     * @param updateObj
     * @return
     */
    public static String updateField(String json, String field, Object updateObj) {
        Object firstLevelObj = toBean(json, Object.class);
        if (firstLevelObj == null) {
            firstLevelObj = Maps.newHashMap();
        }

        MyPropertyUtils.setProperty(firstLevelObj, field, updateObj);

        return toJson(firstLevelObj);
    }

    /**
     * 读取某个字段
     *
     * @param json
     * @param field
     * @param <T>
     * @return
     */
    public static <T> T readField(String json, String field) {
        if (StringUtils.isBlank(json) || StringUtils.isBlank(field)) {
            return null;
        }

        try {
            return JsonPath.read(json, "$." + field);
        } catch (Exception e) {
            log.debug("未读取到字段, json: {}, field: {}", json, field);

            return null;
        }
    }

    /**
     * 读取某个对象
     *
     * @param json
     * @param field
     * @param <T>
     * @return
     */
    public static <T> T readObject(String json, String field, Class<T> clazz) {
        if (StringUtils.isBlank(json) || StringUtils.isBlank(field)) {
            return null;
        }

        try {
            return toNewBean(readField(json, field), clazz);
        } catch (Exception e) {
            log.debug("未读取到对象, json: {}, field: {}", json, field);

            return null;
        }
    }

    /**
     * 读取某个列表
     *
     * @param json
     * @param field
     * @param <T>
     * @return
     */
    public static <T> List<T> readList(String json, String field, Class<T> clazz) {
        if (StringUtils.isBlank(json) || StringUtils.isBlank(field)) {
            return Collections.emptyList();
        }

        try {
            return toNewList(readField(json, field), clazz);
        } catch (Exception e) {
            log.debug("未读取到列表, json: {}, field: {}", json, field);

            return Collections.emptyList();
        }
    }

    /**
     * 反序列化
     *
     * @param json
     * @param valueType
     * @param <T>
     * @return
     */
    @SneakyThrows
    private static <T> T readValue(String json, JavaType valueType) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return objectMapper.readValue(json, valueType);
    }

}