package com.portfolio.cinema_system_reservation.repository;

import com.portfolio.cinema_system_reservation.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    boolean existsByTitleIgnoreCase(String title);
}
