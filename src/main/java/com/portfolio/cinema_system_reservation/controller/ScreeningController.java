package com.portfolio.cinema_system_reservation.controller;

import com.portfolio.cinema_system_reservation.dto.CreateScreeningRequest;
import com.portfolio.cinema_system_reservation.dto.ScreeningDto;
import com.portfolio.cinema_system_reservation.service.ScreeningService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screenings")
public class ScreeningController {
    private final ScreeningService screeningService;

    public ScreeningController(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @PostMapping
    public ScreeningDto create(@RequestBody @Valid CreateScreeningRequest request) {
        return screeningService.create(request);
    }

    @GetMapping("/by-hall/{hallId}")
    public List<ScreeningDto> listByHall (@PathVariable("hallId") Long hallId) {
        return screeningService.listByHall(hallId);
    }

    @GetMapping("/by-movie/{movieId}")
    public List<ScreeningDto> listByMovie (@PathVariable("movieId") Long movieId) {
        return screeningService.listByMovie(movieId);
    }
}
