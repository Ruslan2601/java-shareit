package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdate;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toItem(ItemDto itemDto);

    ItemDto toItemDto(Item item);

    Item toItem(ItemUpdate itemUpdate);

    ItemUpdate toItemUpdate(Item item);

    Item toItem(ItemResponse itemResponse);

    ItemResponse toItemResponse(Item item);
}
