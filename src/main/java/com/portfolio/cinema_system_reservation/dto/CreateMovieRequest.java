package com.portfolio.cinema_system_reservation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateMovieRequest(@NotBlank String title, @Min(1) @Max(400) int durationMinutes) {
}
