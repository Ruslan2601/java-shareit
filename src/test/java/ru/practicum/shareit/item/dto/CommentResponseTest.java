package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentResponseTest {

    @Autowired
    private JacksonTester<CommentResponse> json;

    @Test
    @SneakyThrows
    void commentResponse() {
        CommentResponse comment = new CommentResponse(1, "text", "Ruslan",
                LocalDateTime.of(2020, 1, 1, 1, 1, 1));

        JsonContent<CommentResponse> jsonComment = json.write(comment);

        assertThat(jsonComment).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(jsonComment).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonComment).extractingJsonPathStringValue("$.created")
                .isEqualTo("2020-01-01T01:01:01");
        assertThat(jsonComment).extractingJsonPathStringValue("$.authorName").isEqualTo("Ruslan");
    }
}