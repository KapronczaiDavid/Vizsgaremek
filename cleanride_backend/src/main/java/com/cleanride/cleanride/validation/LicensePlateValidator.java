package com.cleanride.cleanride.validation;

import com.cleanride.cleanride.util.LicensePlateNormalizer;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LicensePlateValidator implements ConstraintValidator<LicensePlate, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String error = LicensePlateNormalizer.check(value);
        if (error == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
        return false;
    }
}
