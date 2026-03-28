package com.portfolio.cinema_system_reservation.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Column(name = "customer_name", length = 120)
    private String customerName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservedSeat> reservedSeat = new ArrayList<>();

    protected Reservation() {

    }

    public Reservation(Screening screening, String customerName) {
        this.screening = screening;
        this.customerName = customerName;
    }

    @PrePersist
    void prePersist() {
        if(createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void addSeat(Seat seat) {
        ReservedSeat rs = new ReservedSeat(this,this.screening, seat);
        reservedSeat.add(rs);
    }

    public Long getId() {
        return id;
    }

    public Screening getScreening() {
        return screening;
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ReservedSeat> getReservedSeat() {
        return reservedSeat;
    }
}
