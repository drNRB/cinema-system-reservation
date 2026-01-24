package com.portfolio.cinema_system_reservation.repository;

import com.portfolio.cinema_system_reservation.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByHall_IdOrderByRowAscNumberAsc(Long hallId);

    boolean existsByHall_IdAndRowAndNumber(Long hallId, int row, int number);

}
