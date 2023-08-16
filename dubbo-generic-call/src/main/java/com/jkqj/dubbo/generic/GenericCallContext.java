package com.jkqj.dubbo.generic;

/**
 * 一次泛化调用的上下文信息
 *
 * @author rolandhe
 *
 */
public class GenericCallContext {
    private String group;
    private String interfaceName;
    private String version;
    private String method;
    private int retries;
    private String[] parameterClassNames;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String[] getParameterClassNames() {
        return parameterClassNames;
    }

    public void setParameterClassNames(String[] parameterClassNames) {
        this.parameterClassNames = parameterClassNames;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }
}
