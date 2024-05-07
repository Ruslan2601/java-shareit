package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository repository;
    @Mock
    private ItemRequestMapper mapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemMapper itemMapper;
    @InjectMocks
    private RequestService service;

    private final User requester = new User(1, "asd@asd.ru", "Ruslan");
    private final ItemRequestResponse response = new ItemRequestResponse(1, "desc", requester, null, null);

    @Test
    void getRequest_whenAllOk() {
        User user = new User();
        ItemRequest itemRequest = new ItemRequest();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findById(anyInt())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestIdOrderByRequestCreatedDesc(anyInt())).thenReturn(List.of());
        when(mapper.toItemRequestResponse(itemRequest)).thenReturn(response);

        assertEquals(response, service.getRequest(1, 1));
    }

    @Test
    void getRequest_whenUserNotFound() {
        assertThrows(ObjectNotFoundException.class, () -> service.getRequest(1, 1));
    }

    @Test
    void getRequest_whenItemRequestNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getRequest(1, 1));
    }

    @Test
    void getAllRequests_whenAllOk() {
        User user = new User();
        ItemRequest itemRequest = new ItemRequest();

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findByRequesterIdOrderByCreatedDesc(anyInt())).thenReturn(List.of(itemRequest, itemRequest, itemRequest));
        when(itemRepository.findByRequestIdOrderByRequestCreatedDesc(anyInt())).thenReturn(List.of());
        when(mapper.toItemRequestResponse(itemRequest)).thenReturn(response);

        List<ItemRequestResponse> itemsRequestResponse = service.getAllRequests(1);

        assertEquals(3, itemsRequestResponse.size());
        assertEquals(response, itemsRequestResponse.get(0));
        assertEquals(response, itemsRequestResponse.get(1));
        assertEquals(response, itemsRequestResponse.get(2));
    }

    @Test
    void getAllRequests_whenUserNotFound() {
        assertThrows(ObjectNotFoundException.class, () -> service.getAllRequests(1));
    }

    @Test
    void getAllRequestsWithPagination_whenAllOk() {
        User user = new User();
        ItemRequest itemRequest = new ItemRequest();

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findByRequesterIdNot(anyInt(), any(Pageable.class))).thenReturn(List.of(itemRequest, itemRequest, itemRequest));
        when(itemRepository.findByRequestIdOrderByRequestCreatedDesc(anyInt())).thenReturn(List.of());
        when(mapper.toItemRequestResponse(itemRequest)).thenReturn(response);

        List<ItemRequestResponse> itemsRequestResponse = service.getAllRequestsWithPagination(1, 1, 1);

        assertEquals(3, itemsRequestResponse.size());
        assertEquals(response, itemsRequestResponse.get(0));
        assertEquals(response, itemsRequestResponse.get(1));
        assertEquals(response, itemsRequestResponse.get(2));
    }

    @Test
    void getAllRequestsWithPagination_whenUserNotFound() {
        assertThrows(ObjectNotFoundException.class, () -> service.getAllRequestsWithPagination(1, 1, 1));
    }

    @Test
    void addRequest_whenAllOk() {
        User user = new User();
        ItemRequestRequest itemRequestRequest = new ItemRequestRequest();
        ItemRequest itemRequest = new ItemRequest();

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(mapper.toItemRequest(itemRequestRequest)).thenReturn(itemRequest);
        when(repository.save(itemRequest)).thenReturn(itemRequest);
        when(mapper.toItemRequestResponse(itemRequest)).thenReturn(response);

        assertEquals(response, service.addRequest(1, itemRequestRequest));
    }

    @Test
    void addRequest_whenUserNotFound() {
        assertThrows(ObjectNotFoundException.class, () -> service.addRequest(1, new ItemRequestRequest()));
    }
}