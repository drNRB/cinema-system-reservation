package com.portfolio.cinema_system_reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.cinema_system_reservation.dto.CreateMovieRequest;
import com.portfolio.cinema_system_reservation.dto.MovieDto;
import com.portfolio.cinema_system_reservation.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovieService movieService;

    @Test
    void create_ShouldReturnCreatedMovie_WhenValidRequestIsProvided() throws Exception {
        Long movieId = 1L;
        CreateMovieRequest request = new CreateMovieRequest("Inception",148);
        MovieDto mockMovieDto = new MovieDto(
                movieId,
                "Inception",
                148
        );

        when(movieService.create(request)).thenReturn(mockMovieDto);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.durationMinutes").value(148));
    }

    @Test
    void list_ShouldReturnPagesOfMovies_WhenTheyExists() throws Exception {
        MovieDto mockMovieDto = new MovieDto(1L, "Inception", 148);
        List<MovieDto> moviesList = List.of(mockMovieDto);
        Page<MovieDto> mockPage = new PageImpl<>(moviesList);

        when(movieService.list(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/movies")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Inception"))
                .andExpect(jsonPath("$.content.size()").value(1));
    }
}
