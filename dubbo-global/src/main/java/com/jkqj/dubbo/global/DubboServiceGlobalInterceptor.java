package com.jkqj.dubbo.global;


import com.jkqj.dubbo.global.handler.DubboGlobalExceptionHandler;
import com.jkqj.dubbo.global.handler.GlobalExceptionHandlerHelper;
import com.jkqj.dubbo.global.validate.DubboValidateHelper;
import com.jkqj.dubbo.global.validate.DubboValidated;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 拦截dubbo service的拦截器，用于参数验证及异常错误处理
 *
 * @author rolandhe
 */
@Aspect
@Slf4j
public class DubboServiceGlobalInterceptor implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        // 自动从spring容器抓取全局的异常处理器
        Map<String, Object> objectMap = applicationContext.getBeansWithAnnotation(DubboGlobalExceptionHandler.class);
        GlobalExceptionHandlerHelper.accept(objectMap);
    }


    @Around("execution(* com.jkqj..rpc.server..*.*(..)) && @target(org.apache.dubbo.config.annotation.DubboService)")
    public Object aroundInvoke(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String fullMethodName = getMethodFullName(methodSignature.getMethod().getName(), joinPoint.getTarget().getClass());
        boolean isValidate = needValidate(methodSignature.getMethod());
        log.info("start aop invoke:{}.", fullMethodName);
        try {
            if (isValidate) {
                DubboValidateHelper.validateParameter(joinPoint.getArgs());
            }
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            log.info("call {} 遇到异常，接下来交给异常处理器处理.", fullMethodName, throwable);
            Object handledValue = GlobalExceptionHandlerHelper.handleException(throwable, fullMethodName);
            if (GlobalExceptionHandlerHelper.isHandled(handledValue)) {
                log.info("全局异常处理器处理完成:{}", fullMethodName);
                return handledValue;
            }
            throw throwable;
        }
    }

    private String getMethodFullName(String methodName, Class<?> targetClass) {
        DubboService dubboService = targetClass.getAnnotation(DubboService.class);
        return String.format("%s.%s", dubboService.interfaceClass().getName(), methodName);
    }

    private boolean needValidate(Method method) {
        DubboValidated dubboValidated = method.getAnnotation(DubboValidated.class);
        if (dubboValidated != null) {
            return true;
        }
        dubboValidated = method.getDeclaringClass().getAnnotation(DubboValidated.class);
        return dubboValidated != null;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
