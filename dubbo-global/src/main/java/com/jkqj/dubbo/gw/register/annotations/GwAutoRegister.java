package com.jkqj.dubbo.gw.register.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GwAutoRegister {
    String urlRoot() default "/";
    String[] passHeaders() default {"platform","os","app-version"};
}
