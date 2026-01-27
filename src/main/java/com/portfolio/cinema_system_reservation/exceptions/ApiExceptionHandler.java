package com.portfolio.cinema_system_reservation.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(SeatAlreadyReservedException.class)
    public ResponseEntity<Map<String, Object>> handleSeatAlreadyReserved(SeatAlreadyReservedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", ex.getMessage()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException exception) {
        HttpStatus status = exception.getMessage() != null && exception.getMessage().startsWith("Seat already reserved")
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", exception.getMessage()
        ));
    }


}
