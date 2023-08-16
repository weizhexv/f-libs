package com.jkqj.common.cache;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存注解
 *
 * @author cb
 * @date 2021-03-24
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LocalCacheable {

    /**
     * 缓存名称
     *
     * @return
     */
    String name() default "";

    /**
     * 过期时间数
     *
     * @return
     */
    long expire() default 3600;

    /**
     * 过期时间单位
     *
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 最大缓存数目
     *
     * @return
     */
    int maximumSize() default 500;

    /**
     * key字段名，在为multi缓存以及结果集为list的情况下使用
     *
     * @return
     */
    String keyField() default "id";

}