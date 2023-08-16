package com.jkqj.dubbo.gw.register.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GwMapping {
    String url();
    String[] passHeaders() default {};
    boolean login() default true;
    boolean inheritHeader() default true;
    String[] httpMethod() default {"POST"};
    String description() default "";
    String[] roles() default {};
    String timeout() default "";
}
