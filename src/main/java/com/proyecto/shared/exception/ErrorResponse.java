package com.proyecto.shared.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String status,
        LocalDateTime timestamp
) {
}
