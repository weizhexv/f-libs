package com.jkqj.dubbo.generic.reference;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.HashMap;
import java.util.Map;

/**
 * 泛化调用ReferenceConfig生成工厂,需要在spring中注入，且需要配置destroy方法，用于释放资源
 *
 * @author rolandhe
 */
public class ApiModeDubboGenericFactory extends AbstractDubboGenericFactory implements DubboGenericFactory {
    private final GenericConsumerConfig genericConsumerConfig;
    private final ApplicationConfig applicationConfig;
    private final RegistryConfig registryConfig;


    public ApiModeDubboGenericFactory(GenericConsumerConfig genericConsumerConfig) {
        this.genericConsumerConfig = genericConsumerConfig;
        applicationConfig = new ApplicationConfig();
        applicationConfig.setName(genericConsumerConfig.getAppName());
        applicationConfig.setEnableFileCache(true);
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(genericConsumerConfig.getRegisterAddress());
        this.registryConfig = registryConfig;
    }


    @Override
    protected ReferenceConfig<GenericService> build(String group, String interfaceName, String version) {

        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();

        registryConfig.getApplicationModel().getApplicationConfigManager().setApplication(applicationConfig);
        referenceConfig.setProtocol(genericConsumerConfig.getProtocol());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("threadpool", genericConsumerConfig.getThreadPoolType());
        parameters.put("threads", genericConsumerConfig.getThreads() + "");
        parameters.put("threadname", genericConsumerConfig.getThreadName());
        referenceConfig.setRegistry(registryConfig);
        referenceConfig.setTimeout(genericConsumerConfig.getTimeout());
        referenceConfig.setParameters(parameters);

        referenceConfig.setGroup(group);
        referenceConfig.setInterface(interfaceName);
        referenceConfig.setCheck(false);
        if (version != null && version.length() > 0) {
            referenceConfig.setVersion(version);
        }

        referenceConfig.setGeneric("true");
        referenceConfig.setAsync(false);

        return referenceConfig;
    }
}
