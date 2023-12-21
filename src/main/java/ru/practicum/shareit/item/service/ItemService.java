package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdate;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.util.ItemValidation;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper mapper;
    private final UserStorage userStorage;

    public Item addItem(Integer userId, ItemDto itemDto, BindingResult bindingResult) {
        ItemValidation.validation(bindingResult);
        itemDto.setOwner(userStorage.getUser(userId));
        return itemStorage.addItem(mapper.toItem(itemDto), userId);
    }

    public Item updateItem(Integer userId, Integer itemId, ItemUpdate itemUpdate) {
        return itemStorage.updateItem(mapper.toItem(itemUpdate), userId, itemId);
    }

    public Item getItem(Integer itemId) {
        return itemStorage.getItem(itemId);
    }

    public Collection<Item> getAllItems(Integer userId) {
        return itemStorage.getAllItems(userId);
    }

    public Collection<Item> search(String text) {
        return itemStorage.search(text);
    }
}
