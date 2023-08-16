package com.jkqj.common.log;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 参数日志注解
 *
 * @author cb
 * @date 2020-10-31
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamsLog {

    /**
     * 描述
     */
    @AliasFor("desc")
    String value() default "";

    /**
     * 描述
     */
    @AliasFor("value")
    String desc() default "";

    /**
     * 是否包含返回值
     */
    boolean includeRetVal() default true;

}