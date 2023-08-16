package com.jkqj.common.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.Resource;

import java.io.InputStream;

/**
 * 自定义文件工具类
 *
 * @author cb
 * @date 2021-07-15
 */
public final class MyFileUtils {

    /**
     * 获取资源文件输入流
     *
     * @param resourceFile
     * @return
     */
    public static InputStream getResourceFileInputStream(String resourceFile) {
        Resource resource = new ClassPathResource(resourceFile);

        return resource.getStream();
    }

    /**
     * 获取资源文件内容
     *
     * @param resourceFile
     * @return
     */
    public static String getResourceFileContent(String resourceFile) {
        return IoUtil.readUtf8(getResourceFileInputStream(resourceFile));
    }

}