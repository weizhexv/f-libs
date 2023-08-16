package com.jkqj.common.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author xuweizhe@reta-inc.com
 * @date 2022/4/11
 * @description
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {IsIdentityValidator.class})
public @interface IsIdentity {
    String message() default "身份证号格式错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
