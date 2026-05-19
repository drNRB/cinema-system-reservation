package com.portfolio.cinema_system_reservation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.cinema_system_reservation.dto.CreateReservationRequest;
import com.portfolio.cinema_system_reservation.dto.ReservationDto;
import com.portfolio.cinema_system_reservation.service.ReservationService;
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

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    void get_ShouldReturnReservation_WhenReservationExists() throws Exception {
        Long id = 1L;
        ReservationDto mockDto = new ReservationDto(
                id,
                2L,
                "Inception",
                1L,
                null,
                "John Doe",
                null,
                List.of()
        );

        when(reservationService.get(id)).thenReturn(mockDto);

        mockMvc.perform(get("/api/reservations/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.movieTitle").value("Inception"));
    }

    @Test
    void listByScreening_ShouldReturnListOfReservation_WhenTheyExist() throws Exception {
        Long screeningId = 3L;
        ReservationDto mockDtoFirst = new ReservationDto(
                10L,
                screeningId,
                "Inception",
                1L,
                null,
                "John Doe",
                null,
                List.of()
        );

        ReservationDto mockDtoSecond = new ReservationDto(
                11L,
                screeningId,
                "Inception",
                1L,
                null,
                "Mark Johnson",
                null,
                List.of()
        );

        when(reservationService.listByScreening(screeningId)).thenReturn(List.of(mockDtoFirst, mockDtoSecond));

        mockMvc.perform(get("/api/reservations/by-screening/{screeningId}", screeningId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerName").value("John Doe"))
                .andExpect(jsonPath("$[1].customerName").value("Mark Johnson"));
    }

    @Test
    void cancel_ShouldReturnNoContent_WhenReservationIsCancelled() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/api/reservations/{id}", id))
                .andExpect(status().isNoContent());

        verify(reservationService, times(1)).cancel(id);
    }

    @Test
    void create_ShouldReturnReservationDto_WhenRequestIsValid() throws Exception {
        CreateReservationRequest request = new CreateReservationRequest(
                3L, List.of(10L, 11L), "John Doe");
        ReservationDto responseDto = new ReservationDto(
                1L,
                3L,
                "Inception",
                1L,
                null,
                "John Doe",
                null, List.of()
        );

        when(reservationService.create(any(CreateReservationRequest.class))).thenReturn(responseDto);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("John Doe"));
    }

}
