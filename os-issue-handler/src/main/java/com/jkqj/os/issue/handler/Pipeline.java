package com.jkqj.os.issue.handler;

/**
 * 数据增量处理管道
 *
 * @author liuyang
 */
public interface Pipeline {

   boolean execute(String topic, String tableName, Long idValue);

}
