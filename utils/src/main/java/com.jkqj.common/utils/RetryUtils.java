package com.jkqj.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 重试工具类
 *
 * @author cb
 * @date 2021-12-08
 */
@Slf4j
public final class RetryUtils {

    public interface Executor<T> {
        T execute() throws Throwable;
    }

    /**
     * 重试执行
     *
     * @param retryCount  失败重试的次数 如果=0，失败不重试
     * @param interval
     * @param timeUnit
     * @param throwIfFail
     * @param function
     * @throws Exception
     */
    public static <T> T retry(int retryCount, long interval, TimeUnit timeUnit, boolean throwIfFail,
                              Executor<T> function) throws Throwable {
        if (function == null) {
            return null;
        }

        for (int i = 0; i <= retryCount; i++) {
            try {
                return function.execute();
            } catch (Exception e) {
                if (i == retryCount) {
                    if (throwIfFail) {
                        throw e;
                    } else {
                        log.error(e.getMessage(), e);
                        break;
                    }
                } else {
                    if (timeUnit != null && interval > 0L) {
                        try {
                            timeUnit.sleep(interval);
                        } catch (InterruptedException e1) {
                            log.error(e1.getMessage(), e1);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 有间隔的重试
     *
     * @param retryCount 失败重试的次数 如果=0，失败不重试
     * @param interval
     * @param timeUnit
     * @param function
     * @throws Exception
     */
    public static <T> T retry(int retryCount, long interval, TimeUnit timeUnit, Executor<T> function) throws Throwable {
        return retry(retryCount, interval, timeUnit, false, function);
    }

    /**
     * 不间隔重试
     *
     * @param retryCount 失败重试的次数 如果=0，失败不重试
     * @param function
     * @throws Exception
     */
    public static <T> T retry(int retryCount, boolean throwIfFail, Executor<T> function) throws Throwable {
        return retry(retryCount, -1, null, throwIfFail, function);
    }
}