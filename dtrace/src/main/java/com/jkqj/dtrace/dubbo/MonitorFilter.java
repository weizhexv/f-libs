package com.jkqj.dtrace.dubbo;


import com.jkqj.dtrace.annotions.LogLevel;
import com.jkqj.dtrace.monitor.MonitorOutputContext;
import com.jkqj.dtrace.output.MonitorOutputHelper;
import org.apache.dubbo.config.spring.extension.SpringExtensionInjector;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class MonitorFilter extends MonitorOutputContext implements Filter {
    private static final Map<String, LogLevel> logLevelCache = new ConcurrentHashMap<>();
    private volatile ApplicationContext applicationContext;


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        Class<?> clazz = invoker.getInterface();

        if (clazz.getName().startsWith("org.apache.dubbo.")) {
            getCurrentLogger().info("忽略拦截dubbo 内置接口:{}", clazz.getName());
            return invoker.invoke(invocation);
        }

        String methodName = invocation.getMethodName();
        ensureLogout();
        String wholePath = genWholePath(clazz.getName() + "." + methodName,invoker);
        LogLevel logLevel = getMethodLogLevel(clazz, methodName);
        Result ret = null;
        Throwable e = null;
        long startTime = System.currentTimeMillis();
        try {
            processPassedInfo(invocation);
            MonitorOutputHelper.logStart(ensureLogout(), logLevel, wholePath, invocation.getArguments());
            ret = invoker.invoke(invocation);
            return ret;
        } catch (RpcException rpcException) {
            getCurrentLogger().info("call {} error.", wholePath, rpcException);
            e = rpcException;
            throw rpcException;
        } finally {
            Object inValue = null;
            if (ret != null) {
                if (ret.hasException()) {
                    e = ret.getException();
                } else {
                    inValue = ret.getValue();
                }
            }
            MonitorOutputHelper.logEnd(ensureLogout(), logLevel, wholePath, inValue, System.currentTimeMillis() - startTime, e);
            clearContextInfo();
        }
    }

    protected String genWholePath(String wholePath,Invoker<?> invoker) {
        return wholePath;
    }

    protected abstract void processPassedInfo(Invocation invocation);


    protected void clearContextInfo() {

    }


    private LogLevel getMethodLogLevel(Class<?> clazz, String methodName) {
        String fullPath = clazz.getName() + "." + methodName;
        LogLevel logLevel = logLevelCache.get(fullPath);
        if (logLevel != null) {
            return logLevel;
        }

        Method method = getMethodByName(clazz, methodName);
        ensureLogLevelProvider();

        logLevel = ensureLogLevelProvider().provide(clazz, method);
        logLevelCache.putIfAbsent(fullPath, logLevel);
        return logLevel;
    }


    private Method getMethodByName(Class clazz, String methodName) {
        Method[] all = clazz.getMethods();
        for (Method m : all) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        throw new RuntimeException("can't find method : " + methodName);
    }


    @Override
    protected ApplicationContext getSpringApplicationContext() {
        if (applicationContext != null) {
            return applicationContext;
        }
        applicationContext = SpringExtensionInjector.get(ApplicationModel.defaultModel()).getContext();
//        SpringExtensionInjector springExtensionInjector = (SpringExtensionInjector) ApplicationModel.defaultModel().getExtension(ExtensionInjector.class, SpringExtensionInjector.NAME);
//        applicationContext = springExtensionInjector.getContext();
        return applicationContext;
    }
}
