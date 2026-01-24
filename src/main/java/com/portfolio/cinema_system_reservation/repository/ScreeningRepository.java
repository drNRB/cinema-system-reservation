package com.portfolio.cinema_system_reservation.repository;

import com.portfolio.cinema_system_reservation.model.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {
    boolean existsByHall_IdAndStartTime (Long hallId, LocalDateTime startTime);

    List<Screening> findByHall_IdOrderByStartTimeAsc(Long hallId);
    List<Screening> findByMovie_IdOrderByStartTimeAsc (Long movieId);
}
