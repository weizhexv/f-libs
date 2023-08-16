package com.jkqj.common.validation;

import com.jkqj.common.utils.MyEnumUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * 枚举验证器
 *
 * @author cb
 * @date 2021-12-27
 */
public class EnumCheckValidator implements ConstraintValidator<EnumCheck, Object> {
    /**
     * 注解对象
     */
    private EnumCheck annotation;

    /**
     * 初始化方法
     *
     * @param constraintAnnotation 注解对象
     */
    @Override
    public void initialize(EnumCheck constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return false;
        }

        if (StringUtils.isNotBlank(annotation.fieldName())) {
            return MyEnumUtils.withField(annotation.clazz(), annotation.fieldName(), value) != null;
        }

        return EnumUtils.isValidEnum(annotation.clazz(), String.valueOf(value));
    }
}
