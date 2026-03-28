package com.portfolio.cinema_system_reservation.service;

import com.portfolio.cinema_system_reservation.model.Hall;
import com.portfolio.cinema_system_reservation.model.Seat;
import com.portfolio.cinema_system_reservation.repository.HallRepository;
import com.portfolio.cinema_system_reservation.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock
    private HallRepository hallRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatService seatService;

    @Captor
    private ArgumentCaptor<List<Seat>> seatListCaptor;

    @Test
    void generateSeats_ShouldThrowException_WhenDimensionsAreInvalid() {
        assertThrows(IllegalArgumentException.class, ()->  seatService.generateSeats(1L,0,10));
        assertThrows(IllegalArgumentException.class, ()->  seatService.generateSeats(1L,10,-5));
    }

    @Test
    void generateSeats_ShouldSaveOnlyMissingSeats_WhenSomeSeatsAlreadyExist() {
        Long hallId = 1L;
        Hall hall = mock(Hall.class);
        when(hallRepository.findById(hallId)).thenReturn(Optional.of(hall));

        Seat existingSeat = mock(Seat.class);
        when(existingSeat.getRow()).thenReturn(1);
        when(existingSeat.getNumber()).thenReturn(1);

        when(seatRepository.findByHall_IdOrderByRowAscNumberAsc(hallId)).thenReturn(List.of(existingSeat));

        int createdCount = seatService.generateSeats(hallId,2,2);

        assertEquals(3, createdCount);

        verify(seatRepository, times(1)).saveAll(seatListCaptor.capture());

        List<Seat> savedSeats = seatListCaptor.getValue();
        assertEquals(3, savedSeats.size());

        boolean containSeat1_1 = savedSeats.stream()
                .anyMatch(seat -> seat.getRow() == 1 && seat.getNumber() == 1);
        assertFalse(containSeat1_1);
    }
}
