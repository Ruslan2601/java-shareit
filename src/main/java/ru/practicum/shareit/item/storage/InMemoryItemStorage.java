package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {

    protected final Map<Integer, Item> itemMap = new HashMap<>();
    private int id = 0;

    @Override
    public Item addItem(Item item, Integer userId) {
        item.setId(++id);
        itemMap.put(item.getId(), item);
        log.info("Добавлен товар");
        return item;
    }

    @Override
    public Item updateItem(Item item, Integer userId, Integer itemId) {
        getItem(itemId);
        Item thisItem = itemMap.get(itemId);
        if (thisItem.getOwner().getId() == userId) {
            if (item.getName() != null) {
                thisItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                thisItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                thisItem.setAvailable(item.getAvailable());
            }
            itemMap.put(itemId, thisItem);
            log.info("Обновлен товар");
            return thisItem;
        } else {
            throw new ObjectNotFoundException("Данный товар не принадлежит пользователю с id = " + userId);
        }
    }

    @Override
    public Item getItem(Integer itemId) {
        if (itemMap.containsKey(itemId)) {
            log.info("Отображен товар с id = {}", itemId);
            return itemMap.get(itemId);
        } else {
            throw new ObjectNotFoundException("Товара с такими id нет");
        }
    }

    @Override
    public Collection<Item> getAllItems(Integer userId) {
        log.info("Обновлен список всех товаров пользователя");
        return itemMap.values().stream().filter(x -> x.getOwner().getId() == userId).collect(Collectors.toList());
    }

    @Override
    public Collection<Item> search(String text) {
        log.info("Вывод результатов поиска");
        if (text.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return itemMap.values().stream()
                .filter(x -> x.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
                        || x.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
