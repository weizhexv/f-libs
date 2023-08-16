package com.jkqj.validator.hibernate.ext;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CompanyIdConstraintValidator.class)
public @interface CompanyId {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
