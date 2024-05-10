package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository repository;
    @Mock
    private BookingMapper mapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingService service;

    @Test
    void getBooking_Ok() {
        Booking booking = new Booking();
        booking.setBooker(new User(1, null, null));
        booking.setItem(new Item(1, null, null, null, new User(2, null, null), null));

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setBooker(booking.getBooker());
        bookingResponse.setItem(booking.getItem());

        when(repository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        assertEquals(bookingResponse, service.getBooking(1, 1));
    }

    @Test
    void getBooking_whenBookingNotFound() {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getBooking(1, 1));
    }

    @Test
    void getBooking_whenNotAccessUser() {
        Booking booking = new Booking();
        booking.setBooker(new User(1, null, null));
        booking.setItem(new Item(1, null, null, null, new User(2, null, null), null));

        when(repository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(ObjectNotFoundException.class, () -> service.getBooking(3, 1));
    }

    @Test
    void getBookingListByItemOwner_whenAllState() {
        Booking booking = new Booking();
        BookingResponse bookingResponse = new BookingResponse();
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class))).thenReturn(List.of(booking, booking, booking));
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> bookingResponses = service.getBookingListByItemOwner(1, "ALL", 1, 1);

        assertEquals(3, bookingResponses.size());
        assertEquals(bookingResponse, bookingResponses.get(0));
        assertEquals(bookingResponse, bookingResponses.get(1));
        assertEquals(bookingResponse, bookingResponses.get(2));
    }

    @Test
    void getBookingListByItemOwner_whenWaitingState() {
        Booking booking = new Booking();
        BookingResponse bookingResponse = new BookingResponse();
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findAllByItemOwnerAndStatusOrderByStartDesc(any(User.class), any(Status.class), any(Pageable.class))).thenReturn(List.of(booking, booking, booking));
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> bookingResponses = service.getBookingListByItemOwner(1, "WAITING", 1, 1);

        assertEquals(3, bookingResponses.size());
        assertEquals(bookingResponse, bookingResponses.get(0));
        assertEquals(bookingResponse, bookingResponses.get(1));
        assertEquals(bookingResponse, bookingResponses.get(2));
    }

    @Test
    void getBookingListByItemOwner_whenRejectedState() {
        Booking booking = new Booking();
        BookingResponse bookingResponse = new BookingResponse();
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findAllByItemOwnerAndStatusOrderByStartDesc(any(User.class), any(Status.class), any(Pageable.class))).thenReturn(List.of(booking, booking, booking));
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> bookingResponses = service.getBookingListByItemOwner(1, "REJECTED", 1, 1);

        assertEquals(3, bookingResponses.size());
        assertEquals(bookingResponse, bookingResponses.get(0));
        assertEquals(bookingResponse, bookingResponses.get(1));
        assertEquals(bookingResponse, bookingResponses.get(2));
    }

    @Test
    void getBookingListByItemOwner_whenPastState() {
        Booking booking = new Booking();
        BookingResponse bookingResponse = new BookingResponse();
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(any(User.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking, booking, booking));
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> bookingResponses = service.getBookingListByItemOwner(1, "PAST", 1, 1);

        assertEquals(3, bookingResponses.size());
        assertEquals(bookingResponse, bookingResponses.get(0));
        assertEquals(bookingResponse, bookingResponses.get(1));
        assertEquals(bookingResponse, bookingResponses.get(2));
    }

    @Test
    void getBookingListByItemOwner_whenCurrentState() {
        Booking booking = new Booking();
        BookingResponse bookingResponse = new BookingResponse();
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking, booking, booking));
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> bookingResponses = service.getBookingListByItemOwner(1, "CURRENT", 1, 1);

        assertEquals(3, bookingResponses.size());
        assertEquals(bookingResponse, bookingResponses.get(0));
        assertEquals(bookingResponse, bookingResponses.get(1));
        assertEquals(bookingResponse, bookingResponses.get(2));
    }

    @Test
    void getBookingListByItemOwner_whenFutureState() {
        Booking booking = new Booking();
        BookingResponse bookingResponse = new BookingResponse();
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findAllByItemOwnerAndStartAfterOrderByStartDesc(any(User.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking, booking, booking));
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> bookingResponses = service.getBookingListByItemOwner(1, "FUTURE", 1, 1);

        assertEquals(3, bookingResponses.size());
        assertEquals(bookingResponse, bookingResponses.get(0));
        assertEquals(bookingResponse, bookingResponses.get(1));
        assertEquals(bookingResponse, bookingResponses.get(2));
    }

    @Test
    void getBookingListByItemOwner_whenUserNotFound() {
        assertThrows(ObjectNotFoundException.class, () -> service.getBookingListByItemOwner(1, "", 1, 1));
    }

    @Test
    void getBookingListByItemOwner_whenUnknownState() {
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> service.getBookingListByItemOwner(1, "UNSUPPORTED_STATUS", 1, 1));
    }

    @Test
    void getBookingList_whenAllState() {
        Booking booking = new Booking();
        BookingResponse bookingResponse = new BookingResponse();
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findAllByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class))).thenReturn(List.of(booking, booking, booking));
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> bookingResponses = service.getBookingList(1, "ALL", 1, 1);

        assertEquals(3, bookingResponses.size());
        assertEquals(bookingResponse, bookingResponses.get(0));
        assertEquals(bookingResponse, bookingResponses.get(1));
        assertEquals(bookingResponse, bookingResponses.get(2));
    }

    @Test
    void getBookingList_whenWaitingState() {
        Booking booking = new Booking();
        BookingResponse bookingResponse = new BookingResponse();
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findAllByBookerIdAndStatusOrderByStartDesc(anyInt(),any(Status.class), any(Pageable.class))).thenReturn(List.of(booking, booking, booking));
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> bookingResponses = service.getBookingList(1, "WAITING", 1, 1);

        assertEquals(3, bookingResponses.size());
        assertEquals(bookingResponse, bookingResponses.get(0));
        assertEquals(bookingResponse, bookingResponses.get(1));
        assertEquals(bookingResponse, bookingResponses.get(2));
    }

    @Test
    void getBookingList_whenRejectedState() {
        Booking booking = new Booking();
        BookingResponse bookingResponse = new BookingResponse();
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findAllByBookerIdAndStatusOrderByStartDesc(anyInt(),any(Status.class), any(Pageable.class))).thenReturn(List.of(booking, booking, booking));
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> bookingResponses = service.getBookingList(1, "REJECTED", 1, 1);

        assertEquals(3, bookingResponses.size());
        assertEquals(bookingResponse, bookingResponses.get(0));
        assertEquals(bookingResponse, bookingResponses.get(1));
        assertEquals(bookingResponse, bookingResponses.get(2));
    }

    @Test
    void getBookingList_whenPastState() {
        Booking booking = new Booking();
        BookingResponse bookingResponse = new BookingResponse();
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyInt(),any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking, booking, booking));
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> bookingResponses = service.getBookingList(1, "PAST", 1, 1);

        assertEquals(3, bookingResponses.size());
        assertEquals(bookingResponse, bookingResponses.get(0));
        assertEquals(bookingResponse, bookingResponses.get(1));
        assertEquals(bookingResponse, bookingResponses.get(2));
    }

    @Test
    void getBookingList_whenCurrentState() {
        Booking booking = new Booking();
        BookingResponse bookingResponse = new BookingResponse();
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyInt(),any(LocalDateTime.class),any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking, booking, booking));
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> bookingResponses = service.getBookingList(1, "CURRENT", 1, 1);

        assertEquals(3, bookingResponses.size());
        assertEquals(bookingResponse, bookingResponses.get(0));
        assertEquals(bookingResponse, bookingResponses.get(1));
        assertEquals(bookingResponse, bookingResponses.get(2));
    }

    @Test
    void getBookingList_whenFutureState() {
        Booking booking = new Booking();
        BookingResponse bookingResponse = new BookingResponse();
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(repository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyInt(),any(LocalDateTime.class), any(Pageable.class))).thenReturn(List.of(booking, booking, booking));
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        List<BookingResponse> bookingResponses = service.getBookingList(1, "FUTURE", 1, 1);

        assertEquals(3, bookingResponses.size());
        assertEquals(bookingResponse, bookingResponses.get(0));
        assertEquals(bookingResponse, bookingResponses.get(1));
        assertEquals(bookingResponse, bookingResponses.get(2));
    }

    @Test
    void getBookingList_whenUserNotFound() {
        assertThrows(ObjectNotFoundException.class, () -> service.getBookingList(1, "", 1, 1));
    }

    @Test
    void getBookingList_whenUnknownState() {
        User user = new User(1, "asd@sd.ru", "Ruslan");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> service.getBookingList(1, "UNSUPPORTED_STATUS", 1, 1));
    }

    @Test
    void addBooking_whenAllOk() {
        User booker = new User();
        booker.setId(1);

        User owner = new User();
        owner.setId(2);

        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(true);

        BookingDto bookingRequest = new BookingDto(1,
                LocalDateTime.of(2020, 1, 1, 1, 1, 1),
                LocalDateTime.of(2021, 1, 1, 1, 1, 1),
                1);

        Booking booking = new Booking();

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setItem(item);
        bookingResponse.setBooker(booker);
        bookingResponse.setStatus(Status.WAITING);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(mapper.toBooking(bookingRequest)).thenReturn(booking);
        when(repository.save(booking)).thenReturn(booking);
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        assertEquals(bookingResponse, service.addBooking(1, bookingRequest));
    }

    @Test
    void addBooking_whenUserNotFound() {
        Item item = new Item();
        item.setAvailable(true);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        assertThrows(ObjectNotFoundException.class, () -> service.addBooking(1, new BookingDto()));
    }

    @Test
    void addBooking_whenItemNotFound() {
        BookingDto bookingRequest = new BookingDto();
        bookingRequest.setItemId(1);
        assertThrows(ObjectNotFoundException.class, () -> service.addBooking(1, bookingRequest));
    }

    @Test
    void addBooking_whenSameTime() {
        User booker = new User();
        booker.setId(1);

        User owner = new User();
        owner.setId(2);

        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(true);

        BookingDto bookingRequest = new BookingDto(1,
                LocalDateTime.of(2021, 1, 1, 1, 1, 1),
                LocalDateTime.of(2021, 1, 1, 1, 1, 1),
                1);

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setItem(item);
        bookingResponse.setBooker(booker);
        bookingResponse.setStatus(Status.WAITING);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));


        assertThrows(ValidationException.class, () -> service.addBooking(1, bookingRequest));
    }

    @Test
    void addBooking_whenWrongTime() {
        User booker = new User();
        booker.setId(1);

        User owner = new User();
        owner.setId(2);

        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(true);

        BookingDto bookingRequest = new BookingDto(1,
                LocalDateTime.of(2022, 1, 1, 1, 1, 1),
                LocalDateTime.of(2021, 1, 1, 1, 1, 1),
                1);

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setItem(item);
        bookingResponse.setBooker(booker);
        bookingResponse.setStatus(Status.WAITING);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));


        assertThrows(ValidationException.class, () -> service.addBooking(1, bookingRequest));
    }

    @Test
    void addBooking_whenBookerIsOwner() {
        BookingDto bookingRequest = new BookingDto(1,
                LocalDateTime.of(2020, 1, 1, 1, 1, 1),
                LocalDateTime.of(2021, 1, 1, 1, 1, 1),
                1);

        User owner = new User();
        owner.setId(1);

        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(true);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        assertThrows(ObjectNotFoundException.class, () -> service.addBooking(1, bookingRequest));
    }

    @Test
    void addBooking_whenItemIsNotAvailable() {
        BookingDto bookingRequest = new BookingDto(1,
                LocalDateTime.of(2020, 1, 1, 1, 1, 1),
                LocalDateTime.of(2021, 1, 1, 1, 1, 1),
                1);

        User owner = new User();
        owner.setId(2);

        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(false);

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> service.addBooking(1, bookingRequest));
    }

    @Test
    void updateStatus_whenBookingIsApproved() {
        Booking booking = new Booking();

        User owner = new User();
        owner.setId(1);

        Item item = new Item();
        item.setOwner(owner);

        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setItem(item);
        bookingResponse.setStatus(Status.APPROVED);

        when(repository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(repository.save(booking)).thenReturn(booking);
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        assertEquals(bookingResponse, service.updateStatus(1, 1, "true"));
    }

    @Test
    void updateStatus_whenBookingIsNotApproved() {
        Booking booking = new Booking();

        User owner = new User();
        owner.setId(1);

        Item item = new Item();
        item.setOwner(owner);

        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setItem(item);
        bookingResponse.setStatus(Status.REJECTED);

        when(repository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(repository.save(booking)).thenReturn(booking);
        when(mapper.toBookingResponse(booking)).thenReturn(bookingResponse);

        assertEquals(bookingResponse, service.updateStatus(1, 1, "false"));
    }

    @Test
    void updateStatus_whenItemNotFound() {
        assertThrows(ObjectNotFoundException.class, () -> service.updateStatus(1, 1, "true"));
    }

    @Test
    void updateStatus_whenUserIsNotOwner() {
        Booking booking = new Booking();

        User owner = new User();
        owner.setId(1);

        Item item = new Item();
        item.setOwner(owner);

        booking.setItem(item);

        when(repository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(ObjectNotFoundException.class, () -> service.updateStatus(2, 1, "true"));
    }

    @Test
    void updateStatus_whenBookingIsAlreadyApproved() {
        Booking booking = new Booking();

        User owner = new User();
        owner.setId(1);

        Item item = new Item();
        item.setOwner(owner);

        booking.setItem(item);
        booking.setStatus(Status.APPROVED);

        when(repository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> service.updateStatus(1, 1, "true"));
    }
}