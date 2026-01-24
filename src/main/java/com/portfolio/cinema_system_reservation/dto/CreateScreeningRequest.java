package com.portfolio.cinema_system_reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateScreeningRequest(@NotNull Long movieId, @NotNull Long hallId, @NotNull LocalDateTime startTime) {
}
