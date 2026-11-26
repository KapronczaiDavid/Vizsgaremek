package com.cleanride.cleanride.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LicensePlateValidator.class)
public @interface LicensePlate {
    String message() default "érvénytelen rendszám";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
