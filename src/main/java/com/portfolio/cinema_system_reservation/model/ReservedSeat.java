package com.portfolio.cinema_system_reservation.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "reserved_seats",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reserved_seat_screening_seat",
                columnNames = {"screening_id", "seat_id"}
        ),
        indexes = {
                @Index(name = "idx_reserved_seats_screening", columnList = "screening_id"),
                @Index(name = "idx_reserved_seats_seat", columnList = "seat_id")
        }
)
public class ReservedSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    protected ReservedSeat() {

    }

    public ReservedSeat(Reservation reservation, Screening screening, Seat seat) {
        this.reservation = reservation;
        this.screening = screening;
        this.seat = seat;
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Screening getScreening() {
        return screening;
    }

    public Seat getSeat() {
        return seat;
    }
}
