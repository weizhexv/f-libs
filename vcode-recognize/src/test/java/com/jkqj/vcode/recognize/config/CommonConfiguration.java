package com.jkqj.vcode.recognize.config;

import com.jkqj.common.http.client.MyHttpConfiguration;
import com.jkqj.dtrace.cglib.CglibMonitorSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 公共配置
 *
 * @author cb
 * @date 2022-03-01
 */
@Configuration
@Import(MyHttpConfiguration.class)
public class CommonConfiguration {

    @Bean
    public CglibMonitorSupport cglibMonitorSupport() {
        return new CglibMonitorSupport();
    }

}
