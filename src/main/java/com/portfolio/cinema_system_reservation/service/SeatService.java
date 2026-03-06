package com.portfolio.cinema_system_reservation.service;

import com.portfolio.cinema_system_reservation.exceptions.ResourceNotFoundException;
import com.portfolio.cinema_system_reservation.model.Hall;
import com.portfolio.cinema_system_reservation.model.Seat;
import com.portfolio.cinema_system_reservation.repository.HallRepository;
import com.portfolio.cinema_system_reservation.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeatService {
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;

    public SeatService(HallRepository hallRepository, SeatRepository seatRepository) {
        this.hallRepository = hallRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public int generateSeats(Long hallId, int rows, int seatsPerRow) {
        if (rows <= 0 || seatsPerRow <= 0) {
            throw new IllegalArgumentException("Rows and seatsPerRow must be greater than 0");
        }

        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new ResourceNotFoundException("Hall not found: " + hallId));

        List<Seat> existingSeats = seatRepository.findByHall_IdOrderByRowAscNumberAsc(hallId);

        Set<String> existingSeatSet = existingSeats.stream()
                .map(seat -> seat.getRow() + "-" + seat.getNumber())
                .collect(Collectors.toSet());

        List<Seat> seatsToSave = new ArrayList<>();

        for (int row = 1; row <= rows; row++) {
            for (int seatNumber = 1; seatNumber <= seatsPerRow; seatNumber++) {
                String seatKey = row + "-" + seatNumber;

                if (!existingSeatSet.contains(seatKey)) {
                    seatsToSave.add(new Seat(hall, row, seatNumber));
                }
            }
        }

        if (!seatsToSave.isEmpty()) {
            seatRepository.saveAll(seatsToSave);
        }

        return seatsToSave.size();
    }
}