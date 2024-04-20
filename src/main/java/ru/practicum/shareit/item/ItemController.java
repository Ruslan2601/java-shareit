package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

@Slf4j
@Validated
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
    public Collection<ItemResponse> getAllItems(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @RequestParam(defaultValue = "0")
                                                @Min(value = 0, message = "from должно быть больше или равно 0") int from,
                                                @RequestParam(defaultValue = "10")
                                                @Min(value = 1, message = "size должно быть больше 0") int size) {
        log.info("Получен GET запрос на получение всех товаров");
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping("search")
    public Collection<ItemResponse> search(@RequestParam String text,
                                           @RequestParam(defaultValue = "0")
                                           @Min(value = 0, message = "from должно быть больше или равно 0") int from,
                                           @RequestParam(defaultValue = "10")
                                           @Min(value = 1, message = "size должно быть больше 0") int size) {
        log.info("Получен GET запрос на получение всех товаров по названию");
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @RequestBody CommentDto commentDto,
                                      @PathVariable("itemId") Integer itemId) {
        log.info("Получен POST запрос на добавление нового коментария");
        return itemService.addComment(userId, commentDto, itemId);
    }
}
