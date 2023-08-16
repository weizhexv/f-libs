package com.jkqj.dubbo.gw.register.auto;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.StringWriter;
import java.util.List;

@Slf4j
public class NacosAutoRegister implements AutoRegister {
    public static final String SUFFIX_SERVER_CONFIG = "_gw_config.yaml";
    public static String DUBBO_APP_INDEX = "DUBBO_GATEWAY_TRIGGER.TEXT";
    public static int DUBBO_INDEX_REFRESH_MAX = 3;

    @Value("${dubbo.application.name}")
    private String appName;

    @Value("${dubbo.provider.group}")
    private String providerGroup;


    @Value("${nacos.config.group}")
    private String nacosGroup;

    @NacosInjected
    private ConfigService configService;


    @Override
    public void register(List<DubboMappingMethod> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        checkApplicationIndex();
        String dataId = getDataId();
        String content = buildYamlContent(list);
        String newMd5 = MD5Utils.md5Hex(content, "UTF-8");
        try {
            String readContent = configService.getConfig(dataId, nacosGroup, 5000L);
            if (StringUtils.isNotBlank(readContent)) {
                String oldMd5 = MD5Utils.md5Hex(readContent, "UTF-8");
                if (newMd5.equals(oldMd5)) {
                    return;
                }
            }
            boolean published = configService.publishConfig(getDataId(), nacosGroup, content, ConfigType.YAML.getType());
            if (!published) {
                throw new RuntimeException("auto push dubbo gw error");
            }
            refreshIndex();
        } catch (NacosException e) {
            log.error("auto push dubbo gw error.", e);
            throw new RuntimeException(e);
        }
    }

    private String getDataId() {
        return String.format("%s%s", appName, SUFFIX_SERVER_CONFIG);
    }

    private String buildYamlContent(List<DubboMappingMethod> list) {
        StringWriter stringWriter = new StringWriter();
        stringWriter.write("rules:\n");
        list.forEach(dubboMappingMethod -> outputMethod(dubboMappingMethod, stringWriter));


        return StringUtils.stripEnd(stringWriter.toString(), "\n");
    }

    private void checkApplicationIndex() {
        String readContent = null;
        try {
            readContent = configService.getConfig(DUBBO_APP_INDEX, nacosGroup, 5000L);
        } catch (NacosException e) {
            log.error("index config is not exist", e);
            throw new RuntimeException(e);
        }
        if (StringUtils.isBlank(readContent)) {
            log.error("index config is not exist");
            throw new RuntimeException("index config is not exist");
        }
    }

    private void refreshIndex() {
        for (int i = 0; i < DUBBO_INDEX_REFRESH_MAX; i++) {
            try {
                String newContent = System.currentTimeMillis() + "\n";
//                String oldMd5 = MD5Utils.md5Hex(readContent, "UTF-8");

//                boolean published = configService.publishConfigCas(DUBBO_APP_INDEX, nacosGroup, newContent, oldMd5);
                boolean published = configService.publishConfig(DUBBO_APP_INDEX, nacosGroup, newContent);
                if (published) {
                    return;
                }
                log.warn("refresh index error,{}", i);

            } catch (NacosException e) {
                log.warn("refresh index error,{}", i, e);
            }
            if (i < DUBBO_INDEX_REFRESH_MAX - 1) {
                safeSleep(500L);
            }
        }

        log.error("refresh index error,{}", DUBBO_INDEX_REFRESH_MAX);
        throw new RuntimeException("refresh index error");
    }

    private void safeSleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            // ignore
        }
    }


    private void outputMethod(DubboMappingMethod method, StringWriter writer) {
        String margin = "  ";

        int deep = 1;
        writer.write(StringUtils.repeat(margin, deep));
        writer.write("- path: " + method.getUrl() + "\n");

        deep++;
        writer.write(StringUtils.repeat(margin, deep));
        writer.write("methods:\n");
        deep++;
        for (String m : method.getMethods()) {
            writer.write(StringUtils.repeat(margin, deep));
            writer.write("- " + m + "\n");
        }
        deep--;

        writer.write(StringUtils.repeat(margin, deep));
        writer.write("description: " + method.getDescription() + "\n");

        writer.write(StringUtils.repeat(margin, deep));
        writer.write("login: " + method.isLogin() + "\n");

        writer.write(StringUtils.repeat(margin, deep));
        writer.write("roles:\n");
        deep++;
        for (String r : method.getRoles()) {
            writer.write(StringUtils.repeat(margin, deep));
            writer.write("- " + r + "\n");
        }
        deep--;

        writer.write(StringUtils.repeat(margin, deep));
        writer.write("passHeaders:\n");
        deep++;
        for (String h : method.getPassHeaders()) {
            writer.write(StringUtils.repeat(margin, deep));
            writer.write("- " + h + "\n");
        }
        deep--;

        writer.write(StringUtils.repeat(margin, deep));
        writer.write("traceHeader: " + method.getTraceHeader() + "\n");

        writer.write(StringUtils.repeat(margin, deep));
        writer.write("proxyPass: " + buildProxyPass(method.getProxyPass()) + "\n");

        if (StringUtils.isNotEmpty(method.getTimeout())) {
            writer.write(StringUtils.repeat(margin, deep));
            writer.write("timeout: " + method.getTimeout() + "\n");
        }
    }

    private String buildProxyPass(DubboMappingMethod.ProxyPass proxyPass) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("dubbo://")
                .append(providerGroup)
                .append(":")
                .append(proxyPass.getDubboInterface())
                .append(":")
                .append(proxyPass.getDubboMethod())
                .append(":")
                .append("(")
                .append(proxyPass.getParameterClassName())
                .append(")")
                .append(":")
                .append(StringUtils.isBlank(proxyPass.getVersion()) ? "1.0.0" : proxyPass.getVersion());

        return stringBuilder.toString();
    }
}
