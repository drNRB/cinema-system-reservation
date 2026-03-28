package com.portfolio.cinema_system_reservation.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "screenings",
        uniqueConstraints = @UniqueConstraint(name = "uk_hall_start_time", columnNames = {"hall_id", "start_time"})
)
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    protected Screening() {

    }

    public Screening(Movie movie, Hall hall, LocalDateTime startTime) {
        this.movie = movie;
        this.hall = hall;
        this.startTime = startTime;
    }

    public Long getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Hall getHall() {
        return hall;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
