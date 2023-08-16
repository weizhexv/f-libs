package com.jkqj.mod.issue.sub.dal.po;

import lombok.Setter;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 接受消息
 *
 * @author liuyang
 */
@Setter
@Getter
public class IssueSub {
    private Long id;
    private String topic;
    private String target;
    private String body;
    private Integer status;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}