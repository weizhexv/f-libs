package com.jkqj.dubbo.global.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常处理工具.<br>
 *
 * <ul>
 *     <li>接收全局处理异常类实例，缓存各个异常处理方法</li>
 *     <li>根据调用中的异常，交接给具体的异常处理方法</li>
 *     <li>返回异常处理结果</li>
 * </ul>
 *
 * @author rolandhe
 */
@Slf4j
public class GlobalExceptionHandlerHelper {

    private final static Map<Class, Method> cached = new LinkedHashMap<>();
    private final static Object NO_HANDLE_RESULT = new Object();
    private static Object bean;

    private GlobalExceptionHandlerHelper() {
    }

    public static void accept(Map<String, Object> beanMap) {
        if (beanMap.size() == 0) {
            log.warn("没有配置全局的dubbo异常处理。");
            return;
        }
        if (beanMap.size() > 1) {
            String beanNames = StringUtils.join(beanMap.keySet(), ",");
            throw new RuntimeException("存在多个全局异常处理bean:" + beanNames);
        }
        Map.Entry<String, Object> beanEntry = beanMap.entrySet().iterator().next();
        log.info("use 全局异常处理bean:{}", beanEntry.getKey());

        Method[] methods = beanEntry.getValue().getClass().getMethods();


        for (Method m : methods) {
            DubboExceptionHandler dubboExceptionHandler = m.getAnnotation(DubboExceptionHandler.class);
            if (dubboExceptionHandler == null) {
                continue;
            }
            cached.put(dubboExceptionHandler.exceptionClass(), m);
        }
        bean = beanEntry.getValue();
    }

    public static Object handleException(Throwable throwable, String fullMethodName) {
        Method method = cached.get(throwable.getClass());

        if (method == null) {
            for (Map.Entry<Class, Method> entry : cached.entrySet()) {
                if (entry.getKey().isAssignableFrom(throwable.getClass())) {
                    method = entry.getValue();
                    break;
                }
            }
        }

        if (method == null) {
            return NO_HANDLE_RESULT;
        }
        try {
            Object handledValue = method.invoke(bean, throwable, fullMethodName);
            return handledValue;
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.warn("met err to handle exception:{}", fullMethodName, e);
            return NO_HANDLE_RESULT;
        }
    }

    public static boolean isHandled(Object v) {
        return v != NO_HANDLE_RESULT;
    }
}
