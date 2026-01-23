package com.portfolio.cinema_system_reservation.service;

import com.portfolio.cinema_system_reservation.model.Hall;
import com.portfolio.cinema_system_reservation.model.Seat;
import com.portfolio.cinema_system_reservation.repository.HallRepository;
import com.portfolio.cinema_system_reservation.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new IllegalArgumentException("Rows and seatsPerRow must be greather than 0");
        }

        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new IllegalArgumentException("Hall not found: " + hallId));

        int created = 0;

        for (int row = 1; row <= rows; row++) {
            for(int seat = 1; seat <= seatsPerRow; seat++) {
                if(!seatRepository.existsByHall_IdAndRowAndNumber(hallId,row,seat)) {
                    seatRepository.save(new Seat(hall,row,seat));
                    created++;
                }
            }
        }
        return created;
    }
}
