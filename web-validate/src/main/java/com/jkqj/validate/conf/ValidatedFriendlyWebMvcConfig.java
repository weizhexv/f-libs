package com.jkqj.validate.conf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 扩展WebMvcConfigurationSupport来自定义requestMappingHandlerAdapter,从而改变HandlerMethodArgumentResolver来修改缺省的行为。
 * <p>
 * 在public String valid(@Valid @NotNull(message = "toUser参数不能为空") String toUser)模式下，修改BindException 为MethodArgumentNotValidException，
 * 增加MethodParameter信息，帮助实现自定义InvalidateCode功能
 * <p>
 * 要想使用该类，必须要继承一下，且增加 @Configuration。e.g.
 *

 * @Configuration public class WebConfig extends ValidatedFriendlyWebMvcConfig {
 *
 * }
 *
 *
 *  @author rolandhe
 */
@ConditionalOnBean(value = {WebMvcAutoConfiguration.class, RequestMappingHandlerAdapter.class})
public class ValidatedFriendlyWebMvcConfig {


    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;


    @PostConstruct
    public void init(){
        List<HandlerMethodArgumentResolver> resolvers =  requestMappingHandlerAdapter.getArgumentResolvers();
        requestMappingHandlerAdapter.setArgumentResolvers(changeResolvers(resolvers));
    }


    /**
     * 改变ServletModelAttributeMethodProcessor的行为，采用组装模式
     *
     * @param resolvers
     * @return
     */
    public static List<HandlerMethodArgumentResolver> changeResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        if (resolvers == null) {
            return null;
        }
        List<HandlerMethodArgumentResolver> resolverList = new ArrayList<>(resolvers.size());
        for (HandlerMethodArgumentResolver resolver : resolvers) {
            if ((!(resolver instanceof ServletModelAttributeMethodProcessor))) {
                resolverList.add(resolver);
                continue;
            }
            resolverList.add(build(resolver));
        }

        return resolverList;
    }

    /**
     * 采用组装模式封装ServletModelAttributeMethodProcessor对象，当validate失败时，改变BindException为MethodArgumentNotValidException
     *
     * @param resolver
     * @return
     */
    private static HandlerMethodArgumentResolver build(HandlerMethodArgumentResolver resolver) {
        return new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return resolver.supportsParameter(parameter);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
                try {
                    return resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
                } catch (BindException e) {
                    List<FieldError> fieldErrorList = e.getBindingResult().getFieldErrors();
                    boolean ret = true;
                    for (FieldError fieldError : fieldErrorList) {
                        ret = ret && isValidateError(fieldError);
                    }
                    if (ret) {
                        throw new MethodArgumentNotValidException(parameter, e.getBindingResult());
                    }
                    throw e;
                }
            }
        };
    }

    private static boolean isValidateError(FieldError fieldError) {
        if ("org.springframework.validation.beanvalidation.SpringValidatorAdapter$ViolationFieldError".equals(fieldError.getClass().getName())) {
            return true;
        }
        return false;
    }
}