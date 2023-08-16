package com.jkqj.common.utils;

import com.jkqj.common.exception.BusinessException;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * 属性工具类
 *
 * @author cb
 * @date 2021-03-24
 */
public final class MyPropertyUtils {

    public static <T> T getProperty(Object bean, String name) {
        try {
            return (T) PropertyUtils.getProperty(bean, name);
        } catch (Exception e) {
            throw new BusinessException("获取属性" + name + "失败");
        }
    }

    public static void setProperty(Object bean, String name, Object value) {
        try {
            PropertyUtils.setProperty(bean, name, value);
        } catch (Exception e) {
            throw new BusinessException("设置属性" + name + "失败");
        }
    }

}