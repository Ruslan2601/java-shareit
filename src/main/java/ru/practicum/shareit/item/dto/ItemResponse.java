package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserCreate;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemResponse {

    private int id;

    private String name;

    private String description;

    private Boolean available;

    private UserCreate owner;

    private ItemBooking lastBooking;

    private ItemBooking nextBooking;

    private int requestId;

    private List<CommentResponse> comments;
}
