package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final ItemRequestMapper mapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Transactional
    public ItemRequestResponse addRequest(int userId, ItemRequestRequest itemRequestRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователя с такими id нет"));
        ItemRequest itemRequest = mapper.toItemRequest(itemRequestRequest);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(user);
        log.info("Добавлен запрос на товар");
        return mapper.toItemRequestResponse(requestRepository.save(itemRequest));
    }

    public ItemRequestResponse getRequest(int userId, int requestId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователя с такими id нет"));
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new ObjectNotFoundException("Запроса с таким id нет"));
        log.info("Отображен запрос с id = {}", requestId);
        return createItemRequestResponseWithItemsInfo(itemRequest);
    }

    public List<ItemRequestResponse> getAllRequests(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователя с такими id нет"));
        log.info("Отображен список запросов с id пользователя = {}", userId);
        return requestRepository.findByRequesterIdOrderByCreatedDesc(userId).stream()
                .map(this::createItemRequestResponseWithItemsInfo).collect(Collectors.toList());
    }

    public List<ItemRequestResponse> getAllRequestsWithPagination(int userId, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователя с такими id нет"));
        Pageable sortedByCreated = PageRequest.of(from / size, size, Sort.by("created").descending());
        log.info("Отображен список запросов с id пользователя = {}", userId);
        return requestRepository.findByRequesterIdNot(userId, sortedByCreated).stream()
                .map(this::createItemRequestResponseWithItemsInfo).collect(Collectors.toList());
    }

    private ItemRequestResponse createItemRequestResponseWithItemsInfo(ItemRequest itemRequest) {
        ItemRequestResponse itemRequestResponse = mapper.toItemRequestResponse(itemRequest);
        itemRequestResponse.setItems(itemRepository.findByRequestIdOrderByRequestCreatedDesc(itemRequest.getId()).stream()
                .map(itemMapper::toItemInfoForItemRequest).collect(Collectors.toList()));
        return itemRequestResponse;
    }
}
