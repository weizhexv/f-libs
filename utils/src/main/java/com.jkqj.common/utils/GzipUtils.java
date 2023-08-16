package com.jkqj.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * gzip工具类
 *
 * @author cb
 * @date 2022-01-18
 */
public final class GzipUtils {

    public static byte[] compress(String str) {
        if (StringUtils.isBlank(str)) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return out.toByteArray();
    }

    public static String uncompress(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
                int b;
                while ((b = gis.read()) != -1) {
                    baos.write((byte) b);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

}