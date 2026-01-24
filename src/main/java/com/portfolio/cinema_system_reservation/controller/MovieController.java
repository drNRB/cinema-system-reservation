package com.portfolio.cinema_system_reservation.controller;

import com.portfolio.cinema_system_reservation.dto.CreateMovieRequest;
import com.portfolio.cinema_system_reservation.dto.MovieDto;
import com.portfolio.cinema_system_reservation.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public MovieDto create(@RequestBody @Valid CreateMovieRequest request) {
        return movieService.create(request);
    }

    @GetMapping
    public List<MovieDto> list() {
        return movieService.list();
    }
}
