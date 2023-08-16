package com.jkqj.wx.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.jkqj.wx.api.WxErrCode;
import lombok.Getter;

@Getter
public class WxBaseResult implements WxErrCode {
    @JsonProperty("errcode")
    private long errCode;

    @JsonProperty("errmsg")
    private String errMsg;

    private Integer code;

    private boolean success;

    /**
     * 设置错误码
     */
    @JsonProperty("errcode")
    public void setErrCode(long errCode) {
        this.errCode = errCode;
        this.code = errCode == 0L ? 0 : 5001;
        this.success = errCode == 0L;
    }

    /**
     * 设置错误信息
     */
    @JsonProperty("errmsg")
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

}
