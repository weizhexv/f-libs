package com.jkqj.dubbo.common.timeout;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

@Slf4j
@Activate(group = CommonConstants.CONSUMER,order = 1)
public class DubboClientTimeoutFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        MethodTimeoutConfProvider methodTimeoutConfProvider =  ConfHolder.getMethodTimeoutConfProvider();
        if(methodTimeoutConfProvider == null){
            return invoker.invoke(invocation);
        }
        Class<?> clazz = invoker.getInterface();

        if (clazz.getName().startsWith("org.apache.dubbo.")) {
            log.info("忽略拦截dubbo 内置接口:{}", clazz.getName());
            return invoker.invoke(invocation);
        }

        String moduleName =  invocation.getModuleModel().getModelName();

        String methodName = invocation.getMethodName();
        String wholePath = clazz.getName() + "." + methodName;

        if(!StringUtils.isEmpty(moduleName)){
            wholePath = moduleName + ":" + wholePath;
        }

        DubboMethodConf dubboMethodConf =  methodTimeoutConfProvider.getMethodTimeout(wholePath);

        if(dubboMethodConf != null) {
            if(dubboMethodConf.getTimeout() != null) {
                RpcContext.getClientAttachment().setObjectAttachment(CommonConstants.TIMEOUT_KEY, dubboMethodConf.getTimeout());
            }
            if(dubboMethodConf.getRetries() != null){
                RpcContext.getClientAttachment().setObjectAttachment(CommonConstants.RETRIES_KEY, dubboMethodConf.getRetries());
            }
        }
        return invoker.invoke(invocation);
    }
}
