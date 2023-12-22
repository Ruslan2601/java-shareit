package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdate;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.util.ItemValidation;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper mapper;
    private final UserStorage userStorage;

    public ItemResponse addItem(Integer userId, ItemDto itemDto, BindingResult bindingResult) {
        ItemValidation.validation(bindingResult);
        return mapper.toItemResponse(itemStorage.addItem(mapper.toItem(itemDto), userStorage.getUser(userId)));
    }

    public ItemResponse updateItem(Integer userId, Integer itemId, ItemUpdate itemUpdate) {
        return mapper.toItemResponse(itemStorage.updateItem(mapper.toItem(itemUpdate), userId, itemId));
    }

    public ItemResponse getItem(Integer itemId) {
        return mapper.toItemResponse(itemStorage.getItem(itemId));
    }

    public Collection<ItemResponse> getAllItems(Integer userId) {
        return itemStorage.getAllItems(userId).stream().map(mapper::toItemResponse).collect(Collectors.toList());
    }

    public Collection<ItemResponse> search(String text) {
        return itemStorage.search(text).stream().map(mapper::toItemResponse).collect(Collectors.toList());
    }
}
