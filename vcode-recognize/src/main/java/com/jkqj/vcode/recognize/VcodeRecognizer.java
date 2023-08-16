package com.jkqj.vcode.recognize;

import com.google.common.collect.Maps;
import com.jkqj.common.constants.Symbols;
import com.jkqj.common.http.client.MyHttpClient;
import com.jkqj.common.utils.JsonUtils;
import com.jkqj.common.utils.Lambdas;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 验证码识别器
 *
 * @author cb
 * @date 2022-09-21
 */
@Slf4j
@Component
public class VcodeRecognizer {

    @Resource
    private MyHttpClient myHttpClient;

    @Value("${vcode.recognizer.token:SXXL99nAoCUOVJcrfrX4Qg}")
    private String token;

    private static final String customerApi = "https://www.jfbym.com/api/YmServer/customApi";
    private static final Map<String, String> headers = Map.of("Content-Type", "application/json");

    public Optional<Double> slideVerifyImagePosition(String slideImageBase64, String backgroundImageBase64) {
        Map<String, String> params = Maps.newHashMap();
        params.put("type", "20111");
        params.put("token", token);
        params.put("slide_image", slideImageBase64);
        params.put("background_image", backgroundImageBase64);

        String response = myHttpClient.doPost(customerApi, params, headers);
        log.info("滑块图片响应: {}", response);

        if (StringUtils.isBlank(response)) {
            log.error("获取滑块图片位点失败，无返回内容");
            return Optional.empty();
        }

        String data = JsonUtils.readField(response, "data.data");
        if (StringUtils.isBlank(data)) {
            log.error("获取滑块图片位点失败，无返回数据");
            return Optional.empty();
        }

        return Optional.of(Double.valueOf(data));
    }

    public List<Pair<Double, Double>> clickVerifyImagePoints(String imageBase64) {
        return clickVerifyImagePoints(imageBase64, null);
    }

    public List<Pair<Double, Double>> clickVerifyImagePoints(String imageBase64, String extra) {
        Map<String, String> params = Maps.newHashMap();
        params.put("type", "30009");
        params.put("token", token);
        params.put("image", imageBase64);
        if (extra != null) {
            params.put("extra", extra);
        }

        String response = myHttpClient.doPost(customerApi, params, headers);
        log.info("点选图片响应: {}", response);

        if (StringUtils.isBlank(response)) {
            log.error("获取点选图片位点失败，无返回内容");
            return Collections.emptyList();
        }

        String data = JsonUtils.readField(response, "data.data");
        if (StringUtils.isBlank(data)) {
            log.error("获取点选图片位点失败，无返回数据");
            return Collections.emptyList();
        }

        List<String> points = Symbols.VERTICAL_SPLITTER.splitToList(data);

        return Lambdas.mapToList(points, point -> {
            List<String> pointArr = Symbols.SPLITTER.splitToList(point);
            Pair<Double, Double> pointPair = ImmutablePair.of(Double.valueOf(pointArr.get(0)), Double.valueOf(pointArr.get(1)));

            return pointPair;
        });
    }

}
