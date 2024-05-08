package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ItemRequestRequest {

    @NotBlank(message = "поле description не может быть пустым")
    private String description;
}
