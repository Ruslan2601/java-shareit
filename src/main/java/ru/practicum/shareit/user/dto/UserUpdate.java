package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserUpdate {

    @Email(message = "электронная почта должна содержать символ @")
    private String email;

    private String name;
}
