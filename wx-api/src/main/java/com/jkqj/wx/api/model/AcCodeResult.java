package com.jkqj.wx.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AcCodeResult extends WxBaseResult{
    private String contentType;
    @JsonIgnore
    private byte[] buffer;
}
