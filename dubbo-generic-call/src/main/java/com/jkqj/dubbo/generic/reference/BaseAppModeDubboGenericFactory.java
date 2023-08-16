package com.jkqj.dubbo.generic.reference;

import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;

public class BaseAppModeDubboGenericFactory extends AbstractDubboGenericFactory implements DubboGenericFactory{
    @Override
    protected ReferenceConfig<GenericService> build(String group, String interfaceName, String version) {
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setGroup(group);
        referenceConfig.setInterface(interfaceName);
        referenceConfig.setCheck(false);
        if(version != null && version.length() > 0) {
            referenceConfig.setVersion(version);
        }

        referenceConfig.setGeneric("true");
        referenceConfig.setAsync(false);

        return referenceConfig;
    }
}
