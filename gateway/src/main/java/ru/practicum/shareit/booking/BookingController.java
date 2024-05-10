package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.Validation;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookingList(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                 @RequestParam(name = "state", defaultValue = "ALL") String stateString,
                                                 @RequestParam(defaultValue = "0")
                                                 @Min(value = 0, message = "from должно быть больше или равно 0") int from,
                                                 @RequestParam(defaultValue = "10")
                                                 @Min(value = 1, message = "size должно быть больше 0") int size) {
        log.info("Получен GET запрос на получение данных о бронировании текущего пользователя");
        BookingState state = BookingState.from(stateString)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateString));
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable("bookingId") Long bookingId) {
        log.info("Получен GET запрос на получение данных о конкретном бронировании");
        return bookingClient.getBooking(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @Valid @RequestBody BookingDto bookingDto, BindingResult bindingResult) {
        log.info("Получен POST запрос на добавление нового бронирования");
        Validation.validation(bindingResult);
        return bookingClient.bookItem(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @PathVariable("bookingId") Integer bookingId,
                                               @RequestParam("approved") Boolean isApprove) {
        log.info("Получен PATCH запрос на подтверждение бронирования");
        return bookingClient.approveBooking(userId, bookingId, isApprove);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingListByItemOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                            @RequestParam(name = "state", defaultValue = "ALL") String stateString,
                                                            @RequestParam(defaultValue = "0")
                                                            @Min(value = 0, message = "from должно быть больше или равно 0") int from,
                                                            @RequestParam(defaultValue = "10")
                                                            @Min(value = 1, message = "size должно быть больше 0") int size) {
        log.info("Получен GET запрос на получение списка бронирований для всех вещей текущего пользователя");
        BookingState state = BookingState.from(stateString)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateString));
        return bookingClient.getOwnerBookings(userId, state, from, size);
    }
}
