package com.jkqj.mybatis.extension.sharding.policy;

/**
 * 缺省且无效的分表名称策略
 * @author rolandhe
 */
public class NoneShardingTablePolicy implements ShardingTablePolicy {

    @Override
    public String shardTableName(Object key, String originalTableName, int sliceCount) {
        throw new RuntimeException("NoneShardingTableNamePolicy");
    }
}
