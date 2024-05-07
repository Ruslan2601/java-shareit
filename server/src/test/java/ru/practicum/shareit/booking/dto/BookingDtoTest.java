package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    private final BookingDto bookingRequest = new BookingDto(1,
            LocalDateTime.of(2020, 1, 1, 1, 1, 1),
            LocalDateTime.of(2021, 1, 1, 1, 1, 1),
            1);

    @Test
    @SneakyThrows
    void testBookingDto() {
        JsonContent<BookingDto> result = json.write(bookingRequest);
        System.out.println(result);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2020-01-01T01:01:01");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2021-01-01T01:01:01");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}