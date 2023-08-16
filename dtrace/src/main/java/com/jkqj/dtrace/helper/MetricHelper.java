package com.jkqj.dtrace.helper;

import com.jkqj.dtrace.feign.FeignMonitorSupport;
import com.jkqj.dtrace.output.MetricStdTags;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.dubbo.common.constants.CommonConstants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MetricHelper {
    private static final String SUCCESS = "success";
    private static final String CODE = "code";

    private static final Map<Class, Holder> cache = new ConcurrentHashMap<>();

    private static class Holder {
        boolean noSuccess;
        boolean noCode;
    }

    private MetricHelper() {
    }

    public static boolean hasHost(MetricStdTags stdTags) {
        if (stdTags.getSide().equals(CommonConstants.CONSUMER) || stdTags.getSide().equals(FeignMonitorSupport.SIDE)) {
            return true;
        }
        return false;
    }

    public static MetricStdTags buildStdTags(String fullName, Object value, String side) {
        MetricStdTags stdTags = new MetricStdTags();

        int pos = fullName.lastIndexOf("#");

        if ((side.equals(CommonConstants.CONSUMER) || side.equals(FeignMonitorSupport.SIDE))
                && pos > 0) {
            stdTags.setHost(fullName.substring(pos + 1));
            fullName = fullName.substring(0, pos);
        }

        stdTags.setUrl(fullName);
        stdTags.setSide(side);

        if (value == null) {
            return stdTags;
        }

        Holder holder = cache.get(value.getClass());
        boolean unknown = holder == null;

        Holder newHolder = null;

        if (unknown || !holder.noSuccess) {
            try {
                Object v = FieldUtils.readField(value, SUCCESS, true);
                if (v != null && v instanceof Boolean) {
                    stdTags.setSuccess(v.toString());
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                log.info("读取success识别，不用担心，不影响业务流程,methodName:{}",fullName, e);
                if (unknown) {
                    newHolder = new Holder();
                    newHolder.noSuccess = true;
                }
            }
        }
        if (unknown || !holder.noCode) {
            try {
                Object v = FieldUtils.readField(value, CODE, true);
                if (v != null && v instanceof Boolean) {
                    stdTags.setCode(v.toString());
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                log.info("读取code识别，不用担心，不影响业务流程,methodName:{}", fullName, e);
                if (unknown) {
                    if (newHolder == null) {
                        newHolder = new Holder();
                    }
                    newHolder.noCode = true;
                }
            }
        }
        if (unknown && newHolder != null) {
            cache.putIfAbsent(value.getClass(), newHolder);
        }
        return stdTags;
    }
}
