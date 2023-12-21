package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item addItem(Item item, Integer userId);

    Item updateItem(Item item, Integer userId, Integer itemId);

    Item getItem(Integer itemId);

    Collection<Item> getAllItems(Integer userId);

    Collection<Item> search(String text);
}
