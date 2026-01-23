package com.portfolio.cinema_system_reservation.controller;

import com.portfolio.cinema_system_reservation.dto.SeatDto;
import com.portfolio.cinema_system_reservation.repository.SeatRepository;
import com.portfolio.cinema_system_reservation.service.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/halls")
public class SeatController {
    private final SeatService seatService;
    private final SeatRepository seatRepository;

    public SeatController(SeatService seatService, SeatRepository seatRepository) {
        this.seatService = seatService;
        this.seatRepository = seatRepository;
    }

    @PostMapping("/{hallId}/seats/generate")
    public ResponseEntity<String> generate(
            @PathVariable Long hallId,
            @RequestParam int rows,
            @RequestParam int seatsPerRow
    ) {
        int created = seatService.generateSeats(hallId, rows, seatsPerRow);

        return ResponseEntity.ok("Created seats: " + created);
    }

    @GetMapping("/{hallId}/seats")
    public List<SeatDto> list(@PathVariable Long hallId) {
        return seatRepository.findByHall_IdOrderByRowAscNumberAsc(hallId)
                .stream()
                .map(seat -> new SeatDto(seat.getId(), seat.getRow(), seat.getNumber()))
                .toList();
    }
}
