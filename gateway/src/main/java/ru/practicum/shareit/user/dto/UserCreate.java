package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class UserCreate {

    private int id;

    @NotEmpty(message = "email не может быть пустым")
    @Email(message = "электронная почта должна содержать символ @")
    private String email;

    @NotBlank(message = "поле name не может быть пустым")
    private String name;
}
