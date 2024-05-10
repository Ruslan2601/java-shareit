package ru.practicum.shareit.request.dto;


import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestRequestTest {
    @Autowired
    private JacksonTester<ItemRequestRequest> json;

    @Test
    @SneakyThrows
    void testBookingDto() {
        ItemRequestRequest itemRequestRequest = new ItemRequestRequest();
        itemRequestRequest.setDescription("123");
        JsonContent<ItemRequestRequest> result = json.write(itemRequestRequest);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("123");

    }
}