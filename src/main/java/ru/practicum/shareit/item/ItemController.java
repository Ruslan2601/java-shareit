package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponse> addItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @Valid @RequestBody ItemDto itemDto, BindingResult bindingResult) {
        log.info("Получен POST запрос на добавление нового товара");
        return ResponseEntity.ok(itemService.addItem(userId, itemDto, bindingResult));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemResponse> updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @RequestBody ItemUpdate itemUpdate, @PathVariable("id") Integer itemId) {
        log.info("Получен Patch запрос на обновление товара");
        return ResponseEntity.ok(itemService.updateItem(userId, itemId, itemUpdate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItem(@PathVariable("id") Integer itemId) {
        log.info("Получен GET запрос на получение товара");
        return ResponseEntity.ok(itemService.getItem(itemId));
    }

    @GetMapping
    public ResponseEntity<Collection<ItemResponse>> getAllItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Получен GET запрос на получение всех товаров");
        return ResponseEntity.ok(itemService.getAllItems(userId));
    }

    @GetMapping("search")
    public ResponseEntity<Collection<ItemResponse>> search(@RequestParam String text) {
        log.info("Получен GET запрос на получение всех товаров по названию");
        return ResponseEntity.ok(itemService.search(text));
    }
}
