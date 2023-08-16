package com.jkqj.common.log;

import cn.hutool.core.annotation.AnnotationUtil;
import com.google.common.collect.Maps;
import com.jkqj.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 参数日志切面
 *
 * @author cb
 * @date 2020-11-04
 */
@Slf4j
@Aspect
public class ParamsLogAspect {

    @AfterReturning(value = "@annotation(com.jkqj.common.log.ParamsLog)", returning = "retVal")
    public void afterReturningLog(JoinPoint joinPoint, Object retVal) {
        Signature joinPointSignature = joinPoint.getSignature();
        if (!(joinPointSignature instanceof MethodSignature)) {
            return;
        }
        MethodSignature methodSignature = (MethodSignature) joinPointSignature;
        Method method = methodSignature.getMethod();

        // 获取方法声明注解
        ParamsLog paramsLog = AnnotationUtil.getAnnotation(method, ParamsLog.class);

        if (paramsLog == null) {
            log.warn("paramsLog为空");

            return;
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();

        if (paramsLog.includeRetVal() && retVal != null) {
            log.info("{}.{} {}, 入参：{}, 出参：{}", className, method.getName(), paramsLog.desc(),
                    JsonUtils.toJson(getRequestParams(joinPoint, method)), JsonUtils.toJson(retVal));
        } else {
            log.info("{}.{} {}, 入参：{}", className, method.getName(), paramsLog.desc(),
                    JsonUtils.toJson(getRequestParams(joinPoint, method)));
        }
    }

    private Object getRequestParams(JoinPoint joinPoint, Method method) {
        Object[] args = joinPoint.getArgs();
        if (ArrayUtils.isEmpty(args)) {
            return null;
        }

        if (args.length == 1) {
            return args[0];
        }

        Parameter[] parameters = method.getParameters();
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            params.put(parameters[i].getName(), args[i]);
        }

        return params;
    }

}