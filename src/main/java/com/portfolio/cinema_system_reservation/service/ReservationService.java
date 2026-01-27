package com.portfolio.cinema_system_reservation.service;

import com.portfolio.cinema_system_reservation.dto.CreateReservationRequest;
import com.portfolio.cinema_system_reservation.dto.ReservationDto;
import com.portfolio.cinema_system_reservation.dto.ReservedSeatDto;
import com.portfolio.cinema_system_reservation.exceptions.SeatAlreadyReservedException;
import com.portfolio.cinema_system_reservation.model.Reservation;
import com.portfolio.cinema_system_reservation.model.Screening;
import com.portfolio.cinema_system_reservation.model.Seat;
import com.portfolio.cinema_system_reservation.repository.ReservationRepository;
import com.portfolio.cinema_system_reservation.repository.ReservedSeatRepository;
import com.portfolio.cinema_system_reservation.repository.ScreeningRepository;
import com.portfolio.cinema_system_reservation.repository.SeatRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservedSeatRepository reservedSeatRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;

    public ReservationService(
            ReservationRepository reservationRepository, ReservedSeatRepository reservedSeatRepository,
            ScreeningRepository screeningRepository, SeatRepository seatRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservedSeatRepository = reservedSeatRepository;
        this.screeningRepository = screeningRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public ReservationDto create(CreateReservationRequest request) {
        Screening screening = screeningRepository.findById(request.screeningId())
                .orElseThrow(() -> new IllegalArgumentException("Screening not found: " + request.screeningId()));

        Long hallId = screening.getHall().getId();

        List<Long> seatIds = request.seatIds();
        if(seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("seatIds must not be empty");
        }

        Set<Long> uniqueSeatIds = new HashSet<>(seatIds);

        List<Seat> seats = seatRepository.findAllById(uniqueSeatIds);
        if (seats.size() != uniqueSeatIds.size()) {
            throw new IllegalArgumentException("Some seats do not exist");
        }

        for (Seat seat : seats) {
            Long seatHallId = seat.getHall().getId();
            if (!hallId.equals(seatHallId)) {
                throw new IllegalArgumentException("Seat " + seat.getId() + " does not belong to hall " + hallId);
            }

            if(reservedSeatRepository.existsByScreening_IdAndSeat_Id(screening.getId(), seat.getId())) {
                throw new SeatAlreadyReservedException("Seat already reserved: " + seat.getId());
            }
        }

        Reservation reservation = new Reservation(screening, request.customerName());
        seats.forEach(reservation::addSeat);

        try {
            Reservation saved = reservationRepository.saveAndFlush(reservation);
            return toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new SeatAlreadyReservedException("One or more seats are already reserved");
        }
    }

    @Transactional(readOnly = true)
    public ReservationDto get(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
        return toDto(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> listByScreening(Long screeningId) {
        return reservationRepository.findByScreening_IdOrderByCreatedAtAsc(screeningId).stream()
                .map(this::toDto)
                .toList();
    }


    private ReservationDto toDto(Reservation reservation) {
        Screening s = reservation.getScreening();

        return new ReservationDto(
                reservation.getId(),
                s.getId(),
                s.getMovie().getTitle(),
                s.getHall().getId(),
                s.getStartTime(),
                reservation.getCustomerName(),
                reservation.getCreatedAt(),
                reservation.getReservedSeat().stream()
                        .map(rs -> new ReservedSeatDto(
                                rs.getSeat().getId(),
                                rs.getSeat().getRow(),
                                rs.getSeat().getNumber()
                        ))
                        .toList()
        );
    }


}
