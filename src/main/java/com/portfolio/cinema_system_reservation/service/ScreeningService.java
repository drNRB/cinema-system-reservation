package com.portfolio.cinema_system_reservation.service;

import com.portfolio.cinema_system_reservation.dto.CreateScreeningRequest;
import com.portfolio.cinema_system_reservation.dto.ScreeningDto;
import com.portfolio.cinema_system_reservation.dto.SeatStatusDto;
import com.portfolio.cinema_system_reservation.exceptions.ResourceNotFoundException;
import com.portfolio.cinema_system_reservation.model.Hall;
import com.portfolio.cinema_system_reservation.model.Movie;
import com.portfolio.cinema_system_reservation.model.Screening;
import com.portfolio.cinema_system_reservation.repository.HallRepository;
import com.portfolio.cinema_system_reservation.repository.ReservedSeatRepository;
import com.portfolio.cinema_system_reservation.repository.ScreeningRepository;
import com.portfolio.cinema_system_reservation.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieService movieService;
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;
    private final ReservedSeatRepository reservedSeatRepository;

    public ScreeningService(ScreeningRepository screeningRepository,
                            MovieService movieService,
                            HallRepository hallRepository,
                            SeatRepository seatRepository, ReservedSeatRepository reservedSeatRepository) {
        this.screeningRepository = screeningRepository;
        this.movieService = movieService;
        this.hallRepository = hallRepository;
        this.seatRepository = seatRepository;
        this.reservedSeatRepository = reservedSeatRepository;
    }

    @Transactional
    public ScreeningDto create(CreateScreeningRequest request) {
        Movie movie = movieService.getOrThrow(request.movieId());
        Hall hall = hallRepository.findById(request.hallId())
                .orElseThrow(() -> new ResourceNotFoundException("Hall not found: " + request.hallId()));

        LocalDateTime newStart = request.startTime();
        LocalDateTime newEnd = newStart.plusMinutes(movie.getDurationMinutes()).plusMinutes(15);

        List<Screening> dailyScreenings = screeningRepository.findByHall_IdAndStartTimeBetween(
                request.hallId(),
                newStart.minusHours(12),
                newStart.plusHours(12)
        );

        boolean hasOverlap = dailyScreenings.stream().anyMatch(existing -> {
            LocalDateTime existingStart = existing.getStartTime();
            LocalDateTime existingEnd = existingStart
                    .plusMinutes(existing.getMovie().getDurationMinutes())
                    .plusMinutes(15);

            return existingStart.isBefore(newEnd) && existingEnd.isAfter(newStart);
        });

        if (hasOverlap) {
            throw new IllegalArgumentException("Screening overlaps with an existing one.");
        }

        Screening saved = screeningRepository.save(new Screening(movie, hall, request.startTime()));
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ScreeningDto> listByHall(Long hallId) {
        return screeningRepository.findByHall_IdOrderByStartTimeAsc(hallId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScreeningDto> listByMovie(Long movieId) {
        return screeningRepository.findByMovie_IdOrderByStartTimeAsc(movieId).stream()
                .map(this::toDto)
                .toList();
    }

    public List<SeatStatusDto> getSeatStatus(Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new ResourceNotFoundException("Screening not found: " + screeningId));

        Long hallId = screening.getHall().getId();

        var allSeatsInHall = seatRepository.findByHall_IdOrderByRowAscNumberAsc(hallId);

        Set<Long> reservedSeatIds = reservedSeatRepository.findByScreening_Id(screeningId)
                .stream()
                .map(reservedSeat -> reservedSeat.getSeat().getId())
                .collect(Collectors.toSet());

        return allSeatsInHall.stream()
                .map(seat -> new SeatStatusDto(
                        seat.getId(),
                        seat.getRow(),
                        seat.getNumber(),
                        reservedSeatIds.contains(seat.getId())
                ))
                .toList();
    }

    private ScreeningDto toDto(Screening saved) {
        return new ScreeningDto(
                saved.getId(),
                saved.getMovie().getId(),
                saved.getMovie().getTitle(),
                saved.getMovie().getDurationMinutes(),
                saved.getHall().getId(),
                saved.getHall().getName(),
                saved.getStartTime()
        );
    }
}
