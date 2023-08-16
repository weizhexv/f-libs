package com.jkqj.dubbo.common.timeout;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DubboMethodConf {
    Integer timeout;
    Integer retries;
}
