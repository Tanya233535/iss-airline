package com.example.issairline.api;

import java.time.LocalDateTime;

public record ApiError(
        String message,
        int status,
        String timestamp
) {
    public static ApiError of(String message, int status) {
        return new ApiError(message, status, LocalDateTime.now().toString());
    }
}
