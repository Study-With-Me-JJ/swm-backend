package com.jj.swm.global.common.annotation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = EnumMatchValidator.class)
public @interface EnumMatch {
    String message() default "Not Match Enum Value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] includes() default {};

    String[] excludes() default {};
}

class EnumMatchValidator implements ConstraintValidator<EnumMatch, Enum<?>> {

    private String[] includes;
    private String[] excludes;

    @Override
    public void initialize(EnumMatch constraintAnnotation) {
        includes = constraintAnnotation.includes();
        excludes = constraintAnnotation.excludes();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (includes.length == 0 && excludes.length == 0) {
            return true;
        }

        if (includes.length != 0) {
            for (String include : includes) {
                if (include.equals(value.name())) {
                    return true;
                }
            }

            return false;
        }

        for (String exclude : excludes) {
            if (exclude.equals(value.name())) {
                return false;
            }
        }

        return true;
    }
}
