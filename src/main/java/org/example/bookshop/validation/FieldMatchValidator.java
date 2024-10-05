package org.example.bookshop.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.beanutils.BeanUtils;
import org.example.bookshop.annotation.FieldMatch;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            String firstValue = BeanUtils.getProperty(object, firstFieldName);
            String secondValue = BeanUtils.getProperty(object, secondFieldName);

            return firstValue != null && firstValue.equals(secondValue);
        } catch (Exception e) {
            return false;
        }
    }
}
