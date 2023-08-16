package com.jkqj.vcode.recognize.test;

import cn.hutool.core.codec.Base64;
import com.jkqj.common.utils.JsonUtils;
import com.jkqj.vcode.recognize.VcodeRecognizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class VcodeRecognizerTest {

    @Resource
    private VcodeRecognizer vcodeRecognizer;

    @Test
    public void clickVerifyImagePoints() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource[] resources = resolver.getResources("vcode.png");
        InputStream inputStream = resources[0].getInputStream();
        String image = Base64.encode(inputStream);

        List<Pair<Double, Double>> points = vcodeRecognizer.clickVerifyImagePoints(image);
        log.info("points: {}", JsonUtils.toJson(points));
    }

}
