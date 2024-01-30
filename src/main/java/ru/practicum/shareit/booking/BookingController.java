package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.State;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                      @Valid @RequestBody BookingDto bookingDto, BindingResult bindingResult) {
        log.info("Получен POST запрос на добавление нового бронирования");
        return ResponseEntity.ok(bookingService.addBooking(userId, bookingDto, bindingResult));
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<BookingResponse> updateStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                        @PathVariable("bookingId") Integer bookingId,
                                                        @RequestParam String approved) {
        log.info("Получен PATCH запрос на подтверждение бронирования");
        return ResponseEntity.ok(bookingService.updateStatus(userId, bookingId, approved));
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<BookingResponse> getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                      @PathVariable("bookingId") Integer bookingId) {
        log.info("Получен GET запрос на получение данных о конкретном бронировании");
        return ResponseEntity.ok(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getBookingList(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                                @RequestParam(defaultValue = "ALL") State state) {


        log.info("Получен GET запрос на получение данных о бронировании текущего пользователя");
        return ResponseEntity.ok(bookingService.getBookingList(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponse>> getBookingListByItemOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                                           @RequestParam(defaultValue = "ALL") State state) {
        log.info("Получен GET запрос на получение списка бронирований для всех вещей текущего пользователя");
        return ResponseEntity.ok(bookingService.getBookingListByItemOwner(userId, state));
    }
}
