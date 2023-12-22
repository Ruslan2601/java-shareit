package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserCreate;

@Data
@AllArgsConstructor
public class ItemResponse {

    private int id;

    private String name;

    private String description;

    private Boolean available;

    private UserCreate owner;

    private ItemRequest request;
}
