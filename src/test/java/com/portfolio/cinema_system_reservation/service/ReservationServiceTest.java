package com.portfolio.cinema_system_reservation.service;

import com.portfolio.cinema_system_reservation.dto.CreateReservationRequest;
import com.portfolio.cinema_system_reservation.dto.ReservationDto;
import com.portfolio.cinema_system_reservation.exceptions.InvalidReservationException;
import com.portfolio.cinema_system_reservation.exceptions.ResourceNotFoundException;
import com.portfolio.cinema_system_reservation.exceptions.SeatAlreadyReservedException;
import com.portfolio.cinema_system_reservation.model.*;
import com.portfolio.cinema_system_reservation.repository.ReservationRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReservedSeatRepository reservedSeatRepository;
    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void cancel_ShouldThrowException_WhenScreeningStartsInLessThan15Minutes() {
        Long reservationId = 1L;
        Movie movie = new Movie("Test movie", 120);
        Hall hall = new Hall("Test hall");

        Screening screening = new Screening(movie, hall, LocalDateTime.now().plusMinutes(10));
        Reservation reservation = new Reservation(screening, "John Doe");

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        assertThrows(InvalidReservationException.class, () -> reservationService.cancel(reservationId));

        verify(reservationRepository, never()).delete(any());
    }

    @Test
    void cancel_ShouldDeleteReservation_WhenScreeningIsFarInFuture() {
        Long reservationId = 1L;
        Movie movie = new Movie("Test movie", 120);
        Hall hall = new Hall("Test hall");

        Screening screening = new Screening(movie, hall, LocalDateTime.now().plusDays(2));
        Reservation reservation = new Reservation(screening, "John Doe");

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        reservationService.cancel(reservationId);

        verify(reservationRepository, times(1)).delete(reservation);
    }

    @Test
    void create_ShouldThrowSeatAlreadyReservedException_WhenSeatIsTaken() {
        Long screeningId = 1L;
        Long hallId = 1L;
        Long seatId = 10L;
        CreateReservationRequest request = new CreateReservationRequest(screeningId, List.of(seatId), "John Doe");

        Screening screening = mock(Screening.class);
        Hall hall = mock(Hall.class);
        Seat seat = mock(Seat.class);

        when(screening.getId()).thenReturn(screeningId);
        when(screening.getHall()).thenReturn(hall);
        when(hall.getId()).thenReturn(hallId);

        when(screening.getStartTime()).thenReturn(LocalDateTime.now().plusDays(1));

        when(seat.getId()).thenReturn(seatId);
        when(seat.getHall()).thenReturn(hall);

        when(screeningRepository.findById(screeningId)).thenReturn(Optional.of(screening));
        when(seatRepository.findAllById(any())).thenReturn(List.of(seat));

        when(reservedSeatRepository.existsByScreening_IdAndSeat_Id(screeningId, seatId)).thenReturn(true);

        assertThrows(SeatAlreadyReservedException.class, () -> reservationService.create(request));
        verify(reservationRepository, never()).saveAndFlush(any());
    }

    @Test
    void get_ShouldReturnReservationDto_WhenReservationExists() {
        Long id = 1L;
        Reservation mockReservation = mock(Reservation.class);
        Screening mockScreening = mock(Screening.class);
        Movie mockMovie = mock(Movie.class);
        Hall mockHall = mock(Hall.class);

        when(mockReservation.getId()).thenReturn(id);
        when(mockReservation.getCustomerName()).thenReturn("John Doe");
        when(mockReservation.getScreening()).thenReturn(mockScreening);

        when(mockMovie.getTitle()).thenReturn("Inception");

        when(mockScreening.getId()).thenReturn(2L);
        when(mockScreening.getMovie()).thenReturn(mockMovie);
        when(mockScreening.getHall()).thenReturn(mockHall);
        when(mockScreening.getStartTime()).thenReturn(LocalDateTime.now().plusDays(1));

        when(reservationRepository.findById(id)).thenReturn(Optional.of(mockReservation));

        ReservationDto result = reservationService.get(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("John Doe", result.customerName());
    }

    @Test
    void get_ShouldThrowResourceNotFoundException_WhenReservationDoesNotExists() {
        Long id = 999L;
        when(reservationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.get(id));
    }

    @Test
    void listByScreening_ShouldReturnListOfReservationDtos() {
        Long screeningId = 1L;
        Reservation mockReservation = mock(Reservation.class);
        Screening mockScreening = mock(Screening.class);
        Movie mockMovie = mock(Movie.class);
        Hall mockHall = mock(Hall.class);

        when(mockScreening.getMovie()).thenReturn(mockMovie);
        when(mockScreening.getHall()).thenReturn(mockHall);

        when(mockMovie.getTitle()).thenReturn("Inception");
        when(mockHall.getId()).thenReturn(99L);

        when(mockReservation.getId()).thenReturn(10L);
        when(mockReservation.getCustomerName()).thenReturn("John Doe");
        when(mockReservation.getScreening()).thenReturn(mockScreening);

        when(mockScreening.getId()).thenReturn(screeningId);

        when(reservationRepository.findByScreening_IdOrderByCreatedAtAsc(screeningId))
                .thenReturn(List.of(mockReservation));

        List<ReservationDto> result = reservationService.listByScreening(screeningId);
        
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).id());
        assertEquals("John Doe", result.get(0).customerName());
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenScreeningDoesNotExist() {
        Long screeningId = 999L;
        CreateReservationRequest request = new CreateReservationRequest(screeningId, List.of(1L, 2L), "John Doe");

        when(screeningRepository.findById(screeningId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.create(request));
        verify(reservationRepository, never()).saveAndFlush(any());
    }

    @Test
    void cancel_ShouldThrowResourceNotFoundException_WhenReservationDoesNotExist() {
        Long id = 999L;
        when(reservationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.cancel(id));
        verify(reservationRepository, never()).delete(any());
    }


}
