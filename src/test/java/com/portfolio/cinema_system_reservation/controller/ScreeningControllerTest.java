package com.portfolio.cinema_system_reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.cinema_system_reservation.dto.CreateScreeningRequest;
import com.portfolio.cinema_system_reservation.dto.ScreeningDto;
import com.portfolio.cinema_system_reservation.dto.SeatStatusDto;
import com.portfolio.cinema_system_reservation.service.ScreeningService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ScreeningController.class)
class ScreeningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ScreeningService screeningService;

    @Test
    void get_ShouldReturnScreenings_WhenTheyExists() throws Exception {
        Long hallId = 1L;
        ScreeningDto mockScreeningDto = new ScreeningDto(
                1L,
                2L,
                "Inception",
                140,
                hallId,
                "VIP Hall",
                LocalDateTime.of(2026,5,1,18,0)
        );

        when(screeningService.listByHall(hallId)).thenReturn(List.of(mockScreeningDto));

        mockMvc.perform(get("/api/screenings/by-hall/{hallId}", hallId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));

    }

    @Test
    void listByMovie_ShouldReturnScreenings_WhenTheyExists() throws Exception {
        Long movieId = 1L;
        ScreeningDto mockScreeningDto = new ScreeningDto(
                1L,
                movieId,
                "Inception",
                140,
                1L,
                "VIP Hall",
                LocalDateTime.of(2026,5,1,18,0)
        );

        when(screeningService.listByMovie(movieId)).thenReturn(List.of(mockScreeningDto));

        mockMvc.perform(get("/api/screenings/by-movie/{movieId}", movieId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getSeatsStatus_ShouldReturnListOfSeatStatus_WhenScreeningExists() throws Exception {
        Long screeningId = 1L;
        SeatStatusDto mockSeatStatus = new SeatStatusDto(
                15L,
                5,
                4,
                true
        );

        when(screeningService.getSeatStatus(screeningId)).thenReturn(List.of(mockSeatStatus));

        mockMvc.perform(get("/api/screenings/{id}/seats", screeningId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].row").value(5))
                .andExpect(jsonPath("$[0].isReserved").value(true));
    }

    @Test
    void post_ShouldReturnReservation_WhenItsExists() throws Exception {
        CreateScreeningRequest request = new CreateScreeningRequest(
                1L,
                2L,
                LocalDateTime.of(2026,5,1,18,0)
        );

        ScreeningDto responseDto = new ScreeningDto(
                10L,
                1L,
                "Inception",
                140,
                2L,
                "VIP Hall",
                LocalDateTime.of(2026,5,1,18,0)
        );

        when(screeningService.create(any(CreateScreeningRequest.class))).thenReturn(responseDto);

        String requestJson = objectMapper.writeValueAsString(request);


        mockMvc.perform(post("/api/screenings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.movieTitle").value("Inception"));

    }
}