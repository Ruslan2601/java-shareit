package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    private String error;
    private String timestamp;

    public ErrorResponse(String error, String timestamp) {
        this.error = error;
        this.timestamp = timestamp;
    }
}
