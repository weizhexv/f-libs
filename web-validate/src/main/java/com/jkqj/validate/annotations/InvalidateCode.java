package com.jkqj.validate.annotations;

import java.lang.annotation.*;

/**
 * 用于指定参数验证失败时的返回错误码, 缺省为Integer.MIN_VALUE，表示无效
 *
 * @author rolandhe
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InvalidateCode {
    int value() default Integer.MIN_VALUE;

    String message() default "";
}
