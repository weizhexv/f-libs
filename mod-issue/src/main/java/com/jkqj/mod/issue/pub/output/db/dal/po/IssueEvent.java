package com.jkqj.mod.issue.pub.output.db.dal.po;

import lombok.Setter;
import lombok.Getter;
import java.time.LocalDateTime;


@Setter
@Getter
public class IssueEvent {
    private Long id;
    private String body;
    private LocalDateTime createdAt;
    private Integer status;
    private LocalDateTime reSendAt;

}