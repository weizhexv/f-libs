package com.jkqj.mybatis.extension.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoFillBackstage {
    String backstageColumn() default "op_id";
}
