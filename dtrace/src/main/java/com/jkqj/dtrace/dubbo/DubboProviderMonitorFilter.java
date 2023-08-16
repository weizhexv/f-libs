package com.jkqj.dtrace.dubbo;

import com.jkqj.dtrace.context.ReqRunContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.UUID;

@Activate(group = CommonConstants.PROVIDER)
@Slf4j
public class DubboProviderMonitorFilter extends MonitorFilter implements Filter {

    @Override
    protected void processPassedInfo(Invocation invocation) {
        String traceId = (String)RpcContext.getServiceContext().getObjectAttachment(ReqRunContext.TRACE_ID_KEY);
        if (StringUtils.isEmpty(traceId)) {
            traceId = UUID.randomUUID().toString() + "-CR";
        }

        MDC.put(ReqRunContext.TRACE_ID_KEY, traceId);

        ReqRunContext.put(ReqRunContext.TRACE_ID_KEY, traceId);

        Object v = RpcContext.getServiceContext().getObjectAttachment(ReqRunContext.BIZ_TYPES_KEY);
        ReqRunContext.put(ReqRunContext.BIZ_TYPES_KEY, v);

        v = RpcContext.getServiceContext().getObjectAttachment(ReqRunContext.UID_KEY);
        ReqRunContext.put(ReqRunContext.UID_KEY, v);

        MDC.put(ReqRunContext.UID_KEY, v == null?"null" : v.toString());

        v = RpcContext.getServiceContext().getObjectAttachment(ReqRunContext.COMPANY_KEY);
        ReqRunContext.put(ReqRunContext.COMPANY_KEY, v);

        v = RpcContext.getServiceContext().getObjectAttachment(ReqRunContext.TOKEN_KEY);
        ReqRunContext.put(ReqRunContext.TOKEN_KEY, v);

        v = RpcContext.getServiceContext().getObjectAttachment(ReqRunContext.PLATFORM_KEY);
        ReqRunContext.put(ReqRunContext.PLATFORM_KEY, v);

        v = RpcContext.getServiceContext().getObjectAttachment(ReqRunContext.OS_KEY);
        ReqRunContext.put(ReqRunContext.OS_KEY, v);

        v = RpcContext.getServiceContext().getObjectAttachment(ReqRunContext.DEVICE_ID_KEY);
        ReqRunContext.put(ReqRunContext.DEVICE_ID_KEY, v);

        v = RpcContext.getServiceContext().getObjectAttachment(ReqRunContext.APP_VERSION);
        ReqRunContext.put(ReqRunContext.APP_VERSION, v);

        v = RpcContext.getServiceContext().getObjectAttachment(ReqRunContext.APP_VSN);
        ReqRunContext.put(ReqRunContext.APP_VSN, v);

        v = RpcContext.getServiceContext().getObjectAttachment(ReqRunContext.OP_ID);
        ReqRunContext.put(ReqRunContext.OP_ID, v);

        v = RpcContext.getServiceContext().getObjectAttachment(ReqRunContext.ROLES);
        ReqRunContext.put(ReqRunContext.ROLES, v);
    }

    @Override
    protected String side() {
        return CommonConstants.PROVIDER;
    }

    @Override
    protected void clearContextInfo() {
        ReqRunContext.clear();
        MDC.remove(ReqRunContext.TRACE_ID_KEY);
        MDC.remove(ReqRunContext.UID_KEY);
    }

    @Override
    protected Logger getCurrentLogger() {
        return log;
    }
}