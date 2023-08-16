package com.jkqj.dubbo.global.validate;

import java.lang.annotation.*;

/**
 * dubbo 服务实现需要自动验证参数
 *
 * @author rolandhe
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DubboValidated {
}
