package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoForItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toItem(ItemDto itemDto);

    Item toItem(ItemResponse itemResponse);

    ItemResponse toItemResponse(Item item);

    @Mapping(target = "requestId", expression = "java(item.getRequest().getId())")
    ItemInfoForItemRequest toItemInfoForItemRequest(Item item);
}
