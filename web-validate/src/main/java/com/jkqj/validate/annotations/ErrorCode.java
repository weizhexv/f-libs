package com.jkqj.validate.annotations;


import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * @author lingchangmeng
 * @version 1.0.0
 * @ClassName ErrorCode.java
 * @Description 自定义ErrorCode注解
 * @createTime 2022/01/06 16:39:00
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
public @interface ErrorCode {

    String code() default "200";

    String fieldName() default "字段名字";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
