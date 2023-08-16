package com.jkqj.wx.token.dal.po;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class WxToken {
    private String appId;
    private String token;
    private long expired;
    private LocalDateTime createdAt;
}
