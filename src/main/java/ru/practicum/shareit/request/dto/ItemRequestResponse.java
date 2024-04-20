package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemInfoForItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestResponse {

    private int id;

    private String description;

    private User requester;

    private LocalDateTime created;

    private List<ItemInfoForItemRequest> items;
}
