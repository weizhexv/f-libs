package com.jkqj.dubbo.common.timeout;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.extension.SpringExtensionInjector;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.context.ApplicationContext;

@Slf4j
final class ConfHolder {
    private static volatile ApplicationContext applicationContext;
    private static volatile MethodTimeoutConfProvider methodTimeoutConfProvider;

    private static final MethodTimeoutConfProvider INVALID_PROVIDER = methodUrl -> null;

    private ConfHolder() {

    }


    private static ApplicationContext getSpringApplicationContext() {
        if (applicationContext != null) {
            return applicationContext;
        }
        applicationContext = SpringExtensionInjector.get(ApplicationModel.defaultModel()).getContext();
        return applicationContext;
    }

    static MethodTimeoutConfProvider getMethodTimeoutConfProvider() {
        if (methodTimeoutConfProvider == INVALID_PROVIDER) {
            return null;
        }
        if (methodTimeoutConfProvider != null) {
            return methodTimeoutConfProvider;
        }
        ApplicationContext ctx = getSpringApplicationContext();
        if (ctx == null) {
            return null;
        }

        try {
            MethodTimeoutConfProvider temp = ctx.getBean(MethodTimeoutConfProvider.class);
            log.info("load MethodTimeoutConfProvider,ok");
            methodTimeoutConfProvider = temp;
            return temp;
        } catch (RuntimeException e) {
            log.error("get MethodTimeoutConfProvider error", e);
            methodTimeoutConfProvider = INVALID_PROVIDER;
            return null;
        }
    }
}
