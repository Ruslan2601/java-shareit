package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdate;
import ru.practicum.shareit.util.Validation;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @Valid @RequestBody ItemDto itemDto, BindingResult bindingResult) {
        log.info("Получен POST запрос на добавление нового товара");
        Validation.validation(bindingResult);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestBody ItemUpdate itemUpdate, @PathVariable("id") Integer itemId) {
        log.info("Получен Patch запрос на обновление товара");
        return itemClient.updateItem(userId, itemId, itemUpdate);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable("id") Integer itemId,
                                          @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Получен GET запрос на получение товара");
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @RequestParam(defaultValue = "0")
                                              @Min(value = 0, message = "from должно быть больше или равно 0") int from,
                                              @RequestParam(defaultValue = "10")
                                              @Min(value = 1, message = "size должно быть больше 0") int size) {
        log.info("Получен GET запрос на получение всех товаров");
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping("search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @RequestParam String text,
                                         @RequestParam(defaultValue = "0")
                                         @Min(value = 0, message = "from должно быть больше или равно 0") int from,
                                         @RequestParam(defaultValue = "10")
                                         @Min(value = 1, message = "size должно быть больше 0") int size) {
        log.info("Получен GET запрос на получение всех товаров по названию");
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestBody CommentDto commentDto,
                                             @PathVariable("itemId") Integer itemId) {
        log.info("Получен POST запрос на добавление нового комментария");
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
