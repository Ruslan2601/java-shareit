package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.Validation;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @Valid @RequestBody BookingDto bookingDto, BindingResult bindingResult) {
        log.info("Получен POST запрос на добавление нового бронирования");
        Validation.validation(bindingResult);
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public BookingResponse updateStatus(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                        @PathVariable("bookingId") Integer bookingId,
                                        @RequestParam String approved) {
        log.info("Получен PATCH запрос на подтверждение бронирования");
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingResponse getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @PathVariable("bookingId") Integer bookingId) {
        log.info("Получен GET запрос на получение данных о конкретном бронировании");
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponse> getBookingList(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(defaultValue = "0")
                                                @Min(value = 0, message = "from должно быть больше или равно 0") int from,
                                                @RequestParam(defaultValue = "10")
                                                @Min(value = 1, message = "size должно быть больше 0") int size) {
        log.info("Получен GET запрос на получение данных о бронировании текущего пользователя");
        return bookingService.getBookingList(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getBookingListByItemOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                           @RequestParam(defaultValue = "ALL") String state,
                                                           @RequestParam(defaultValue = "0")
                                                           @Min(value = 0, message = "from должно быть больше или равно 0") int from,
                                                           @RequestParam(defaultValue = "10")
                                                           @Min(value = 1, message = "size должно быть больше 0") int size) {
        log.info("Получен GET запрос на получение списка бронирований для всех вещей текущего пользователя");
        return bookingService.getBookingListByItemOwner(userId, state, from, size);
    }
}
