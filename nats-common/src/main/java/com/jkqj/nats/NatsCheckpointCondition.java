package com.jkqj.nats;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author xuweizhe@reta-inc.com
 * @date 2022/3/21
 * @description
 */
public class NatsCheckpointCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String enable = context.getEnvironment().getProperty("mq.nats.checkpoint.enable");
        return BooleanUtils.toBoolean(enable);
    }
}
