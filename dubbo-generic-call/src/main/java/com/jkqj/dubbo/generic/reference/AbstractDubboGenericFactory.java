package com.jkqj.dubbo.generic.reference;

import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;

public abstract class AbstractDubboGenericFactory implements DubboGenericFactory{
    private final BuildCallback buildCallback = (group, interfaceName, version) -> AbstractDubboGenericFactory.this.build(group, interfaceName,version);

    private boolean destroyed;
    @Override
    public ReferenceConfig<GenericService> factory(String group, String interfaceName, String version) {
        return ReferenceConfigCache.getReferenceConfig(group, interfaceName,version, buildCallback);
    }

    @Override
    public ReferenceConfig<GenericService> factory(String group, String interfaceName, String version, ReferenceConfig<GenericService> bad) {
        return ReferenceConfigCache.getReferenceConfig(group, interfaceName,version, buildCallback, bad);
    }


    @Override
    public synchronized void destroy() {
        if(destroyed) {
            return;
        }
        ReferenceConfigCache.destroy();
        destroyed = true;
    }

    protected abstract ReferenceConfig<GenericService> build(String group, String interfaceName,String version);
}
