package com.jkqj.common.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 枚举检查注解
 *
 * @author cb
 * @date 2021-12-27
 */
@Documented
@Constraint(validatedBy = EnumCheckValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(EnumCheck.List.class)
public @interface EnumCheck {

    /**
     * 提示信息
     *
     */
    String message() default "{javax.validation.constraints.EnumCheck.message}";

    /**
     * 分组
     *
     */
    Class<?>[] groups() default { };

    /**
     * 扩展对象
     *
     */
    Class<? extends Payload>[] payload() default { };

    /**
     * 枚举类
     */
    Class<? extends Enum> clazz();

    /**
     * 指定验证某列
     *
     * @return
     */
    String fieldName() default "";

    /**
     * Defines several {@code @In} constraints on the same element.
     *
     * @see EnumCheck
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        /**
         * In数组
         */
        EnumCheck[] value();
    }

}