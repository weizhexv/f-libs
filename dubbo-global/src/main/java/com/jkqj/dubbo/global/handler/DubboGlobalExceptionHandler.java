package com.jkqj.dubbo.global.handler;

import java.lang.annotation.*;

/**
 * 标注全局处理dubbo异常的类
 *
 * @author rolandhe
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DubboGlobalExceptionHandler {
}
