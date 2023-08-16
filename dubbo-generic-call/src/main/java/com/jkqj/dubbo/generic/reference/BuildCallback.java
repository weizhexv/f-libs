package com.jkqj.dubbo.generic.reference;

import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;

/**
 * 构造ReferenceConfig的回调
 * @author rolandhe
 */
interface BuildCallback {

    ReferenceConfig<GenericService> build(String group,String interfaceName,String version);
}
