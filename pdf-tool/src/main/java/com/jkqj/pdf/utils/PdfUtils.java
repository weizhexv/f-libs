package com.jkqj.pdf.utils;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public final class PdfUtils {

    private static final SpringTemplateEngine TEMPLATE_ENGINE;

    static {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        TEMPLATE_ENGINE = new SpringTemplateEngine();
        TEMPLATE_ENGINE.setTemplateResolver(resolver);
    }

    private static Resource DEFAULT_FONT_RESOURCE;
    static {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("fonts/yahei.ttf");
            DEFAULT_FONT_RESOURCE = resources[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String DEFAULT_FONT_FAMILY = "Microsoft YaHei";

    @SneakyThrows
    public static void buildPdf(String template, Map<String, Object> variables, OutputStream outputStream) {
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = TEMPLATE_ENGINE.process(template, context);
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFont(() -> {
            try {
                return DEFAULT_FONT_RESOURCE.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }, DEFAULT_FONT_FAMILY);
        builder.useFastMode();
        builder.withHtmlContent(htmlContent, "");
        builder.toStream(outputStream);
        builder.run();
    }

}
