package com.portfolio.cinema_system_reservation.controller;

import com.portfolio.cinema_system_reservation.dto.CreateReservationRequest;
import com.portfolio.cinema_system_reservation.dto.ReservationDto;
import com.portfolio.cinema_system_reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ReservationDto create(@RequestBody @Valid CreateReservationRequest request) {
        return reservationService.create(request);
    }

    @GetMapping("/{id}")
    public ReservationDto get(@PathVariable Long id) {
        return reservationService.get(id);
    }

    @GetMapping("/by-screening/{screeningId}")
    public List<ReservationDto> listByScreening(@PathVariable Long screeningId) {
        return reservationService.listByScreening(screeningId);
    }
}
