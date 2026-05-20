package com.portfolio.cinema_system_reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.cinema_system_reservation.model.Seat;
import com.portfolio.cinema_system_reservation.repository.SeatRepository;
import com.portfolio.cinema_system_reservation.service.SeatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeatController.class)
class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SeatService seatService;

    @MockitoBean
    private SeatRepository seatRepository;

    @Test
    void post_ShouldReturnSeats_WhenItsGenerated() throws Exception {
        when(seatService.generateSeats(1L,10,10)).thenReturn(100);

        mockMvc.perform(post("/api/halls/{hallId}/seats/generate",1L)
                .param("rows","10")
                .param("seatsPerRow", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Created seats: 100"));
    }

    @Test
    void get_ShouldReturnSeats_WhenTheyExists() throws Exception {
        Long hallId = 1L;
        Seat mockSeat = mock(Seat.class);

        when(mockSeat.getId()).thenReturn(10L);
        when(mockSeat.getRow()).thenReturn(2);
        when(mockSeat.getNumber()).thenReturn(5);

        when(seatRepository.findByHall_IdOrderByRowAscNumberAsc(hallId)).thenReturn(List.of(mockSeat));

        mockMvc.perform(get("/api/halls/{hallId}/seats",hallId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].row").value(2))
                .andExpect(jsonPath("$[0].number").value(5));

    }
}