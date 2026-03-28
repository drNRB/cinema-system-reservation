package com.portfolio.cinema_system_reservation.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationDto(
        Long id,
        Long screeningId,
        String movieTitle,
        Long hallId,
        LocalDateTime startTime,
        String customerName,
        LocalDateTime createdAt,
        List<ReservedSeatDto> seats
) {
}
