package ru.practicum.shareit.request.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

public class ItemRequestValidation {
    public static void validation(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            }
            throw new ValidationException(errorMsg.toString());
        }
    }
}
