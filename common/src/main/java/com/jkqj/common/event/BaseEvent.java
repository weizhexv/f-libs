package com.jkqj.common.event;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 事件基类
 *
 * @author cb
 * @date 2022-01-13
 */
@Data
public class BaseEvent {

    private String event;

    private LocalDateTime occurredAt;

}
