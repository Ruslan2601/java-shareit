package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestResponseTest {
    @Autowired
    private JacksonTester<ItemRequestResponse> json;
    private final User requester = new User(1, "asd@asd.ru", "Ruslan");
    private final ItemRequestResponse response = new ItemRequestResponse(1, "desc", requester, null, null);


    @Test
    @SneakyThrows
    void testBookingDto() {
        JsonContent<ItemRequestResponse> result = json.write(response);
        System.out.println(result);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathNumberValue("$.requester.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.requester.name").isEqualTo("Ruslan");
        assertThat(result).extractingJsonPathStringValue("$.requester.email").isEqualTo("asd@asd.ru");
        assertThat(result).extractingJsonPathStringValue("$.items").isEqualTo(null);

    }
}