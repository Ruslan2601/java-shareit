package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final RequestService requestService;

    @PostMapping
    public ItemRequestResponse addRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @Valid @RequestBody ItemRequestRequest itemRequestRequest, BindingResult bindingResult) {
        log.info("Получен POST запрос на добавление нового запроса на товар");
        return requestService.addRequest(userId, itemRequestRequest, bindingResult);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse getRequest(@PathVariable("requestId") Integer requestId,
                                          @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Получен GET запрос на получение запроса на товар");
        return requestService.getRequest(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestResponse> getAllRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Получен GET запрос на получение списка запросов на товар");
        return requestService.getAllRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponse> getAllRequestsWithPagination(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                                  @RequestParam(defaultValue = "0")
                                                                  @Min(value = 0, message = "from должно быть больше или равно 0") int from,
                                                                  @RequestParam(defaultValue = "10")
                                                                  @Min(value = 1, message = "size должно быть больше 0") int size) {
        log.info("Получен GET запрос на получение списка запросов на товар с постраничным выводом");
        return requestService.getAllRequestsWithPagination(userId, from, size);
    }
}
