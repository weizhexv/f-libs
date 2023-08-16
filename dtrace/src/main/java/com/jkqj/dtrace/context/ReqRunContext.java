package com.jkqj.dtrace.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 描述一次请求的上下文信息.
 * <p>
 * 包括trace id及其他信息
 *
 * @author rolandhe
 */
public class ReqRunContext {
    public static final String TRACE_ID_KEY = "trace-id";
    public static final String PLATFORM_KEY = "platform";
    public static final String OS_KEY = "os";
    public static final String DEVICE_ID_KEY = "device-id";

//    public static final String HEADERS_KEY = "in_header";
    public static final String UID_KEY = "uid";
    public static final String BIZ_TYPES_KEY = "biz-types";
    public static final String COMPANY_KEY = "company-id";
    public static final String TOKEN_KEY = "reta-token";
    public static final String OP_ID = "op-id";

    public static final String APP_VERSION="app-version";

    public static final String APP_VSN = "app-vsn";
    public static final String ROLES = "roles";



    private ReqRunContext() {
    }

    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(() -> new HashMap<>());

//    public static void putTraceId(String traceId) {
//        CONTEXT.get().put(TRACE_ID_KEY, traceId);
//    }

    public static void clearTraceId() {
        CONTEXT.get().remove(TRACE_ID_KEY);
    }


    public static String getTraceId() {
        return (String) CONTEXT.get().get(TRACE_ID_KEY);
    }

    public static void clear() {
        CONTEXT.get().clear();
    }

    public static void put(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    public static Long getUserId() {
       return (Long) CONTEXT.get().get(UID_KEY);
    }

    public static Integer getUserBizTypes() {
        return (Integer) CONTEXT.get().get(BIZ_TYPES_KEY);
    }

    public static Long getBUserCompanyId() {
        return (Long) CONTEXT.get().get(COMPANY_KEY);
    }
    public  static String getCurrentToken() {
        return (String) CONTEXT.get().get(TOKEN_KEY);
    }

    public static Long getOpId() {
        return (Long)CONTEXT.get().get(OP_ID);
    }

    public static String getPlatform(){
        return (String)CONTEXT.get().get(PLATFORM_KEY);
    }

    public static String getOS(){
        return (String)CONTEXT.get().get(OS_KEY);
    }

    public static String getDeviceId() {
        return (String) CONTEXT.get().get(DEVICE_ID_KEY);
    }

    public static String getAppVersion() {
        return (String) CONTEXT.get().get(APP_VERSION);
    }
    public static String getAppVsn() {
        return (String) CONTEXT.get().get(APP_VSN);
    }

    public static Set<String> getRoles() {
        return (Set<String>) CONTEXT.get().get(ROLES);
    }
}
