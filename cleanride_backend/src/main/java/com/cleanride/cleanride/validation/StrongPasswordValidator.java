package com.cleanride.cleanride.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.nio.charset.StandardCharsets;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // a null-t a @NotBlank kezeli
        }
        return value.length() >= 8
                && value.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}
