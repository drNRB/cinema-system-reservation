package com.portfolio.cinema_system_reservation.dto;

import java.time.LocalDateTime;

public record ScreeningDto(
        Long id, Long movieId, String movieTitle,
        int movieDurationMinutes, Long hallId,
        String hallName, LocalDateTime startTime) {
}
