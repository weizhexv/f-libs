package com.jkqj.mod.issue.sub.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息处理目标对象
 *
 * @author liuyang
 */
@Getter
@Setter
public class TargetBody {
    /**
     * issueSubId
     */
    public Long id;
    /**
     * 目标handle名称
     */
    public String topic;
    /**
     * 目标handle名称
     */
    public String target;
    /**
     * 消息内容
     */
    public String body;

    public TargetBody(){

    }

    public TargetBody(Long id, String topic, String target, String body){
        this.id = id;
        this.body = body;
        this.topic = topic;
        this.target = target;
    }
}