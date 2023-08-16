package com.jkqj.pdf.utils;

import com.jkqj.common.utils.MyMapUtils;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * PDF工具类测试
 *
 * @author cb
 * @date 2022-09-16
 */
public class PdfUtilsTest {

    @Test
    public void buildResumePdf() throws FileNotFoundException {
        Map<String, Object> variables = Map.of(
                "subject", "陈彪的简历",
                "basic", MyMapUtils.newLinkedHashMap(
                        "name", "陈彪",
                        "mobile", "15901431753",
                        "email", "chenbiao@reta-inc.com",
                        "age", "18"
                ),
                "advantage", MyMapUtils.newLinkedHashMap(
                        "summary", "聪明；皮实；健康；活泼；"
                )
        );

        OutputStream outputStream = new FileOutputStream("/Users/mac/Downloads/resume.pdf");

        PdfUtils.buildPdf("resume", variables, outputStream);
    }

}
