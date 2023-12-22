package ru.practicum.shareit.item.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidationNotNull implements ConstraintValidator<NotNull, Boolean> {
    @Override
    public void initialize(NotNull constraintAnnotation) {
    }

    @Override
    public boolean isValid(Boolean string, ConstraintValidatorContext context) {
        return string != null;
    }
}
