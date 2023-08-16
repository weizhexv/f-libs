package com.jkqj.common.utils;

import com.jkqj.common.enums.BaseEnum;
import com.jkqj.common.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 断言
 *
 * @author cb
 * @date 2021-12-08
 */
public final class MyAsserts {

    /**
     * 断言执行表达式后的结果是 true，否则抛出业务异常信息
     *
     * @param expression   表达式
     * @param errorCode    错误码
     * @param errorMessage 错误提示
     */
    public static void isTrue(boolean expression, Integer errorCode, String errorMessage) {
        if (!expression) {
            throw new BusinessException(errorCode, errorMessage);
        }
    }

    /**
     * 断言执行表达式后的结果是 true，否则抛出业务异常信息
     *
     * @param expression   表达式
     * @param errorMessage 错误提示
     */
    public static void isTrue(boolean expression, String errorMessage) {
        if (!expression) {
            throw new BusinessException(errorMessage);
        }
    }

    /**
     * 断言执行表达式后的结果是 true，否则抛出业务异常信息
     *
     * @param expression 表达式
     * @param baseEnum   错误码
     */
    public static void isTrue(boolean expression, BaseEnum baseEnum) {
        if (!expression) {
            throw new BusinessException(baseEnum);
        }
    }

    /**
     * 断言执行表达式后的结果是 false，否则抛出业务异常信息
     *
     * @param expression   表达式
     * @param errorCode    错误码
     * @param errorMessage 错误提示
     */
    public static void isFalse(boolean expression, Integer errorCode, String errorMessage) {
        if (expression) {
            throw new BusinessException(errorCode, errorMessage);
        }
    }

    /**
     * 断言执行表达式后的结果是 false，否则抛出业务异常信息
     *
     * @param expression   表达式
     * @param errorMessage 错误提示
     */
    public static void isFalse(boolean expression, String errorMessage) {
        if (expression) {
            throw new BusinessException(errorMessage);
        }
    }

    /**
     * 断言执行表达式后的结果是 false，否则抛出业务异常信息
     *
     * @param expression 表达式
     * @param baseEnum   错误码
     */
    public static void isFalse(boolean expression, BaseEnum baseEnum) {
        if (expression) {
            throw new BusinessException(baseEnum);
        }
    }

    /**
     * 断言传入的对象不是 null，否则抛出业务异常信息
     *
     * @param object       对象
     * @param errorCode    错误码
     * @param errorMessage 错误提示
     */
    public static void notNull(Object object, Integer errorCode, String errorMessage) {
        if (null == object) {
            throw new BusinessException(errorCode, errorMessage);
        }
    }

    /**
     * 断言传入的对象不是 null，否则抛出业务异常信息
     *
     * @param object       对象
     * @param errorMessage 错误提示
     */
    public static void notNull(Object object, String errorMessage) {
        if (null == object) {
            throw new BusinessException(errorMessage);
        }
    }

    /**
     * 断言传入的对象不是 null，否则抛出业务异常信息
     *
     * @param object   对象
     * @param baseEnum 错误码
     */
    public static void notNull(Object object, BaseEnum baseEnum) {
        if (null == object) {
            throw new BusinessException(baseEnum);
        }
    }

    /**
     * 断言传入的集合不为空，否则抛出业务异常信息
     *
     * @param collection   集合
     * @param errorCode    错误码
     * @param errorMessage 错误提示
     */
    public static void notEmpty(Collection<?> collection, Integer errorCode, String errorMessage) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(errorCode, errorMessage);
        }
    }

    /**
     * 断言传入的集合不为空，否则抛出业务异常信息
     *
     * @param collection   集合
     * @param errorMessage 错误提示
     */
    public static void notEmpty(Collection<?> collection, String errorMessage) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(errorMessage);
        }
    }

    /**
     * 断言传入的集合不为空，否则抛出业务异常信息
     *
     * @param collection 集合
     * @param baseEnum   错误码
     */
    public static void notEmpty(Collection<?> collection, BaseEnum baseEnum) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BusinessException(baseEnum);
        }
    }

    /**
     * 断言传入的集合不为空，否则抛出业务异常信息
     *
     * @param map          集合
     * @param errorCode    错误码
     * @param errorMessage 错误提示
     */
    public static void notEmpty(Map<?, ?> map, Integer errorCode, String errorMessage) {
        if (MapUtils.isEmpty(map)) {
            throw new BusinessException(errorCode, errorMessage);
        }
    }

    /**
     * 断言传入的集合不为空，否则抛出业务异常信息
     *
     * @param map          集合
     * @param errorMessage 错误提示
     */
    public static void notEmpty(Map<?, ?> map, String errorMessage) {
        if (MapUtils.isEmpty(map)) {
            throw new BusinessException(errorMessage);
        }
    }

    /**
     * 断言传入的集合不为空，否则抛出业务异常信息
     *
     * @param map      集合
     * @param baseEnum 错误码
     */
    public static void notEmpty(Map<?, ?> map, BaseEnum baseEnum) {
        if (MapUtils.isEmpty(map)) {
            throw new BusinessException(baseEnum);
        }
    }

    /**
     * 断言传入的数组不为空，否则抛出业务异常信息
     *
     * @param array        数组
     * @param errorCode    错误码
     * @param errorMessage 错误提示
     */
    public static void notEmpty(Object[] array, Integer errorCode, String errorMessage) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(errorCode, errorMessage);
        }
    }

    /**
     * 断言传入的数组不为空，否则抛出业务异常信息
     *
     * @param array        数组
     * @param errorMessage 错误提示
     */
    public static void notEmpty(Object[] array, String errorMessage) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(errorMessage);
        }
    }

    /**
     * 断言传入的数组不为空，否则抛出业务异常信息
     *
     * @param array    数组
     * @param baseEnum 错误码
     */
    public static void notEmpty(Object[] array, BaseEnum baseEnum) {
        if (ArrayUtils.isEmpty(array)) {
            throw new BusinessException(baseEnum);
        }
    }
}