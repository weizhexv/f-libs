package com.jkqj.mybatis.extension.sharding.annotations;


import com.jkqj.mybatis.extension.sharding.policy.NoneShardingTablePolicy;
import com.jkqj.mybatis.extension.sharding.policy.ShardingTablePolicy;

import java.lang.annotation.*;

/**
 * 指明分表参数，在某些特殊的场景中，Mapper的方法没有指定entity参数，此时需要一个参数来指定分表键，用TableShardingKey来声明该参数。
 *
 * 要使用该注解必须同时在Mapper上声明TableSharding注解
 *
 * 分表时会首先使用TableShardingKey 再使用 TableSharding
 *
 * @author rolandhe
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableShardingKey {
    /**
     * 指定分表策略，缺省是无效的分表策略类，会使用Mapper上声明的TableSharding.shardingPolicyClass
     *
     * @return
     */
    Class<? extends ShardingTablePolicy> shardingPolicyClass() default NoneShardingTablePolicy.class;
}
