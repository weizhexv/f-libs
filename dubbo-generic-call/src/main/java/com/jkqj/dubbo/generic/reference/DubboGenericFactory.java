package com.jkqj.dubbo.generic.reference;

import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;

public interface DubboGenericFactory {
    ReferenceConfig<GenericService> factory(String group, String interfaceName, String version);
    ReferenceConfig<GenericService> factory(String group, String interfaceName,String version, ReferenceConfig<GenericService> bad);
    void destroy();
}
