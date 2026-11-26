package com.cleanride.cleanride.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
public @interface StrongPassword {
    String message() default "a jelszó legalább 8 karakter és legfeljebb 72 bájt legyen";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
