package com.jkqj.common.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * URL工具类
 *
 * @author cb
 * @date 2022/5/25
 */
public final class UrlUtils {

    private final static Set<String> PUBLIC_SUFFIX_SET = new HashSet<>(
            Arrays.asList("com|org|net|gov|edu|co|tv|mobi|info|asia|xxx|onion|cn|com.cn|edu.cn|gov.cn|net.cn|org.cn|jp|kr|tw|com.hk|hk|com.hk|org.hk|se|com.se|org.se"
                    .split("\\|")));

    private static Pattern IP_PATTERN = Pattern.compile("(\\d{1,3}\\.){3}(\\d{1,3})");

    /**
     * 获取url的顶级域名
     *
     * @param url
     * @return
     */
    public static String getDomainName(URL url) {
        String host = url.getHost();
        if (host.endsWith(".")) {
            host = host.substring(0, host.length() - 1);
        }
        if (IP_PATTERN.matcher(host).matches()) {
            return host;
        }

        int index = 0;
        String candidate = host;
        for (; index >= 0; ) {
            index = candidate.indexOf('.');
            String subCandidate = candidate.substring(index + 1);
            if (PUBLIC_SUFFIX_SET.contains(subCandidate)) {
                return candidate;
            }
            candidate = subCandidate;
        }
        return candidate;
    }

    /**
     * 获取url的顶级域名
     *
     * @param url
     * @return
     * @throws MalformedURLException
     */
    public static String getDomainName(String url) throws MalformedURLException {
        return getDomainName(new URL(url));
    }

    /**
     * 判断两个url顶级域名是否相等
     *
     * @param url1
     * @param url2
     * @return
     */
    public static boolean isSameDomainName(URL url1, URL url2) {
        return getDomainName(url1).equalsIgnoreCase(getDomainName(url2));
    }

    /**
     * 判断两个url顶级域名是否相等
     *
     * @param url1
     * @param url2
     * @return
     * @throws MalformedURLException
     */
    public static boolean isSameDomainName(String url1, String url2) throws MalformedURLException {
        return isSameDomainName(new URL(url1), new URL(url2));
    }

    /**
     * urlEncode 对中文和空格做处理
     *
     * @param url 源url
     * @return encode后的url
     */
    public static String urlEncode(String url) {
        StringBuffer encodeUrl = new StringBuffer();
        for (int i = 0; i < url.length(); i++) {
            char charAt = url.charAt(i);
            // 对汉字和空格处理
            if (isChinese(charAt) || isNbsp(charAt)) {
                String encode = URLEncoder.encode(String.valueOf(charAt), StandardCharsets.UTF_8);
                encodeUrl.append(encode);
            } else {
                encodeUrl.append(charAt);
            }
        }
        return encodeUrl.toString();
    }

    // 判断汉字的方法,只要编码在\u4e00到\u9fa5之间的都是汉字
    public static boolean isChinese(char c) {
        return String.valueOf(c).matches("[\u4e00-\u9fa5]");
    }

    // 判断空格 不间断空格|半角空格|全角空格
    public static boolean isNbsp(char c) {
        return String.valueOf(c).matches("\u00A0|\u0020|\u3000");
    }

}
