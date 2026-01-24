package com.portfolio.cinema_system_reservation.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateHallRequest(@NotBlank String name) {
}
