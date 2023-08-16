package com.jkqj.dubbo.generic.reference;

import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

/**
 * ReferenceConfig很重，需要缓存。ReferenceConfig缓存工具
 *
 * @author rolandhe
 */
class ReferenceConfigCache {
    private static final Logger log = LoggerFactory.getLogger(ReferenceConfigCache.class);

    private static final ConcurrentMap<String, ReferenceConfigHolder> CACHE = new ConcurrentHashMap<>();

    /**
     * 有时会获取到已经被destroy的ReferenceConfig，需要重新获取
     *
     * @param group
     * @param interfaceName
     * @param version
     * @param callback
     * @param bad
     * @return
     */
    static ReferenceConfig<GenericService> getReferenceConfig(String group, String interfaceName, String version, BuildCallback callback, ReferenceConfig<GenericService> bad) {
        String key = generateKey(group, interfaceName, version);
        synchronized (ReferenceConfigCache.class) {
            ReferenceConfigHolder referenceConfigHolder = CACHE.get(key);
            if (referenceConfigHolder != null && referenceConfigHolder.referenceConfig == bad) {
                try {
                    bad.destroy();
                } catch (RuntimeException e) {
                    log.info("destroy bad ReferenceConfig error,{}.{}", group, interfaceName, e);
                }
                CACHE.remove(key);
            }
        }

        return getReferenceConfig(group, interfaceName, version, callback);
    }


    /**
     * 获取ReferenceConfig
     *
     * @param group
     * @param interfaceName
     * @param version
     * @param callback
     * @return
     */
    static ReferenceConfig<GenericService> getReferenceConfig(String group, String interfaceName, String version, BuildCallback callback) {
        String key = generateKey(group, interfaceName, version);
        ReferenceConfigHolder referenceConfigHolder = CACHE.get(key);
        if (referenceConfigHolder != null) {
            return referenceConfigHolder.getReferenceConfigWaiting();
        }

        ReferenceConfigHolder holder = new ReferenceConfigHolder();
        ReferenceConfigHolder old = CACHE.putIfAbsent(key, holder);
        if (old == null) {
            // build
            ReferenceConfig<GenericService> newReferenceConfig = callback.build(group, interfaceName, version);
            return holder.safeAcceptReferenceConfig(newReferenceConfig);
        }

        return old.getReferenceConfigWaiting();
    }

    static void destroy() {
        for (Map.Entry<String, ReferenceConfigHolder> entry : CACHE.entrySet()) {
            if (entry.getValue().referenceConfig != null) {
                entry.getValue().referenceConfig.destroy();
                entry.getValue().referenceConfig.getApplicationModel().destroy();
            }
        }
        CACHE.clear();
    }


    private static class ReferenceConfigHolder {
        private volatile ReferenceConfig<GenericService> referenceConfig;
        private final CountDownLatch countDownLatch = new CountDownLatch(1);


        ReferenceConfigHolder() {

        }

        ReferenceConfig<GenericService> getReferenceConfigWaiting() {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return referenceConfig;
        }
        ReferenceConfig<GenericService> safeAcceptReferenceConfig(ReferenceConfig<GenericService> referenceConfig) {
            this.referenceConfig = referenceConfig;
            countDownLatch.countDown();
            return referenceConfig;
        }
    }


    private static String generateKey(String group, String interfaceName, String version) {
        if (group == null) {
            group = "";
        }
        if (version == null) {
            version = "";
        }
        return String.format("%s/%s/%s", group, interfaceName, version);
    }
}
