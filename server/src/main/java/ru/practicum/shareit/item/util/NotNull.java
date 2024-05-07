package ru.practicum.shareit.item.util;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidationNotNull.class)
public @interface NotNull {
    String message() default "Не может быть null";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
