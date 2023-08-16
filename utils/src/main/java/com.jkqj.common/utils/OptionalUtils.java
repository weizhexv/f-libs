package com.jkqj.common.utils;

import com.jkqj.common.enums.BaseEnum;
import com.jkqj.common.exception.BusinessException;

import java.util.Optional;

/**
 * Optional工具类
 *
 * @author cb
 * @date 2021-12-26
 */
public final class OptionalUtils {

    /**
     * 获取Optional里面的值，没有则抛出自定义业务异常
     *
     * @param opt
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> T get(Optional<T> opt, String errorMessage) {
        return opt.orElseThrow(() -> new BusinessException(errorMessage));
    }

    /**
     * 获取Optional里面的值，没有则抛出自定义业务异常
     *
     * @param opt
     * @param errorCode
     * @param errorMessage
     * @param <T>
     * @return
     */
    public static <T> T get(Optional<T> opt, Integer errorCode, String errorMessage) {
        return opt.orElseThrow(() -> new BusinessException(errorCode, errorMessage));
    }

    /**
     * 获取Optional里面的值，没有则抛出自定义业务异常
     *
     * @param opt
     * @param baseEnum
     * @param <T>
     * @return
     */
    public static <T> T get(Optional<T> opt, BaseEnum baseEnum) {
        return opt.orElseThrow(() -> new BusinessException(baseEnum));
    }

}