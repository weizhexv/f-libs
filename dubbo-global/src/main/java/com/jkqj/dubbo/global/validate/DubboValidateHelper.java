package com.jkqj.dubbo.global.validate;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * dubbo参数验证工具
 *
 * @author rolandhe
 *
 */
@Slf4j
public class DubboValidateHelper {
    private static Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory().getValidator();

    private DubboValidateHelper(){}

    /**
     * 验证参数
     *
     * @param args
     */
    public static void validateParameter(Object[] args) {
        if (args == null || args.length == 0) {
            return;
        }
        for (Object a : args) {
            Class<?> clazz = a.getClass();
            if (clazz.isPrimitive() || clazz.getName().startsWith("java.")) {
                continue;
            }
            Set<ConstraintViolation<Object>> violationSet = validator.validate(a);
            if (violationSet == null || violationSet.size() == 0) {
                continue;
            }
            throw buildValidatedException(violationSet);
        }
    }

    private static DubboValidatedException buildValidatedException(Set<ConstraintViolation<Object>> violationSet) {
        List<String> messageList = violationSet.stream().map(objectConstraintViolation -> objectConstraintViolation.getMessage()).collect(Collectors.toList());
        return new DubboValidatedException(messageList);
    }
}
