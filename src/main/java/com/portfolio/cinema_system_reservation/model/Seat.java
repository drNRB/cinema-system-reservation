package com.portfolio.cinema_system_reservation.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "seats",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_hall_row_number",
                columnNames = {"hall_id", "seat_row", "seat_number"}
        )
)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(name = "seat_row", nullable = false)
    private int row;

    @Column(name = "seat_number", nullable = false)
    private int number;

    protected Seat() {
    }

    public Seat(Hall hall, int row, int number) {
        this.hall = hall;
        this.row = row;
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public Hall getHall() {
        return hall;
    }

    public int getRow() {
        return row;
    }

    public int getNumber() {
        return number;
    }
}
