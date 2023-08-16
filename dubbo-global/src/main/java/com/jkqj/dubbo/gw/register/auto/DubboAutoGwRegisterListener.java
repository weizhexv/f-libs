package com.jkqj.dubbo.gw.register.auto;

import com.jkqj.dubbo.gw.register.annotations.GwAutoRegister;
import com.jkqj.dubbo.gw.register.annotations.GwMapping;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DubboAutoGwRegisterListener implements ApplicationListener<ApplicationStartedEvent> {

    private final AutoRegister autoRegister;

    public DubboAutoGwRegisterListener(AutoRegister autoRegister) {
        this.autoRegister = autoRegister;
    }


    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        Map<String, Object> dubboServiceBeanMap = applicationStartedEvent.getApplicationContext().getBeansWithAnnotation(DubboService.class);
        List<DubboMappingMethod> list = new ArrayList<>();
        dubboServiceBeanMap.forEach((beanName, bean) -> {
            log.info("dubbo-service: bean = {},className={}", beanName, bean.getClass().getName());
            acceptDubboBean(bean, list);
        });
        autoRegister.register(list);
    }

    private void acceptDubboBean(Object bean, final List<DubboMappingMethod> methodList) {
        Class<?> targetDubboBeanClass = bean.getClass();
        if (AopUtils.isAopProxy(bean)) {
            targetDubboBeanClass = AopUtils.getTargetClass(bean);
        }


        GwAutoRegister gwAutoRegister = targetDubboBeanClass.getAnnotation(GwAutoRegister.class);
        if (gwAutoRegister == null) {
            return;
        }

        DubboService dubboService = targetDubboBeanClass.getAnnotation(DubboService.class);
        Class interfaceClass = dubboService.interfaceClass();
        String version = dubboService.version();


        Method[] methods = targetDubboBeanClass.getMethods();
        for (Method m : methods) {
            GwMapping gwMapping = m.getAnnotation(GwMapping.class);
            if (gwMapping == null) {
                continue;
            }
            DubboMappingMethod dubboMappingMethod = build(interfaceClass.getName(), m, version, gwAutoRegister, gwMapping);
            methodList.add(dubboMappingMethod);
        }
    }

    private String catUrl(String root, String url) {
        if (root.endsWith("/")) {
            return root + url;
        }

        return root + "/" + url;
    }

    private DubboMappingMethod build(String interfaceName, Method m, String version, GwAutoRegister gwAutoRegister, GwMapping gwMapping) {
        DubboMappingMethod dubboMappingMethod = new DubboMappingMethod();
        if (gwMapping.url().startsWith("/")) {
            dubboMappingMethod.setUrl(gwMapping.url());
        } else {
            dubboMappingMethod.setUrl(catUrl(gwAutoRegister.urlRoot(), gwMapping.url()));
        }

        dubboMappingMethod.setMethods(Arrays.stream(gwMapping.httpMethod()).collect(Collectors.toList()));

        dubboMappingMethod.setLogin(gwMapping.login());

        Set<String> passHeaderList = new LinkedHashSet<>();
        Arrays.stream(gwMapping.passHeaders()).forEach(s -> passHeaderList.add(s));
        if (gwMapping.inheritHeader()) {
            Arrays.stream(gwAutoRegister.passHeaders()).forEach(s -> passHeaderList.add(s));
        }
        dubboMappingMethod.setPassHeaders(passHeaderList.stream().collect(Collectors.toList()));
        dubboMappingMethod.setDescription(gwMapping.description());

        dubboMappingMethod.setRoles(Arrays.stream(gwMapping.roles()).collect(Collectors.toList()));

        dubboMappingMethod.setTimeout(gwMapping.timeout());

        DubboMappingMethod.ProxyPass proxyPass = new DubboMappingMethod.ProxyPass();
        dubboMappingMethod.setProxyPass(proxyPass);

        proxyPass.setDubboInterface(interfaceName);
        proxyPass.setDubboMethod(m.getName());
        proxyPass.setVersion(version);
        Class<?>[] paramClasses = m.getParameterTypes();
        if (paramClasses.length > 0) {
            proxyPass.setParameterClassName(paramClasses[0].getName());
        } else {
            proxyPass.setParameterClassName("");
        }

        return dubboMappingMethod;
    }

}
