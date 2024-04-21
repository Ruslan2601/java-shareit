package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;


@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService service;
    @InjectMocks
    private BookingController controller;


    @Test
    void addBooking() {
        int userId = 1;
        BookingDto bookingRequest = new BookingDto();
        BookingResponse bookingResponse = new BookingResponse();

        when(service.addBooking(userId, bookingRequest, null)).thenReturn(bookingResponse);

        assertEquals(bookingResponse, controller.addBooking(userId, bookingRequest, null));
    }

    @Test
    void updateStatus() {
        int userId = 1;
        int bookingId = 1;
        String approved = "approved";
        BookingResponse bookingResponse = new BookingResponse();

        when(service.updateStatus(userId, bookingId, approved)).thenReturn(bookingResponse);

        assertEquals(bookingResponse, controller.updateStatus(userId, bookingId, approved));
    }

    @Test
    void getBooking() {
        BookingResponse bookingResponse = new BookingResponse();

        when(service.getBooking(anyInt(), anyInt())).thenReturn(bookingResponse);

        assertEquals(bookingResponse, controller.getBooking(1, 1));
    }

    @Test
    void getBookingList() {
        List<BookingResponse> bookingResponse = List.of();


        when(service.getBookingList(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(bookingResponse);

        assertEquals(bookingResponse, controller.getBookingList(1, "ALL", 0, 1));
    }

    @Test
    void getBookingListByItemOwner() {
        List<BookingResponse> bookingResponse = List.of();

        when(service.getBookingListByItemOwner(anyInt(), anyString(), anyInt(), anyInt())).thenReturn(bookingResponse);

        assertEquals(bookingResponse, controller.getBookingListByItemOwner(1, "ALL", 0, 1));
    }
}