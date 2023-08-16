package com.jkqj.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * 复制工具类
 *
 * @author cb
 * @date 2020-10-15
 */
public final class CopyUtils {

    /**
     * 对象复制
     *
     * @param source：原始对象
     * @param supplier：目标对象提供者
     * @return
     */
    public static <T, E> E copy(T source, Supplier<E> supplier) {
        if (source == null) {
            return null;
        }

        E targetInstance = supplier.get();
        BeanUtils.copyProperties(source, targetInstance);

        return targetInstance;
    }

    /**
     * 对象复制
     *
     * @param source：原始对象
     * @param classType：目标对象
     * @return
     */
    public static <T, E> E copy(T source, Class<E> classType) {
        if (source == null) {
            return null;
        }

        E targetInstance;
        try {
            targetInstance = classType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        BeanUtils.copyProperties(source, targetInstance);

        return targetInstance;
    }

    /**
     * 对象批量复制
     *
     * @param sourceList:原始对象
     * @param supplier：目标对象提供者
     * @return
     */
    public static <T, E> List<E> batchCopy(List<T> sourceList, Supplier<E> supplier) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }

        return Lambdas.mapToList(sourceList, source -> copy(source, supplier));
    }

    /**
     * 对象批量复制
     *
     * @param sourceList:原始对象
     * @param classType：目标对象
     * @return
     */
    public static <T, E> List<E> batchCopy(List<T> sourceList, Class<E> classType) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }

        return Lambdas.mapToList(sourceList, source -> copy(source, classType));
    }

    public static void copyIgnoreNullValue(Object source, Object dest) {
        BeanUtil.copyProperties(source, dest, CopyOptions.create().setIgnoreNullValue(true));
    }

}