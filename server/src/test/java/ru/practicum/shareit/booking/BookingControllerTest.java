package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService service;

    private final BookingDto bookingRequest = new BookingDto(1,
            LocalDateTime.of(3000, 1, 1, 1, 1, 1),
            LocalDateTime.of(3001, 1, 1, 1, 1, 1),
            1);
    private final BookingResponse bookingResponse = new BookingResponse(1,
            LocalDateTime.of(3000, 1, 1, 1, 1, 1),
            LocalDateTime.of(3001, 1, 1, 1, 1, 1),
            new Item(), new User(), Status.APPROVED);


    @Test
    @SneakyThrows
    void addBooking_shouldAddBooking() {

        when(service.addBooking(1, bookingRequest)).thenReturn(bookingResponse);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest))
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.start",
                        is(bookingResponse.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(bookingResponse.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @SneakyThrows
    void addBooking_WrongId() {

        when(service.addBooking(1, bookingRequest)).thenThrow(new ObjectNotFoundException("User с заданным id = 1 является владельцем"));

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest))
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("User с заданным id = 1 является владельцем"));
    }

    @Test
    @SneakyThrows
    void getBooking_shouldReturnBookingById() {
        when(service.getBooking(any(Integer.class), any(Integer.class)))
                .thenReturn(bookingResponse);

        mockMvc.perform(get("/bookings/1")
                .content(objectMapper.writeValueAsString(bookingRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.start",
                        is(bookingResponse.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(bookingResponse.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @SneakyThrows
    void getBookingList_shouldReturnListOfBookings() {
        when(service.getBookingList(any(Integer.class), any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(bookingResponse));

        mockMvc.perform(get("/bookings")
                .content(objectMapper.writeValueAsString(bookingRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.[0].id", is(bookingResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].start",
                        is(bookingResponse.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end",
                        is(bookingResponse.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

    }

    @Test
    @SneakyThrows
    void getBookingListByItemOwner_shouldReturnListOfBookings() {
        when(service.getBookingListByItemOwner(any(Integer.class), any(String.class),
                any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(bookingResponse));

        mockMvc.perform(get("/bookings/owner?from=0&size=10")
                .content(objectMapper.writeValueAsString(bookingRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].start", is(bookingResponse.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(bookingResponse.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    @SneakyThrows
    void updateStatus_shouldUpdateBooking() {
        when(service.updateStatus(any(Integer.class), any(Integer.class), any(String.class)))
                .thenReturn(bookingResponse);

        mockMvc.perform(patch("/bookings/1")
                .content(objectMapper.writeValueAsString(bookingRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .queryParam("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingResponse.getStatus().toString()), String.class))
                .andExpect(jsonPath("$.start",
                        is(bookingResponse.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(bookingResponse.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}