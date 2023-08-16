package com.jkqj.mod.issue.pub;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pub {
    String id() default "id";
    Class<?> poClass();
    String msgDbOutputBeanName() default "issueEventMapper";
}
