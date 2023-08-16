package com.jkqj.dubbo.common.timeout;

public class LocalMethodTimeoutConfProvider implements MethodTimeoutConfProvider{
    private static final ThreadLocal<DubboMethodConf> ONCE = ThreadLocal.withInitial(() -> null);
    @Override
    public DubboMethodConf getMethodTimeout(String methodUrl) {
        DubboMethodConf once = ONCE.get();
        if(once != null){
            return once;
        }
        return matchMethodTimeout(methodUrl);
    }

    @Override
    public void setupOnceTimeout(Integer timeout,Integer retries) {
        DubboMethodConf dubboMethodConf = new DubboMethodConf();
        dubboMethodConf.setTimeout(timeout);
        dubboMethodConf.setRetries(retries);
        ONCE.set(dubboMethodConf);
    }

    @Override
    public void clearOnceTimeout() {
        ONCE.remove();
    }

    protected DubboMethodConf matchMethodTimeout(String methodUrl){

        // todo
        return null;
    }
}
