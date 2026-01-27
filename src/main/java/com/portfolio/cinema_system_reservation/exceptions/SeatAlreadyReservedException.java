package com.portfolio.cinema_system_reservation.exceptions;

public class SeatAlreadyReservedException extends RuntimeException{

    public SeatAlreadyReservedException(String message) {
        super(message);
    }
}
