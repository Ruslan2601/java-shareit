package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface ItemStorage {

    Item addItem(Item item, User user);

    Item updateItem(Item item, Integer userId, Integer itemId);

    Item getItem(Integer itemId);

    Collection<Item> getAllItems(Integer userId);

    Collection<Item> search(String text);
}
