package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    @SneakyThrows
    void commentDto() {
        CommentDto comment = new CommentDto();
        comment.setText("text");

        JsonContent<CommentDto> jsonComment = json.write(comment);

        assertThat(jsonComment).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }
}