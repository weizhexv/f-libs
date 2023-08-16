package com.jkqj.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;

/**
 * 自定义集合工具类
 *
 * @author cb
 * @date 2021/12/17
 */
public final class MyCollectionUtils {

    /**
     * 按照指定实体键列表的顺序排序
     *
     * @param list
     * @param keys
     * @param keyMapper
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> List<T> sortListByKeysOrder(Collection<K> keys, Collection<T> list, Function<T, K> keyMapper) {
        if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(keys)) {
            return Collections.emptyList();
        }

        Map<K, T> map = Lambdas.trans2Map(list, keyMapper);

        return Lambdas.mapToList(keys, map::get);
    }

    public static <T> List<T> trim(List<T> list) {
        return list != null ? list : Collections.emptyList();
    }

    public static <T> Set<T> trim(Set<T> set) {
        return set != null ? set : Collections.emptySet();
    }

    public static <T> List<T> unionToList(Collection<T>... collections) {
        return Lists.newArrayList(unionToSet(collections));
    }

    public static <T> Set<T> unionToSet(Collection<T>... collections) {
        Set<T> set = Sets.newHashSet();

        for (Collection<T> collection : collections) {
            if (collection != null) {
                set.addAll(collection);
            }
        }

        return set;
    }

}
