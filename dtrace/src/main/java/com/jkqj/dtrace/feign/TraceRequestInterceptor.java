package com.jkqj.dtrace.feign;

import com.jkqj.dtrace.context.ReqRunContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class TraceRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header(ReqRunContext.TRACE_ID_KEY,ReqRunContext.getTraceId());
        template.header(ReqRunContext.PLATFORM_KEY,ReqRunContext.getPlatform());
        template.header(ReqRunContext.OS_KEY,ReqRunContext.getOS());
        template.header(ReqRunContext.APP_VERSION,ReqRunContext.getAppVersion());
        template.header(ReqRunContext.APP_VSN,ReqRunContext.getAppVsn());
    }
}
