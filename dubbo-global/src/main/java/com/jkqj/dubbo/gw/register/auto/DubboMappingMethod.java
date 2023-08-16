package com.jkqj.dubbo.gw.register.auto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DubboMappingMethod {
    private String url;
    private List<String> methods;
    private boolean login;
    private List<String> passHeaders;
    private String traceHeader = "trace-id";
    private String description;
    private List<String> roles;

    private ProxyPass proxyPass = new ProxyPass();

    private String timeout;

    @Setter
    @Getter
    public static class ProxyPass {
        private String dubboInterface;
        private String dubboMethod;
        private String parameterClassName;
        private String version;
    }
}
