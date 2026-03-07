package com.portfolio.cinema_system_reservation.service;

import com.portfolio.cinema_system_reservation.dto.CreateScreeningRequest;
import com.portfolio.cinema_system_reservation.dto.ScreeningDto;
import com.portfolio.cinema_system_reservation.model.Hall;
import com.portfolio.cinema_system_reservation.model.Movie;
import com.portfolio.cinema_system_reservation.model.Screening;
import com.portfolio.cinema_system_reservation.repository.HallRepository;
import com.portfolio.cinema_system_reservation.repository.ScreeningRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {

    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private MovieService movieService;
    @Mock
    private HallRepository hallRepository;

    @InjectMocks
    private ScreeningService screeningService;

    @Test
    void create_ShouldSaveScreening_WhenThereIsNoOverlap() {
        Long moveId = 1L;
        Long hallId = 1L;

        LocalDateTime startTime = LocalDateTime.of(2026, 3, 20, 15, 0);
        CreateScreeningRequest request = new CreateScreeningRequest(moveId, hallId, startTime);

        Movie movie = mock(Movie.class);
        when(movie.getId()).thenReturn(moveId);
        when(movie.getTitle()).thenReturn("Inception");
        when(movie.getDurationMinutes()).thenReturn(120);

        Hall hall = mock(Hall.class);
        when(hall.getId()).thenReturn(hallId);
        when(hall.getName()).thenReturn("VIP Hall");

        when(movieService.getOrThrow(moveId)).thenReturn(movie);
        when(hallRepository.findById(hallId)).thenReturn(Optional.of(hall));

        when(screeningRepository.findByHall_IdAndStartTimeBetween(eq(hallId), any(), any()))
                .thenReturn(List.of());

        Screening savedScreening = mock(Screening.class);
        when(savedScreening.getId()).thenReturn(100L);
        when(savedScreening.getMovie()).thenReturn(movie);
        when(savedScreening.getHall()).thenReturn(hall);
        when(savedScreening.getStartTime()).thenReturn(startTime);

        when(screeningRepository.save(any(Screening.class))).thenReturn(savedScreening);

        ScreeningDto result = screeningService.create(request);

        assertNotNull(result);
        assertEquals("Inception", result.movieTitle());
        verify(screeningRepository, times(1)).save(any(Screening.class));
    }

    @Test
    void create_ShouldThrowIllegalArgumentException_WhenScreeningsOverlap() {
        Long movieId = 1L;
        Long hallId = 1L;
        LocalDateTime newStartTime = LocalDateTime.of(2026, 3, 20, 15, 0);
        CreateScreeningRequest request = new CreateScreeningRequest(movieId, hallId, newStartTime);

        Movie movie = mock(Movie.class);
        when(movie.getDurationMinutes()).thenReturn(120);

        Hall hall = mock(Hall.class);

        when(movieService.getOrThrow(movieId)).thenReturn(movie);
        when(hallRepository.findById(hallId)).thenReturn(Optional.of(hall));

        Screening existingScreening = mock(Screening.class);
        when(existingScreening.getStartTime()).thenReturn(LocalDateTime.of(2026, 3, 20, 14, 0));
        when(existingScreening.getMovie()).thenReturn(movie);

        when(screeningRepository.findByHall_IdAndStartTimeBetween(eq(hallId), any(), any()))
                .thenReturn(List.of(existingScreening));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> screeningService.create(request)
        );

        assertTrue(exception.getMessage().contains("Screening overlaps"));

        verify(screeningRepository, never()).save(any(Screening.class));
    }
}
