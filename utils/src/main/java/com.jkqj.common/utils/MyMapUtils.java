package com.jkqj.common.utils;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

/**
 * 自定义Map工具类
 *
 * @author cb
 * @date 2021/12/13
 */
public final class MyMapUtils {

    /**
     * 间接映射
     *
     * @param map1
     * @param map2
     * @param <K>
     * @param <V>
     * @param <T>
     * @return
     */
    public static <K, V, T> Map<K, T> indirectMap(Map<K, V> map1, Map<V, T> map2) {
        if (MapUtils.isEmpty(map1) || MapUtils.isEmpty(map2)) {
            return Collections.emptyMap();
        }

        return Lambdas.extract2Map(map1.entrySet(), Map.Entry::getKey, map1Entry -> map2.get(map1Entry.getValue()));
    }

    /**
     * 把一个对象转换成map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> populateMapFrom(Object obj) {
        if (obj == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> map = Maps.newHashMap();
        BeanUtil.beanToMap(obj, map, false, true);

        return map;
    }

    public static <T> T getValue(Map<String, Object> map, String key) {
        return getValue(map, key, null);
    }

    public static <T> T getValue(Map<String, Object> map, String key, T defaultValue) {
        if (map == null) {
            return defaultValue;
        }

        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }

        return (T) map.getOrDefault(key, defaultValue);
    }

    public static Long getLongValue(Map<String, Object> map, String key) {
        return getLongValue(map, key, null);
    }

    public static Long getLongValue(Map<String, Object> map, String key, Long defaultValue) {
        if (map == null) {
            return defaultValue;
        }

        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Long) {
            return (Long) value;
        }

        String str = value.toString();

        if (NumberUtils.isDigits(str)) {
            return Long.valueOf(str);
        }

        return defaultValue;
    }

    public static <T> LinkedHashMap<T, T> newLinkedHashMap(T... objs) {
        if (ArrayUtils.isEmpty(objs)) {
            return new LinkedHashMap(0);
        }
        Preconditions.checkArgument(objs.length % 2 == 0, "数组长度不是偶数");

        LinkedHashMap<T, T> linkedHashMap = Maps.newLinkedHashMapWithExpectedSize(objs.length / 2);
        for (int i = 0; i < objs.length; i+=2) {
            linkedHashMap.put(objs[i], objs[i + 1]);
        }

        return linkedHashMap;
    }

    public static <T> LinkedHashMap<String, T> sortMapByKey(Map<String, T> params) {
        List<Map.Entry<String, T>> sortedEntries = Lambdas.sort(params.entrySet(), Map.Entry::getKey);
        LinkedHashMap<String, T> linkedHashMap = Maps.newLinkedHashMapWithExpectedSize(sortedEntries.size());
        sortedEntries.forEach(entry -> linkedHashMap.put(entry.getKey(), entry.getValue()));

        return linkedHashMap;
    }

    public static String[] toArray(Map<String, String> map) {
        if (MapUtils.isEmpty(map)) {
            return new String[0];
        }

        return Lambdas.flatMapToList(map.entrySet(), entry -> Lists.newArrayList(entry.getKey(), entry.getValue()))
                .toArray(new String[0]);
    }

    public static <K, V> List<V> multiGetToList(Map<K, V> map, Collection<K> keys) {
        if (MapUtils.isEmpty(map)) {
            return Collections.emptyList();
        }

        return Lambdas.mapToList(keys, map::get);
    }

    public static <K, V> Set<V> multiGetToSet(Map<K, V> map, Collection<K> keys) {
        if (MapUtils.isEmpty(map)) {
            return Collections.emptySet();
        }

        return Lambdas.mapToSet(keys, map::get);
    }

    public static <K, V> Map<K, V> mergeToNewMap(Map<K, V>... maps) {
        Map<K, V> newMap = new HashMap<>();

        for (Map<K, V> map : maps) {
            if (MapUtils.isNotEmpty(map)) {
                newMap.putAll(map);
            }
        }

        return newMap;
    }

}
