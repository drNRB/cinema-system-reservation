package com.portfolio.cinema_system_reservation.dto;

public record SeatStatusDto(
        Long seatId,
        int row,
        int number,
        boolean isReserved
) {
}
