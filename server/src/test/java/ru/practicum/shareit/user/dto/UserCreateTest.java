package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserCreateTest {

    @Autowired
    private JacksonTester<UserCreate> json;

    @Test
    @SneakyThrows
    void testBookingDto() {
        UserCreate userCreate = new UserCreate();
        userCreate.setEmail("mai@sdf.ru");
        userCreate.setId(1);
        userCreate.setName("Ruslan");
        JsonContent<UserCreate> result = json.write(userCreate);
        System.out.println(result);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Ruslan");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("mai@sdf.ru");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);

    }
}