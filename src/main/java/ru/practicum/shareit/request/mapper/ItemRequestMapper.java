package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    ItemRequestResponse toItemRequestResponse(ItemRequest itemRequest);

    ItemRequest toItemRequest(ItemRequestRequest itemRequestRequest);
}
