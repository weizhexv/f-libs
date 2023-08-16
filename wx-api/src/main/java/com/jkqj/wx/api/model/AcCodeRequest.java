package com.jkqj.wx.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AcCodeRequest {
    /**
     * 必须, query 参数放在这儿
     */
    private String scene;
    /**
     * 必须, 不能有query string
     */
    private String page;
    @JsonProperty("check_path")
    private boolean checkPath = false;

    @JsonProperty("env_version")
    private String envVersion = "develop";

    private int width = 430;
}
