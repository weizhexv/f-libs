package com.jkqj.common.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * 验证工具类
 *
 * @author cb
 * @date 2021/11/25
 */
public final class ValidationUtils {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[13456789]\\d{9}$");
    private static final String EMAIL_REGEX = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    /**
     * 验证手机号
     */
    public static boolean isMobile(String mobile) {
        return MOBILE_PATTERN.matcher(mobile).matches();
    }

    public static boolean isPositive(Long num) {
        return num != null && num > 0;
    }

    public static boolean isPositive(Integer num) {
        return num != null && num > 0;
    }

    public static void notNull(Object obj, Consumer<Object> consumer) {
        if (obj != null) {
            consumer.accept(obj);
        }
    }

    public static void notBlank(String str, Consumer<String> consumer) {
        if (StringUtils.isNotBlank(str)) {
            consumer.accept(str);
        }
    }

    public static <T> void notEmpty(Collection<T> coll, Consumer<Collection<T>> consumer) {
        if (CollectionUtils.isNotEmpty(coll)) {
            consumer.accept(coll);
        }
    }

    public static <T> void notEmpty(T[] arr, Consumer<T[]> consumer) {
        if (ArrayUtils.isNotEmpty(arr)) {
            consumer.accept(arr);
        }
    }

    public static <K, V> void notEmpty(Map<K, V> map, Consumer<Map<K, V>> consumer) {
        if (MapUtils.isNotEmpty(map)) {
            consumer.accept(map);
        }
    }

    public static boolean isIdentity(String identity) {
        if (StringUtils.isBlank(identity)) {
            return false;
        }
        var length = StringUtils.length(identity);

        return length == 15 || length == 18;
    }

    /**
     * 利用正则表达式检查字符串是否为邮箱格式
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }

}
