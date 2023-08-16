package com.jkqj.dubbo.generic;

import com.jkqj.dubbo.generic.reference.DubboGenericFactory;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于执行泛化调用的门面封装, 封装重试、获取泛化对象及执行
 *
 * @author rolandhe
 */
public class GenericCallExecutor {
    private static final Logger log = LoggerFactory.getLogger(GenericCallExecutor.class);

    private GenericCallExecutor() {
    }


    /**
     * 执行泛化调用，可能会抛出异常，需要调用方自行捕获处理
     *
     * @param dubboGenericFactory
     * @param ctx
     * @return
     * @throws RuntimeException
     */
    public static Object execute(DubboGenericFactory dubboGenericFactory, GenericCallContext ctx, Object[] parameters) throws RuntimeException {
        ReferenceConfig<GenericService> referenceConfig = dubboGenericFactory.factory(ctx.getGroup(), ctx.getInterfaceName(), ctx.getVersion());
        Object value = null;
        GenericService genericService = null;

        try {
            genericService = referenceConfig.get();
        } catch (RuntimeException e) {
            log.warn("got bad referenceConfig, get new referenceConfig.", e);
            referenceConfig = dubboGenericFactory.factory(ctx.getGroup(), ctx.getInterfaceName(), ctx.getVersion(), referenceConfig);
            genericService = referenceConfig.get();
        }

        int retries = ctx.getRetries() - 1;
        int callCount = 0;
        while (true) {
            try {
                value = genericService.$invoke(ctx.getMethod(), ctx.getParameterClassNames(), parameters);
                ResultPure.pureResult(value);
                log.info("调用rpc 成功,调用次数:{},，{}.{}.", callCount, ctx.getGroup(), ctx.getInterfaceName());
                return value;
            } catch (RuntimeException e) {
                if (callCount < retries) {
                    log.info("调用rpc 错误,重试{}，{}.{}.", callCount, ctx.getGroup(), ctx.getInterfaceName(), e);
                    callCount++;
                    continue;
                }
                log.warn("调用rpc 错误,超过重试次数{},，{}.{}.", callCount, ctx.getGroup(), ctx.getInterfaceName(), e);
                throw e;
            }
        }


    }

}
