package com.jkqj.common.validation;

import com.jkqj.common.utils.ValidationUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 手机号验证器
 *
 * @author cb
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    @Override
    public void initialize(IsMobile constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.isBlank(value) || ValidationUtils.isMobile(value);
    }
}
