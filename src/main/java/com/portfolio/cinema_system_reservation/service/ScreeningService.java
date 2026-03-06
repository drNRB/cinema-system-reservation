package com.portfolio.cinema_system_reservation.service;

import com.portfolio.cinema_system_reservation.dto.CreateScreeningRequest;
import com.portfolio.cinema_system_reservation.dto.ScreeningDto;
import com.portfolio.cinema_system_reservation.exceptions.ResourceNotFoundException;
import com.portfolio.cinema_system_reservation.model.Hall;
import com.portfolio.cinema_system_reservation.model.Movie;
import com.portfolio.cinema_system_reservation.model.Screening;
import com.portfolio.cinema_system_reservation.repository.HallRepository;
import com.portfolio.cinema_system_reservation.repository.ScreeningRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieService movieService;
    private final HallRepository hallRepository;

    public ScreeningService(ScreeningRepository screeningRepository, MovieService movieService, HallRepository hallRepository) {
        this.screeningRepository = screeningRepository;
        this.movieService = movieService;
        this.hallRepository = hallRepository;
    }

    @Transactional
    public ScreeningDto create(CreateScreeningRequest request) {
        Movie movie = movieService.getOrThrow(request.movieId());
        Hall hall = hallRepository.findById(request.hallId())
                .orElseThrow(() -> new ResourceNotFoundException("Hall not found: " + request.hallId()));

        if (screeningRepository.existsByHall_IdAndStartTime(request.hallId(), request.startTime())) {
            throw new IllegalArgumentException("Screening already exists for hall at that starting time.");
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
