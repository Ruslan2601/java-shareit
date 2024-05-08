package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.util.Validation;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @Valid @RequestBody ItemRequestRequest itemRequestRequest, BindingResult bindingResult) {
        log.info("Получен POST запрос на добавление нового запроса на товар");
        Validation.validation(bindingResult);
        return itemRequestClient.addRequest(userId, itemRequestRequest);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable("requestId") Integer requestId,
                                          @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Получен GET запрос на получение запроса на товар");
        return itemRequestClient.getRequest(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Получен GET запрос на получение списка запросов на товар");
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsWithPagination(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                                  @RequestParam(defaultValue = "0")
                                                                  @Min(value = 0, message = "from должно быть больше или равно 0") int from,
                                                                  @RequestParam(defaultValue = "10")
                                                                  @Min(value = 1, message = "size должно быть больше 0") int size) {
        log.info("Получен GET запрос на получение списка запросов на товар с постраничным выводом");
        return itemRequestClient.getAllRequestsWithPagination(userId, from, size);
    }
}
