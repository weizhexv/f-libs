package com.jkqj.dubbo.global.handler;

import java.lang.annotation.*;

/**
 * 标注异常处理方法所能处理的异常类
 *
 *
 * @author rolandhe
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DubboExceptionHandler {
    /**
     * 当前方法所能处理的异常类
     *
     * @return
     */
    Class<? extends Throwable> exceptionClass();
}
