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
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService service;

    @Test
    @SneakyThrows
    void addBooking_Ok() {
        int userId = 1;
        BookingDto bookingRequest = new BookingDto();
        bookingRequest.setStart(LocalDateTime.of(3000, 1, 1, 1, 1, 1));
        bookingRequest.setEnd(LocalDateTime.of(3001, 1, 1, 1, 1, 1));
        bookingRequest.setItemId(1);

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setEnd(LocalDateTime.of(3001, 1, 1, 1, 1, 1));
        bookingResponse.setStart(LocalDateTime.of(3000, 1, 1, 1, 1, 1));
        bookingResponse.setId(1);
        bookingResponse.setItem(new Item());

        when(service.addBooking(userId, bookingRequest, null)).thenReturn(bookingResponse);

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
    public void addBooking_Ok2(){
        int userId = 1;
        BookingDto bookingRequest = new BookingDto();
        bookingRequest.setStart(LocalDateTime.of(3000, 1, 1, 1, 1, 1));
        bookingRequest.setEnd(LocalDateTime.of(3001, 1, 1, 1, 1, 1));
        bookingRequest.setItemId(1);

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setEnd(LocalDateTime.of(3001, 1, 1, 1, 1, 1));
        bookingResponse.setStart(LocalDateTime.of(3000, 1, 1, 1, 1, 1));
        bookingResponse.setId(1);
        bookingResponse.setItem(new Item());

        when(service.addBooking(userId, bookingRequest, null)).thenReturn(bookingResponse);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest))
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    @SneakyThrows
    public void addBooking_Ok3(){
        int userId = 1;
        BookingDto bookingRequest = new BookingDto();
        bookingRequest.setStart(LocalDateTime.of(3000, 1, 1, 1, 1, 1));
        bookingRequest.setEnd(LocalDateTime.of(3001, 1, 1, 1, 1, 1));
        bookingRequest.setItemId(1);

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setEnd(LocalDateTime.of(3001, 1, 1, 1, 1, 1));
        bookingResponse.setStart(LocalDateTime.of(3000, 1, 1, 1, 1, 1));
        bookingResponse.setId(1);
        bookingResponse.setItem(new Item());

        when(service.addBooking(userId, bookingRequest, null)).thenReturn(bookingResponse);

        String response = mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", userId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(response, objectMapper.writeValueAsString(bookingResponse));
    }


//    @Test
//    @SneakyThrows
//    void getBoowking_Owk() {
//        int userId = 1;
//        int bookingId = 1;
//
//        BookingResponse bookingResponse = new BookingResponse();
//
//        when(service.getBooking(userId, bookingId)).thenReturn(bookingResponse);
//
//        String response = mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", bookingId)
//                .header("X-Sharer-User-Id", userId))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        assertEquals(response, objectMapper.writeValueAsString(bookingResponse));
//    }

//    @Test
//    @SneakyThrows
//    void getBookingListByItemOwner_Ok() {
//        int userId = 1;
//        String state = "ALL";
//        int from = 0;
//        int size = 1;
//
//        List<BookingResponse> bookingResponses = List.of();
//
//        when(service.getBookingListByItemOwner(userId, state, from, size)).thenReturn(bookingResponses);
//
//        String response = mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
//                .header("X-Sharer-User-Id", userId)
//                .param("from", from + "")
//                .param("size", size + ""))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        assertEquals(response, objectMapper.writeValueAsString(bookingResponses));
//    }
//
//    @Test
//    @SneakyThrows
//    void getBookingListByItemOwner_NotValid() {
//        int userId = -1;
//        String state = "ALL";
//        int from = -1;
//        int size = -1;
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
//                .header("X-Sharer-User-Id", userId)
//                .param("from", from + "")
//                .param("size", size + ""))
//                .andExpect(status().isBadRequest());
//
//        verify(service, never()).getBookingListByItemOwner(userId, state, from, size);
//    }
//
//    @Test
//    @SneakyThrows
//    void getBookingList_Ok() {
//        int userId = 1;
//        String state = "ALL";
//        int from = 0;
//        int size = 1;
//
//        List<BookingResponse> bookingResponses = List.of();
//
//        when(service.getBookingList(userId, state, from, size)).thenReturn(bookingResponses);
//
//        String response = mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
//                .header("X-Sharer-User-Id", userId)
//                .param("from", from + "")
//                .param("size", size + ""))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        assertEquals(response, objectMapper.writeValueAsString(bookingResponses));
//    }
//
//    @Test
//    @SneakyThrows
//    void getBookingList_NotValid() {
//        int userId = -1;
//        String state = "ALL";
//        int from = -1;
//        int size = -1;
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
//                .header("X-Sharer-User-Id", userId)
//                .param("from", from + "")
//                .param("size", size + ""))
//                .andExpect(status().isBadRequest());
//
//        verify(service, never()).getBookingList(userId, state, from, size);
//    }

//    @Test
//    @SneakyThrows
//    void addBooking_Ok() {
//        int userId = 1;
//        BookingDto bookingRequest = new BookingDto();
//        bookingRequest.setStart(LocalDateTime.of(3000, 1, 1, 1, 1, 1));
//        bookingRequest.setEnd(LocalDateTime.of(3001, 1, 1, 1, 1, 1));
//        bookingRequest.setItemId(1);
//
//        BookingResponse bookingResponse = new BookingResponse();
//
//
//    }
}