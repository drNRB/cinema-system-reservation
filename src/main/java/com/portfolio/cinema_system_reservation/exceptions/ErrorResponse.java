package com.portfolio.cinema_system_reservation.exceptions;

import java.time.Instant;

public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
