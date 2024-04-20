package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingValidation;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper mapper;


    @Transactional
    public BookingResponse addBooking(Integer userId, BookingDto bookingDto, BindingResult bindingResult) {
        BookingValidation.validation(bindingResult);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new ObjectNotFoundException("Товара с такими id нет"));
        if (!item.getAvailable()) {
            throw new ValidationException("Товар недоступен для бронирования");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователя с такими id нет"));
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException("Время начала не может быть равно времени окончания заявки");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd() == bookingDto.getStart()) {
            throw new ValidationException("Ошибка валидации времени");
        }
        if (item.getOwner().getId() == userId) {
            throw new ObjectNotFoundException("User с заданным id = " + userId + " является владельцем");
        }

        Booking booking = mapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        log.info("Добавлена новая бронь товара");
        return mapper.toBookingResponse(booking);
    }

    @Transactional
    public BookingResponse updateStatus(Integer userId, Integer bookingId, String approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("Брони с такими id нет"));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new ObjectNotFoundException("Товар не приндалежит данному пользователю");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Бронь товара уже согласована");
        }
        if (approved.equals("true")) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        log.info("Обновлен статус бронирования");
        return mapper.toBookingResponse(booking);
    }

    public BookingResponse getBooking(Integer userId, Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("Брони с такими id нет"));
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new ObjectNotFoundException("Товар или бронь не приндалежит данному пользователю");
        }
        log.info("Бронирование отображено");
        return mapper.toBookingResponse(booking);
    }

    public List<BookingResponse> getBookingList(Integer userId, State state, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователя с такими id нет"));
        Pageable sortedByStart = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, sortedByStart);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), sortedByStart);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), sortedByStart);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), sortedByStart);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, sortedByStart);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, sortedByStart);
                break;
            default:
                throw new ValidationException(String.format("Unknown state: %s", state));
        }
        log.info("Список бронирований отображен");
        return bookingList.stream().map(mapper::toBookingResponse).collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingListByItemOwner(Integer userId, State state, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователя с такими id нет"));
        List<Booking> bookingList;
        Pageable sortedByStart = PageRequest.of(from / size, size, Sort.by("start").descending());
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByItemOwnerOrderByStartDesc(user, sortedByStart);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(user, LocalDateTime.now(), LocalDateTime.now(), sortedByStart);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now(), sortedByStart);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByItemOwnerAndStartAfterOrderByStartDesc(user, LocalDateTime.now(), sortedByStart);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(user, Status.WAITING, sortedByStart);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(user, Status.REJECTED, sortedByStart);
                break;
            default:
                throw new ValidationException(String.format("Unknown state: %s", state));
        }
        log.info("Список бронирований отображен");
        return bookingList.stream().map(mapper::toBookingResponse).collect(Collectors.toList());
    }
}
