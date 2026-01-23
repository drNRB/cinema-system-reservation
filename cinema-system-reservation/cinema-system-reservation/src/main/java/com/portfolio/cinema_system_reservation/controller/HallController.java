package com.portfolio.cinema_system_reservation.controller;

import com.portfolio.cinema_system_reservation.dto.CreateHallRequest;
import com.portfolio.cinema_system_reservation.model.Hall;
import com.portfolio.cinema_system_reservation.repository.HallRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/halls")
public class HallController {
    private final HallRepository hallRepository;

    public HallController(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    @PostMapping
    public ResponseEntity<Hall> create(@Valid @RequestBody CreateHallRequest request) {
        if(hallRepository.existsByName(request.name())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Hall saved = hallRepository.save(new Hall(request.name()));
        return ResponseEntity.created(URI.create("/api/halls" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<Hall> list() {
        return hallRepository.findAll();
    }
}
