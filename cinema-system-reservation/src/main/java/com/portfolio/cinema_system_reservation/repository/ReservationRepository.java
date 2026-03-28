package com.portfolio.cinema_system_reservation.repository;


import com.portfolio.cinema_system_reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByScreening_IdOrderByCreatedAtAsc(Long screeningId);
}
