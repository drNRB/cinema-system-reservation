package com.portfolio.cinema_system_reservation.repository;

import com.portfolio.cinema_system_reservation.model.ReservedSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ReservedSeatRepository extends JpaRepository<ReservedSeat, Long> {
    boolean existsByScreening_IdAndSeat_Id(Long screeningId, Long seatId);

    List<ReservedSeat> findByScreening_Id(Long screeningId);

    boolean existsByScreening_IdAndSeat_IdIn(Long screeningId, Set<Long> seatIds);
}
