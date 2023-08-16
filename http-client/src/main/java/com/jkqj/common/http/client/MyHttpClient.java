package com.jkqj.common.http.client;

import com.jkqj.common.utils.JsonUtils;
import com.jkqj.common.utils.MyMapUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;

/**
 * 自定义http客户端
 *
 * @author cb
 */
@Slf4j
public class MyHttpClient {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType XML = MediaType.parse("text/xml; charset=utf-8");

    @Resource
    private OkHttpClient okHttpClient;

    /**
     * get 请求
     *
     * @param url 请求url地址
     * @return string
     */
    public String doGet(String url) {
        return doGet(url, Collections.emptyMap(), new String[0]);
    }

    /**
     * get 请求
     *
     * @param url    请求url地址
     * @param params 请求参数 map
     * @return string
     */
    public String doGet(String url, Map<String, String> params) {
        return doGet(url, params, new String[0]);
    }

    /**
     * get 请求
     *
     * @param url     请求url地址
     * @param headers 请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     */
    public String doGet(String url, String[] headers) {
        return doGet(url, Collections.emptyMap(), headers);
    }

    /**
     * get 请求
     *
     * @param url     请求url地址
     * @param params  请求参数 map
     * @param headers 请求头字段
     * @return string
     */
    public String doGet(String url, Map<String, String> params, Map<String, String> headers) {
        return doGet(url, params, MyMapUtils.toArray(headers));
    }

    /**
     * get 请求
     *
     * @param url     请求url地址
     * @param params  请求参数 map
     * @param headers 请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     */
    public String doGet(String url, Map<String, String> params, String[] headers) {
        StringBuilder sb = new StringBuilder(url);
        if (params != null && params.keySet().size() > 0) {
            boolean firstFlag = true;
            for (String key : params.keySet()) {
                if (firstFlag) {
                    sb.append("?").append(key).append("=").append(params.get(key));
                    firstFlag = false;
                } else {
                    sb.append("&").append(key).append("=").append(params.get(key));
                }
            }
        }

        Request.Builder builder = new Request.Builder();
        if (headers != null && headers.length > 0) {
            if (headers.length % 2 == 0) {
                for (int i = 0; i < headers.length; i = i + 2) {
                    builder.addHeader(headers[i], headers[i + 1]);
                }
            } else {
                log.warn("headers's length[{}] is error.", headers.length);
            }

        }

        Request request = builder.url(sb.toString()).build();
        log.info("do get request and url[{}]", sb);
        return execute(request);
    }

    /**
     * post 请求
     *
     * @param url    请求url地址
     * @param params 请求参数 map
     * @return string
     */
    public String doPost(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();

        if (params != null && params.keySet().size() > 0) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        log.info("do post request and url[{}]", url);

        return execute(request);
    }

    /**
     * post 请求
     *
     * @param url     请求url地址
     * @param params  请求参数
     * @param headers 请求头
     * @return string
     */
    public String doPost(String url, Map<String, String> params, Map<String, String> headers) {
        return doPostJson(url, JsonUtils.toJson(params), MyMapUtils.toArray(headers));
    }

    /**
     * get 请求
     *
     * @param url     请求url地址
     * @param json    请求json数据
     * @param headers 请求头字段 {k1, v1 k2, v2, ...}
     * @return string
     */
    public String doPostJson(String url, String json, String[] headers) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request.Builder builder = new Request.Builder().url(url).post(requestBody);

        if (headers != null && headers.length > 0) {
            if (headers.length % 2 == 0) {
                for (int i = 0; i < headers.length; i = i + 2) {
                    builder.addHeader(headers[i], headers[i + 1]);
                }
            } else {
                log.warn("headers's length[{}] is error.", headers.length);
            }
        }

        Request request = builder.build();
        log.info("do post json, url: {}, json: {}, headers: {}", url, json, headers);

        return execute(request);
    }

    /**
     * post 请求, 请求数据为 json 的字符串
     *
     * @param url  请求url地址
     * @param json 请求数据, json 字符串
     * @return string
     */
    public String doPostJson(String url, String json) {
        log.info("do post request and url[{}]", url);
        return executePost(url, json, JSON);
    }

    /**
     * post请求 请求内容是xml的字符串
     *
     * @param url     url地址
     * @param data    xml字符串
     * @param headers 请求头
     * @return 结果
     */
    private String doPostXml(String url, String data, Map<String, String> headers) {
        RequestBody requestBody = RequestBody.create(XML, data);
        Request.Builder builder = new Request.Builder().url(url).post(requestBody);
        String[] hs = MyMapUtils.toArray(headers);
        if (hs.length > 0) {
            if (hs.length % 2 == 0) {
                for (int i = 0; i < hs.length; i = i + 2) {
                    builder.addHeader(hs[i], hs[i + 1]);
                }
            } else {
                log.warn("headers's length[{}] is error.", hs.length);
            }
        }
        Request build = builder.build();
        return execute(build);
    }

    private String executePost(String url, String data, MediaType contentType) {
        RequestBody requestBody = RequestBody.create(contentType, data);
        Request request = new Request.Builder().url(url).post(requestBody).build();

        return execute(request);
    }

    private String execute(Request request) {
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return "";
    }

}