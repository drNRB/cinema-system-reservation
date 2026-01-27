package com.portfolio.cinema_system_reservation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateReservationRequest(@NotNull Long screeningId, @NotEmpty List<Long> seatIds, String customerName) {
}
