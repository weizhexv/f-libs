package com.jkqj.common.cache;

import cn.hutool.core.annotation.AnnotationUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jkqj.common.utils.JsonUtils;
import com.jkqj.common.utils.Lambdas;
import com.jkqj.common.utils.MyPropertyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * guava缓存切面
 *
 * @author cb
 * @date 2021-03-24
 */
@Slf4j
@Aspect
public class GuavaCacheAspect {

    private static final Map<String, Cache<Object, Object>> cacheMap = Maps.newConcurrentMap();

    @Around(value = "@annotation(com.jkqj.common.cache.LocalCacheable)")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args[0] == null) {
            return joinPoint.proceed(args);
        }

        Signature joinPointSignature = joinPoint.getSignature();
        if (!(joinPointSignature instanceof MethodSignature)) {
            return joinPoint.proceed(args);
        }
        MethodSignature methodSignature = (MethodSignature) joinPointSignature;
        Method method = methodSignature.getMethod();

        // 获取方法声明注解
        LocalCacheable cacheable = AnnotationUtil.getAnnotation(method, LocalCacheable.class);
        if (cacheable == null) {
            log.warn("cacheable为空");

            return joinPoint.proceed(args);
        }

        String cacheName = StringUtils.isNotBlank(cacheable.name()) ? cacheable.name() :
                joinPoint.getTarget().getClass().getSimpleName() + "." + method.getName();
        log.debug("cacheName: {}", cacheName);

        Cache<Object, Object> cache = cacheMap.get(cacheName);
        if (cache == null) {
            cacheMap.putIfAbsent(cacheName, CacheBuilder.newBuilder()
                    .expireAfterWrite(cacheable.expire(), cacheable.timeUnit())
                    .maximumSize(cacheable.maximumSize()).build());
            cache = cacheMap.get(cacheName);
        }

        boolean isMapResult = "Map".equals(method.getReturnType().getSimpleName());

        return args[0] instanceof Collection ? getMultiCacheValues(joinPoint, cache, cacheable, cacheName, isMapResult) :
                getSingleCacheValue(joinPoint, cache, cacheName);
    }

    private static Object getSingleCacheValue(ProceedingJoinPoint joinPoint, Cache<Object, Object> cache,
                                              String cacheName) throws Throwable {
        Object[] args = joinPoint.getArgs();

        Object cacheValue = cache.getIfPresent(args[0]);
        if (cacheValue != null) {
            log.debug("命中{}缓存，{} : {}", cacheName, args[0], JsonUtils.toJson(cacheValue));

            return cacheValue;
        } else {
            log.debug("未命中{}缓存: {}", cacheName, args[0]);
        }

        Object result = joinPoint.proceed(args);
        if (result == null) {
            return null;
        }

        if (result instanceof Collection) {
            if (CollectionUtils.isNotEmpty((Collection<Object>) result)) {
                cache.put(args[0], result);
            }
        } else {
            cache.put(args[0], result);
        }

        return result;
    }

    private static Object getMultiCacheValues(ProceedingJoinPoint joinPoint, Cache<Object, Object> cache,
                                              LocalCacheable cacheable, String cacheName, boolean isMapResult) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Collection<Object> keys = (Collection<Object>) args[0];
        if (CollectionUtils.isEmpty((keys))) {
            return joinPoint.proceed(args);
        }
        log.debug("{} keys: {}", cacheName, JsonUtils.toJson(keys));

        return isMapResult ? getMapCacheValues(joinPoint, cache, cacheName) :
                getListCacheValues(joinPoint, cache, cacheable, cacheName);
    }

    private static Object getListCacheValues(ProceedingJoinPoint joinPoint, Cache<Object, Object> cache,
                                             LocalCacheable cacheable, String cacheName) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Collection<Object> keys = (Collection<Object>) args[0];
        Map<Object, Object> allPresent = cache.getAllPresent(keys);
        List<Object> returnValues = Lists.newArrayList();

        if (MapUtils.isNotEmpty(allPresent)) {
            Set<Object> hitKeys = allPresent.keySet();
            List<Object> notHitKeys = Lambdas.filterList(keys, key -> !hitKeys.contains(key));

            if (CollectionUtils.isEmpty(notHitKeys)) {
                log.debug("全部命中{}缓存: {}", cacheName, JsonUtils.toJson(keys));

                return Lists.newArrayList(allPresent.values());
            } else {
                returnValues.addAll(allPresent.values());
            }

            log.debug("未命中{}缓存的有: {}", cacheName, JsonUtils.toJson(notHitKeys));
            args[0] = notHitKeys;
        } else {
            log.debug("未命中任何{}缓存: {}", cacheName, JsonUtils.toJson(keys));
        }

        List<Object> listValues = (List<Object>) joinPoint.proceed(args);
        if (CollectionUtils.isEmpty(listValues)) {
            return returnValues;
        }

        returnValues.addAll(listValues);

        if (StringUtils.isEmpty(cacheable.keyField())) {
            log.warn("缓存注解未配置keyField");

            return returnValues;
        }

        cache.putAll(Lambdas.trans2Map(listValues, value -> MyPropertyUtils.getProperty(value, cacheable.keyField())));

        return returnValues;
    }

    private static Object getMapCacheValues(ProceedingJoinPoint joinPoint, Cache<Object, Object> cache,
                                            String cacheName) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Collection<Object> keys = (Collection<Object>) args[0];
        Map<Object, Object> allPresent = cache.getAllPresent(keys);
        Map<Object, Object> returnValues = Maps.newHashMap();

        if (MapUtils.isNotEmpty(allPresent)) {
            Set<Object> hitKeys = allPresent.keySet();
            List<Object> notHitKeys = Lambdas.filterList(keys, key -> !hitKeys.contains(key));

            if (CollectionUtils.isEmpty(notHitKeys)) {
                log.debug("全部命中{}缓存: {}", cacheName, JsonUtils.toJson(keys));

                return allPresent;
            } else {
                returnValues.putAll(allPresent);
            }

            log.debug("未命中{}缓存的有: {}", cacheName, JsonUtils.toJson(notHitKeys));
            args[0] = notHitKeys;
        } else {
            log.debug("未命中任何{}缓存: {}", cacheName, JsonUtils.toJson(keys));
        }

        Map<Object, Object> mapValues = (Map<Object, Object>) joinPoint.proceed(args);
        if (MapUtils.isEmpty(mapValues)) {
            return returnValues;
        }

        returnValues.putAll(mapValues);

        cache.putAll(mapValues);

        return returnValues;
    }

    public static void clearAll() {
        cacheMap.clear();
    }

    public static void clearCache(String cacheName) {
        cacheMap.remove(cacheName);
    }

}