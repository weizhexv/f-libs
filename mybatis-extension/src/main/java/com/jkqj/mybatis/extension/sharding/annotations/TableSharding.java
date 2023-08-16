package com.jkqj.mybatis.extension.sharding.annotations;


import com.jkqj.mybatis.extension.sharding.policy.ShardingTablePolicy;

import java.lang.annotation.*;

/**
 * 分表规则配置，需要和 mybatis Mapper 配对使用，放置在xxxMapper接口上<br>
 * <p>
 * 需要指定该Mapper操作的实体类、分表规则、分表的总数及按照实体类中的哪个字段进行分表.
 * 类似于 insert(entityClass)或者insert(entityClass, ...其他参数)的方法，会自动匹配TableSharding规则进行分表。
 * </p>
 *
 * @author rolandhe
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableSharding {
    /**
     * 当前Mapper所能操作的实体类
     *
     * @return
     */
    Class entityClass();

    /**
     * 分表策略类
     *
     * @return
     */
    Class<? extends ShardingTablePolicy> shardingPolicyClass();

    /**
     * 分表的总数
     *
     * @return
     */
    int sliceCount();

    /**
     * 分表时需要从entityClass() 读取哪个属性
     *
     * @return
     */
    String shardingKeyNameOfEntity() default "";
}
