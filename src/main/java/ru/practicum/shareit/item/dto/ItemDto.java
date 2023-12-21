package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.item.util.NotNull;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class ItemDto {

    private Integer id;

    @NotBlank(message = "поле name не может быть пустым")
    private String name;

    @NotBlank(message = "поле description не может быть пустым")
    private String description;

    @NotNull
    private Boolean available;
}
