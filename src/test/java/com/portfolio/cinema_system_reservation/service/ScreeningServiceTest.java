package com.portfolio.cinema_system_reservation.service;

import com.portfolio.cinema_system_reservation.dto.CreateScreeningRequest;
import com.portfolio.cinema_system_reservation.dto.ScreeningDto;
import com.portfolio.cinema_system_reservation.dto.SeatStatusDto;
import com.portfolio.cinema_system_reservation.exceptions.ResourceNotFoundException;
import com.portfolio.cinema_system_reservation.exceptions.ScreeningOverlapException;
import com.portfolio.cinema_system_reservation.model.*;
import com.portfolio.cinema_system_reservation.repository.HallRepository;
import com.portfolio.cinema_system_reservation.repository.ReservedSeatRepository;
import com.portfolio.cinema_system_reservation.repository.ScreeningRepository;
import com.portfolio.cinema_system_reservation.repository.SeatRepository;
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
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private ReservedSeatRepository reservedSeatRepository;

    @InjectMocks
    private ScreeningService screeningService;

    @Test
    void create_ShouldSaveScreening_WhenThereIsNoOverlap() {
        Long moveId = 1L;
        Long hallId = 1L;

        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
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
    void create_ShouldThrowScreeningOverlapException_WhenScreeningsOverlap() {
        Long movieId = 1L;
        Long hallId = 1L;

        LocalDateTime newStartTime = LocalDateTime.now().plusDays(1).withHour(15).withMinute(0);
        CreateScreeningRequest request = new CreateScreeningRequest(movieId, hallId, newStartTime);

        Movie movie = mock(Movie.class);
        when(movie.getDurationMinutes()).thenReturn(120);

        Hall hall = mock(Hall.class);

        when(movieService.getOrThrow(movieId)).thenReturn(movie);
        when(hallRepository.findById(hallId)).thenReturn(Optional.of(hall));

        Screening existingScreening = mock(Screening.class);
        when(existingScreening.getStartTime()).thenReturn(newStartTime.minusHours(1));
        when(existingScreening.getMovie()).thenReturn(movie);

        when(screeningRepository.findByHall_IdAndStartTimeBetween(eq(hallId), any(), any()))
                .thenReturn(List.of(existingScreening));

        ScreeningOverlapException exception = assertThrows(
                ScreeningOverlapException.class,
                () -> screeningService.create(request)
        );

        assertTrue(exception.getMessage().contains("Screening overlaps"));

        verify(screeningRepository, never()).save(any(Screening.class));
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenHallDoesNotExists() {
        Long movieId = 1L;
        Long hallId = 999L;
        CreateScreeningRequest request = new CreateScreeningRequest(movieId, hallId, LocalDateTime.now().plusDays(1));

        Movie movie = mock(Movie.class);
        when(movieService.getOrThrow(movieId)).thenReturn(movie);
        when(hallRepository.findById(hallId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> screeningService.create(request));
        verify(screeningRepository, never()).save(any());
    }

    @Test
    void listByHall_ShouldReturnListOfScreeningDtos() {
        Long hallId = 1L;
        Screening mockScreening = mock(Screening.class);
        Movie mockMovie = mock(Movie.class);
        Hall mockHall = mock(Hall.class);

        when(mockScreening.getId()).thenReturn(10L);
        when(mockScreening.getMovie()).thenReturn(mockMovie);
        when(mockScreening.getHall()).thenReturn(mockHall);
        when(mockScreening.getStartTime()).thenReturn(LocalDateTime.now().plusDays(1));
        when(mockMovie.getId()).thenReturn(1L);
        when(mockHall.getId()).thenReturn(hallId);

        when(screeningRepository.findByHall_IdOrderByStartTimeAsc(hallId)).thenReturn(List.of(mockScreening));

        List<ScreeningDto> result = screeningService.listByHall(hallId);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).id());
    }

    @Test
    void listByMovie_ShouldReturnListOfScreeningDtos() {
        Long moveId = 1L;
        Screening mockScreening = mock(Screening.class);
        Movie mockMovie = mock(Movie.class);
        Hall mockHall = mock(Hall.class);

        when(mockScreening.getId()).thenReturn(10L);
        when(mockScreening.getMovie()).thenReturn(mockMovie);
        when(mockScreening.getHall()).thenReturn(mockHall);
        when(mockScreening.getStartTime()).thenReturn(LocalDateTime.now().plusDays(1));
        when(mockMovie.getId()).thenReturn(moveId);
        when(mockHall.getId()).thenReturn(1L);

        when(screeningRepository.findByMovie_IdOrderByStartTimeAsc(moveId)).thenReturn(List.of(mockScreening));

        List<ScreeningDto> result = screeningService.listByMovie(moveId);

        assertEquals(1, result.size());
    }

    @Test
    void getSeatStatus_ShouldThrowResourceNotFoundException_WhenScreeningDoesNotExists() {
        Long screeningId = 999L;
        when(screeningRepository.findById(screeningId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> screeningService.getSeatStatus(screeningId));
    }

    @Test
    void getSeatStatus_ShouldReturnMappedSeatStatuses() {
        Long screeningId = 1L;
        Long hallId = 2L;

        Screening mockScreening = mock(Screening.class);
        Hall mockHall = mock(Hall.class);
        when(mockScreening.getHall()).thenReturn(mockHall);
        when(mockHall.getId()).thenReturn(hallId);

        Seat mockSeat1 = mock(Seat.class);
        when(mockSeat1.getId()).thenReturn(101L);
        when(mockSeat1.getRow()).thenReturn(1);
        when(mockSeat1.getNumber()).thenReturn(1);

        Seat mockSeat2 = mock(Seat.class);
        when(mockSeat2.getId()).thenReturn(102L);
        when(mockSeat2.getRow()).thenReturn(1);
        when(mockSeat2.getNumber()).thenReturn(2);

        ReservedSeat mockReservedSeat = mock(ReservedSeat.class);
        when(mockReservedSeat.getSeat()).thenReturn(mockSeat2);

        when(screeningRepository.findById(screeningId)).thenReturn(Optional.of(mockScreening));
        when(seatRepository.findByHall_IdOrderByRowAscNumberAsc(hallId)).thenReturn(List.of(mockSeat1, mockSeat2));
        when(reservedSeatRepository.findByScreening_Id(screeningId)).thenReturn(List.of(mockReservedSeat));

        List<SeatStatusDto> result = screeningService.getSeatStatus(screeningId);

        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).seatId());
        assertFalse(result.get(0).isReserved());
        assertEquals(102L, result.get(1).seatId());
        assertTrue(result.get(1).isReserved());

    }


}
