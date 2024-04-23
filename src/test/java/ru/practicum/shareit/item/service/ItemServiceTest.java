package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository repository;
    @Mock
    private ItemMapper mapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private ItemService service;

    @Test
    void getItem_whenAllOk() {
        User owner = new User();
        owner.setId(1);

        Item item = new Item();
        item.setOwner(owner);

        User booker = new User();
        booker.setId(2);

        Booking lastBooking = new Booking(5, LocalDateTime.now(), LocalDateTime.now(), null, booker, Status.APPROVED);
        Booking nextBooking = new Booking(6, LocalDateTime.now(), LocalDateTime.now(), null, booker, Status.APPROVED);

        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setNextBooking(new ItemBooking(5, 2));
        itemResponse.setLastBooking(new ItemBooking(6, 2));

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(repository.findById(anyInt())).thenReturn(Optional.of(item));
        when(mapper.toItemResponse(item)).thenReturn(itemResponse);
        when(commentRepository.findByItemId(anyInt())).thenReturn(List.of());
        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(anyInt(), anyInt(), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(lastBooking);
        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(anyInt(), anyInt(), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(nextBooking);

        assertEquals(itemResponse, service.getItem(1, 1));
    }

    @Test
    void getItem_whenUserNotFound() {
        Item item = new Item();

        when(repository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getItem(1, 1));
    }

    @Test
    void getItem_whenItemNotFound() {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getItem(1, 1));
    }

    @Test
    void getAllItems_whenAllOk() {
        User user = new User();
        user.setId(1);
        Item item = new Item();
        item.setOwner(user);
        ItemResponse itemResponse = new ItemResponse();

        when(repository.findByOwnerId(anyInt(), any(Pageable.class))).thenReturn(List.of(item, item, item));
        when(mapper.toItemResponse(item)).thenReturn(itemResponse);
        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(anyInt(), anyInt(), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(null);
        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(anyInt(), anyInt(), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(null);

        List<ItemResponse> itemResponses = new ArrayList<>(service.getAllItems(1, 1, 1));

        assertEquals(3, itemResponses.size());
        assertEquals(itemResponse, itemResponses.get(0));
        assertEquals(itemResponse, itemResponses.get(1));
        assertEquals(itemResponse, itemResponses.get(2));
    }

    @Test
    void searchItems_whenAllOk() {
        Item item = new Item();
        ItemResponse itemResponse = new ItemResponse();

        when(repository.search(anyString(), any(Pageable.class))).thenReturn(List.of(item, item, item));
        when(mapper.toItemResponse(item)).thenReturn(itemResponse);

        List<ItemResponse> itemResponses = new ArrayList<>(service.search("text", 1, 1));

        assertEquals(3, itemResponses.size());
        assertEquals(itemResponse, itemResponses.get(0));
        assertEquals(itemResponse, itemResponses.get(1));
        assertEquals(itemResponse, itemResponses.get(2));
    }

    @Test
    void searchItems_whenTextEmpty() {
        Item item = new Item();

        assertEquals(Collections.emptyList(), service.search("", 1, 1));
    }

    @Test
    void addItem_whenAllOk() {
        User user = new User();

        ItemDto itemDto = new ItemDto(1, "Ruslan", "man", true, 1);

        Item item = new Item();

        ItemResponse itemResponse = new ItemResponse();

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(mapper.toItem(itemDto)).thenReturn(item);
        when(requestRepository.findById(anyInt())).thenReturn(Optional.of(new ItemRequest()));
        when(repository.save(item)).thenReturn(item);
        when(mapper.toItemResponse(item)).thenReturn(itemResponse);

        assertEquals(itemResponse, service.addItem(1, itemDto));
    }

    @Test
    void addItem_whenReqEmpty() {
        User user = new User();

        ItemDto itemDto = new ItemDto(1, "Ruslan", "man", true, null);

        Item item = new Item();

        ItemResponse itemResponse = new ItemResponse();

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(mapper.toItem(itemDto)).thenReturn(item);
        when(repository.save(item)).thenReturn(item);
        when(mapper.toItemResponse(item)).thenReturn(itemResponse);

        assertEquals(itemResponse, service.addItem(1, itemDto));
    }

    @Test
    void addItem_whenUserNotFound() {
        ItemDto itemDto = new ItemDto(1, "Ruslan", "man", true, 1);

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.addItem(1, itemDto));
    }

    @Test
    void addItem_whenItemRequestNotFound() {
        ItemDto itemDto = new ItemDto(1, "Ruslan", "man", true, 1);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));
        when(mapper.toItem(itemDto)).thenReturn(new Item());
        when(requestRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.addItem(1, itemDto));
    }

    @Test
    void addComment_whenAllOk() {
        User owner = new User();
        Item item = new Item();
        owner.setId(1);
        item.setOwner(owner);

        CommentDto commentRequest = new CommentDto();
        commentRequest.setText("123");
        Comment comment = new Comment();
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setText("123");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));
        when(repository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(anyInt(), anyInt(), any(LocalDateTime.class))).thenReturn(Optional.of(new Booking()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponse commentResponseResult = service.addComment(2, commentRequest, 2);

        assertEquals(commentResponse.getId(), commentResponseResult.getId());
        assertEquals(commentResponse.getText(), commentResponseResult.getText());
        assertEquals(commentResponse.getAuthorName(), commentResponseResult.getAuthorName());

    }

    @Test
    void addComment_whenUserNotFound() {
        CommentDto commentRequest = new CommentDto();
        commentRequest.setText("123");
        Item item = new Item();

        when(repository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.addComment(1, commentRequest, 1));
    }

    @Test
    void addComment_whenItemNotFound() {
        CommentDto commentRequest = new CommentDto();
        commentRequest.setText("123");

        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.addComment(1, commentRequest, 1));
    }

    @Test
    void addComment_whenOwnerAddComment() {
        CommentDto commentRequest = new CommentDto();
        commentRequest.setText("123");
        User owner = new User();
        Item item = new Item();
        owner.setId(1);
        item.setOwner(owner);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(anyInt(), anyInt(), any(LocalDateTime.class))).thenReturn(Optional.of(new Booking()));
        when(repository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> service.addComment(1, commentRequest, 1));
    }

    @Test
    void addComment_whenUserDontRent() {
        CommentDto commentRequest = new CommentDto();
        commentRequest.setText("123");
        User owner = new User();
        Item item = new Item();
        owner.setId(1);
        item.setOwner(owner);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));
        when(repository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> service.addComment(1, commentRequest, 1));
    }

    @Test
    void addComment_EmptyTextField() {
        CommentDto commentRequest = new CommentDto();
        commentRequest.setText("");

        assertThrows(ValidationException.class, () -> service.addComment(1, commentRequest, 1));
    }

    @Test
    void updateItem_WhenUpdateName() {
        ItemUpdate itemUpdateReq = new ItemUpdate(1, "Ruslan", "man", true);


        Item itemOld = new Item();
        itemOld.setDescription("description old");
        itemOld.setAvailable(true);

        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setId(1);

        User owner = new User();
        owner.setId(1);
        itemOld.setOwner(owner);

        Item itemUpdate = new Item();
        itemUpdate.setOwner(owner);
        itemUpdate.setName("name");

        when(mapper.toItemResponse(any(Item.class))).thenReturn(itemResponse);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(repository.findById(anyInt())).thenReturn(Optional.of(itemOld));
        when(mapper.toItem(itemResponse)).thenReturn(itemUpdate);
        when(repository.save(itemUpdate)).thenReturn(itemUpdate);
        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(anyInt(), anyInt(), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(null);
        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(anyInt(), anyInt(), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(null);

        assertEquals(itemResponse, service.updateItem(1, 1, itemUpdateReq));
    }

    @Test
    void updateItem_WhenItemNotFound() {
        ItemUpdate itemUpdateReq = new ItemUpdate(1, "Ruslan", "man", true);

        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.updateItem(1, 1, itemUpdateReq));
    }

    @Test
    void updateItem_WhenUserNotFound() {
        ItemUpdate itemUpdateReq = new ItemUpdate(1, "Ruslan", "man", true);

        Item itemOld = new Item();
        User owner = new User();
        owner.setId(1);
        itemOld.setOwner(owner);

        when(repository.findById(anyInt())).thenReturn(Optional.of(itemOld));

        assertThrows(ObjectNotFoundException.class, () -> service.updateItem(1, 1, itemUpdateReq));
    }

    @Test
    void updateItem_WhenUserNotOwner() {
        ItemUpdate itemUpdateReq = new ItemUpdate(1, "Ruslan", "man", true);


        Item itemOld = new Item();
        itemOld.setDescription("description old");
        itemOld.setAvailable(true);

        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setId(1);

        User owner = new User();
        owner.setId(1);
        itemOld.setOwner(owner);

        User owner2 = new User();
        owner.setId(2);

        Item itemUpdate = new Item();
        itemUpdate.setOwner(owner2);
        itemUpdate.setName("name");

        when(mapper.toItemResponse(any(Item.class))).thenReturn(itemResponse);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(repository.findById(anyInt())).thenReturn(Optional.of(itemOld));
        when(mapper.toItem(itemResponse)).thenReturn(itemUpdate);
        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(anyInt(), anyInt(), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(null);
        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(anyInt(), anyInt(), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(null);

        assertThrows(ObjectNotFoundException.class, () -> service.updateItem(1, 1, itemUpdateReq));
    }

    @Test
    void updateItem_WhenAllNull() {
        ItemUpdate itemUpdateReq = new ItemUpdate(1, null, null, null);


        Item itemOld = new Item();
        itemOld.setDescription("description old");
        itemOld.setAvailable(true);

        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setId(1);

        User owner = new User();
        owner.setId(1);
        itemOld.setOwner(owner);

        Item itemUpdate = new Item();
        itemUpdate.setOwner(owner);
        itemUpdate.setName("name");

        when(mapper.toItemResponse(any(Item.class))).thenReturn(itemResponse);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(repository.findById(anyInt())).thenReturn(Optional.of(itemOld));
        when(mapper.toItem(itemResponse)).thenReturn(itemUpdate);
        when(repository.save(itemUpdate)).thenReturn(itemUpdate);
        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(anyInt(), anyInt(), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(null);
        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(anyInt(), anyInt(), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(null);

        assertEquals(itemResponse, service.updateItem(1, 1, itemUpdateReq));
    }
}