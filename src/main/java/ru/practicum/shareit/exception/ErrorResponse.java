package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    private String message;
    private String timestamp;

    public ErrorResponse(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}
