package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
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
    public ItemResponse addItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @Valid @RequestBody ItemDto itemDto, BindingResult bindingResult) {
        log.info("Получен POST запрос на добавление нового товара");
        return itemService.addItem(userId, itemDto, bindingResult);
    }

    @PatchMapping("/{id}")
    public ItemResponse updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                   @RequestBody ItemUpdate itemUpdate, @PathVariable("id") Integer itemId) {
        log.info("Получен Patch запрос на обновление товара");
        return itemService.updateItem(userId, itemId, itemUpdate);
    }

    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable("id") Integer itemId,
                                                @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Получен GET запрос на получение товара");
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public Collection<ItemResponse> getAllItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Получен GET запрос на получение всех товаров");
        return itemService.getAllItems(userId);
    }

    @GetMapping("search")
    public Collection<ItemResponse> search(@RequestParam String text) {
        log.info("Получен GET запрос на получение всех товаров по названию");
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                      @RequestBody CommentDto commentDto,
                                                      @PathVariable("itemId") Integer itemId) {
        log.info("Получен POST запрос на добавление нового коментария");
        return itemService.addComment(userId, commentDto, itemId);
    }
}
