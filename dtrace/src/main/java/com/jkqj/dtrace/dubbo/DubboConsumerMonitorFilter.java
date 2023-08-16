package com.jkqj.dtrace.dubbo;

import com.jkqj.dtrace.context.ReqRunContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;

@Activate(group = CommonConstants.CONSUMER)
@Slf4j
public class DubboConsumerMonitorFilter extends MonitorFilter implements Filter {

    @Override
    protected void processPassedInfo(Invocation invocation) {
        String traceId = ReqRunContext.getTraceId();
        if (StringUtils.isEmpty(traceId)) {
            return;
        }
        RpcContext.getServiceContext().setObjectAttachment(ReqRunContext.TRACE_ID_KEY, traceId);
        RpcContext.getServiceContext().setObjectAttachment(ReqRunContext.PLATFORM_KEY, ReqRunContext.getPlatform());
        RpcContext.getServiceContext().setObjectAttachment(ReqRunContext.OS_KEY, ReqRunContext.getOS());
        RpcContext.getServiceContext().setObjectAttachment(ReqRunContext.APP_VERSION, ReqRunContext.getAppVersion());
        RpcContext.getServiceContext().setObjectAttachment(ReqRunContext.APP_VSN, ReqRunContext.getAppVsn());
    }

    @Override
    protected String side() {
        return CommonConstants.CONSUMER;
    }

    @Override
    protected String genWholePath(String wholePath, Invoker<?> invoker) {
        return wholePath + "#" + invoker.getUrl().getAddress();
    }

    @Override
    protected Logger getCurrentLogger() {
        return log;
    }
}