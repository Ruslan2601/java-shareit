package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseTest {
    @Autowired
    private JacksonTester<BookingResponse> json;

    private final BookingResponse bookingResponse = new BookingResponse(1,
            LocalDateTime.of(2020, 1, 1, 1, 1, 1),
            LocalDateTime.of(2021, 1, 1, 1, 1, 1),
            new Item(1,"plane", "big plane", true, new User(), new ItemRequest()),
            new User(1, "asd@asd.ru", "Ruslan"), Status.APPROVED);

    @Test
    @SneakyThrows
    void testBookingDto() {
        JsonContent<BookingResponse> result = json.write(bookingResponse);
        System.out.println(result);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2020-01-01T01:01:01");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2021-01-01T01:01:01");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("plane");

        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("big plane");

        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Ruslan");

        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("asd@asd.ru");

    }
}