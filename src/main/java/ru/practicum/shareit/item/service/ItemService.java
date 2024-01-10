package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdate;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.util.ItemValidation;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemMapper mapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public ItemResponse addItem(Integer userId, ItemDto itemDto, BindingResult bindingResult) {
        ItemValidation.validation(bindingResult);
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователя с такими id нет"));
        Item item = mapper.toItem(itemDto);
        item.setOwner(user);
        log.info("Добавлен товар");
        return mapper.toItemResponse(itemRepository.save(item));
    }

    @Transactional
    public ItemResponse updateItem(Integer userId, Integer itemId, ItemUpdate itemUpdate) {
        Item thisItem = mapper.toItem(getItem(itemId));
        if (thisItem.getOwner().getId() == userId) {
            if (itemUpdate.getName() != null) {
                thisItem.setName(itemUpdate.getName());
            }
            if (itemUpdate.getDescription() != null) {
                thisItem.setDescription(itemUpdate.getDescription());
            }
            if (itemUpdate.getAvailable() != null) {
                thisItem.setAvailable(itemUpdate.getAvailable());
            }
            itemRepository.save(thisItem);
            log.info("Обновлен товар");
            return mapper.toItemResponse(thisItem);
        } else {
            throw new ObjectNotFoundException("Данный товар не принадлежит пользователю с id = " + userId);
        }
    }

    public ItemResponse getItem(Integer itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Товара с такими id нет"));
        log.info("Отображен товар с id = {}", itemId);
        return mapper.toItemResponse(item);
    }

    public Collection<ItemResponse> getAllItems(Integer userId) {
        log.info("Отображен список всех товаров пользователя");
        return itemRepository.findByOwnerId(userId).stream().map(mapper::toItemResponse).collect(Collectors.toList());
    }

    public Collection<ItemResponse> search(String text) {
        log.info("Вывод результатов поиска");
        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        return itemRepository.findAll().stream()
                .filter(x -> x.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
                        || x.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)))
                .filter(Item::getAvailable)
                .map(mapper::toItemResponse)
                .collect(Collectors.toList());
    }
}
