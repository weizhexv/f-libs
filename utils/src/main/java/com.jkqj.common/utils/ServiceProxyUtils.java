package com.jkqj.common.utils;

import com.jkqj.common.enums.CommonErrorEnum;
import com.jkqj.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.jkqj.common.utils.RetryUtils.retry;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * 服务代理工具类
 *
 * @author cb
 * @date 2021-12-08
 */
@Slf4j
public final class ServiceProxyUtils {

    @FunctionalInterface
    public interface Executor<T> {
        Result<T> execute() throws Throwable;
    }

    public static <T> Optional<T> proxyWithRetry(String operation, int retryCount, long intervalMilli,
                                                 Executor<T> executor, Object... params) {
        Result<T> result;
        try {
            result = retry(retryCount, intervalMilli, MILLISECONDS, true, () -> executor.execute());
        } catch (Throwable t) {
            if (ArrayUtils.isNotEmpty(params)) {
                log.error("{}异常，params: {}", operation, JsonUtils.toJson(params), t);
            } else {
                log.error("{}异常", operation, t);
            }

            return Optional.empty();
        }

        if (result == null) {
            if (ArrayUtils.isNotEmpty(params)) {
                log.error("{}异常，params: {}", operation, JsonUtils.toJson(params));
            } else {
                log.error("{}异常", operation);
            }

            return Optional.empty();
        }

        if (!result.isSuccess()) {
            if (ArrayUtils.isNotEmpty(params)) {
                log.debug("{}失败，params: {}，result: {}", operation, JsonUtils.toJson(params), JsonUtils.toJson(result));
            } else {
                log.debug("{}失败，result: {}", operation, JsonUtils.toJson(result));
            }

            return Optional.empty();
        }

        return Optional.ofNullable(result.getData());
    }

    public static <T> Optional<T> proxy(String operation, Executor<T> executor, Object... params) {
        Result<T> result;
        try {
            result = executor.execute();
        } catch (Throwable t) {
            if (ArrayUtils.isNotEmpty(params)) {
                log.error("{}异常，params: {}", operation, JsonUtils.toJson(params), t);
            } else {
                log.error("{}异常", operation, t);
            }

            return Optional.empty();
        }

        if (result == null) {
            if (ArrayUtils.isNotEmpty(params)) {
                log.error("{}异常，params: {}", operation, JsonUtils.toJson(params));
            } else {
                log.error("{}异常", operation);
            }

            return Optional.empty();
        }

        if (!result.isSuccess()) {
            if (ArrayUtils.isNotEmpty(params)) {
                log.debug("{}失败，params: {}，result: {}", operation, JsonUtils.toJson(params), JsonUtils.toJson(result));
            } else {
                log.debug("{}失败，result: {}", operation, JsonUtils.toJson(result));
            }

            return Optional.empty();
        }

        return Optional.ofNullable(result.getData());
    }

    public static <P, T> Optional<T> proxyWithRetry(String operation, int retryCount, long intervalMilli,
                                                    Function<P, Result<T>> handler, P param) {
        Result<T> result;
        try {
            result = retry(retryCount, intervalMilli, MILLISECONDS, true, () -> handler.apply(param));
        } catch (Throwable t) {
            log.error("{}异常，param: {}", operation, JsonUtils.toJson(param), t);

            return Optional.empty();
        }

        if (result == null) {
            log.error("{}异常，param: {}", operation, JsonUtils.toJson(param));

            return Optional.empty();
        }

        if (!result.isSuccess()) {
            log.debug("{}失败，param: {}，result: {}", operation, JsonUtils.toJson(param), JsonUtils.toJson(result));

            return Optional.empty();
        }

        return Optional.ofNullable(result.getData());
    }

    public static <P, T> Optional<T> proxy(String operation, Function<P, Result<T>> handler, P param) {
        Result<T> result;
        try {
            result = handler.apply(param);
        } catch (Throwable t) {
            log.error("{}异常，param: {}", operation, JsonUtils.toJson(param), t);

            return Optional.empty();
        }

        if (result == null) {
            log.error("{}异常，param: {}", operation, JsonUtils.toJson(param));

            return Optional.empty();
        }

        if (!result.isSuccess()) {
            log.debug("{}失败，param: {}，result: {}", operation, JsonUtils.toJson(param), JsonUtils.toJson(result));

            return Optional.empty();
        }

        return Optional.ofNullable(result.getData());
    }

    public static <P, T> Result<T> protoProxy(String operation, Function<P, Result<T>> handler, P param) {
        Result<T> result;
        try {
            result = handler.apply(param);
        } catch (Throwable t) {
            log.error("{}异常，param: {}", operation, JsonUtils.toJson(param), t);

            return Result.fail(CommonErrorEnum.SYSTEM_ERROR);
        }

        if (result == null) {
            log.error("{}异常，param: {}", operation, JsonUtils.toJson(param));

            return Result.fail(CommonErrorEnum.SYSTEM_ERROR);
        }

        return result;
    }

    public static <P1, P2, T> Optional<T> proxyWithRetry(String operation, int retryCount, long intervalMilli,
                                                         BiFunction<P1, P2, Result<T>> handler, P1 param1, P2 param2) {
        Result<T> result;
        try {
            result = retry(retryCount, intervalMilli, MILLISECONDS, true, () -> handler.apply(param1, param2));
        } catch (Throwable t) {
            log.error("{}异常，param1: {}, param2: {}", operation, JsonUtils.toJson(param1), JsonUtils.toJson(param2), t);

            return Optional.empty();
        }

        if (result == null) {
            log.error("{}异常，param1: {}, param2: {}", operation, JsonUtils.toJson(param1), JsonUtils.toJson(param2));

            return Optional.empty();
        }

        if (!result.isSuccess()) {
            log.debug("{}失败，param1: {}, param2: {}，result: {}",
                    operation, JsonUtils.toJson(param1), JsonUtils.toJson(param2), JsonUtils.toJson(result));

            return Optional.empty();
        }

        return Optional.ofNullable(result.getData());
    }

    public static <P1, P2, T> Optional<T> proxy(String operation, BiFunction<P1, P2, Result<T>> handler, P1 param1, P2 param2) {
        Result<T> result;
        try {
            result = handler.apply(param1, param2);
        } catch (Throwable t) {
            log.error("{}异常，param1: {}, param2: {}", operation, JsonUtils.toJson(param1), JsonUtils.toJson(param2), t);

            return Optional.empty();
        }

        if (result == null) {
            log.error("{}异常，param1: {}, param2: {}", operation, JsonUtils.toJson(param1), JsonUtils.toJson(param2));

            return Optional.empty();
        }

        if (!result.isSuccess()) {
            log.debug("{}失败，param1: {}, param2: {}，result: {}",
                    operation, JsonUtils.toJson(param1), JsonUtils.toJson(param2), JsonUtils.toJson(result));

            return Optional.empty();
        }

        return Optional.ofNullable(result.getData());
    }

}