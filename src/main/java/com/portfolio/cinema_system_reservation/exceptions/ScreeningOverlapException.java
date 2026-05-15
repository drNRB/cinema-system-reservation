package com.portfolio.cinema_system_reservation.exceptions;

public class ScreeningOverlapException extends RuntimeException {
    public ScreeningOverlapException(String message) {
        super(message);
    }
}
