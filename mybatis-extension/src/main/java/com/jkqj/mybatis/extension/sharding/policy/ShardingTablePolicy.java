package com.jkqj.mybatis.extension.sharding.policy;

/**
 * 分表策略接口
 *
 * @author rolandhe
 *
 */
public interface ShardingTablePolicy {
    /**
     * 生成分区表名字
     *
     * @param key 分表键值
     * @param originalTableName 原来的表名
     * @param sliceCount  分表的总数
     * @return 新的分表名称
     */
    String shardTableName(Object key, String originalTableName, int sliceCount);
}
