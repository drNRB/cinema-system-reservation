package com.portfolio.cinema_system_reservation.repository;

import com.portfolio.cinema_system_reservation.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HallRepository  extends JpaRepository<Hall, Long> {
    boolean existsByName(String name);
}
