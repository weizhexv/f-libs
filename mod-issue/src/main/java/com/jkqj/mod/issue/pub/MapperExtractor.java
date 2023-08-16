package com.jkqj.mod.issue.pub;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
class MapperExtractor {
    private static final Map<String, Context> cache = new ConcurrentHashMap<>();

    private MapperExtractor() {
    }


    public static Configuration extract(Object mapper) {
        Object h =  Proxy.getInvocationHandler(mapper);
        try {
            SqlSession sqlSession = (SqlSession) FieldUtils.readField(h, "sqlSession", true);
            return  sqlSession.getConfiguration();
        } catch (IllegalAccessException e) {
            log.info("get sqlSession error",e);
            throw new RuntimeException(e);
        }
    }

    public static Context getCachedContext(Object target) {
        String name = target.getClass().getName();
        Context context = cache.get(name);
        if (context != null) {
            return context;
        }
        context = getContext(target);
        Context old = cache.putIfAbsent(name, context);
        return old != null ? old : context;
    }

    private static Context getContext(Object target) {
        Context context = new Context();
        Class<?>[] interfaces = target.getClass().getInterfaces();

        if (walk(interfaces, context)) {
            context.configuration = MapperExtractor.extract(target);
            return context;
        }

        return Context.NONE;
    }

    private static boolean walk(Class<?>[] interfaces, Context context) {
        if (interfaces == null) {
            return context.isComplete();
        }
        for (Class<?> ci : interfaces) {
            Pub pub = ci.getAnnotation(Pub.class);
            if (pub != null) {
                context.pub = pub;
            }
            Mapper mapper = ci.getAnnotation(Mapper.class);
            if (mapper != null) {
                context.mapper = mapper;
                context.mapperClassName = ci.getName();
            }
            if (context.isComplete()) {
                return true;
            }
            if (walk(ci.getInterfaces(), context)) {
                return true;
            }
        }
        return false;
    }
}
