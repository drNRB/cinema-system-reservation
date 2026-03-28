package com.portfolio.cinema_system_reservation.service;

import com.portfolio.cinema_system_reservation.dto.CreateMovieRequest;
import com.portfolio.cinema_system_reservation.dto.MovieDto;
import com.portfolio.cinema_system_reservation.model.Movie;
import com.portfolio.cinema_system_reservation.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    void create_ShouldThrowException_WhenMovieAlreadyExists() {
        CreateMovieRequest request = new CreateMovieRequest("Test", 136);
        when(movieRepository.existsByTitleIgnoreCase("Test")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> movieService.create(request)
        );

        assertEquals("Movie already exists: Test", exception.getMessage());
        verify(movieRepository, never()).save(any());
    }

    @Test
    void create_ShouldSaveAndReturnMovieDto_WhenTitleIsUnique() {
        CreateMovieRequest request = new CreateMovieRequest("Test", 136);
        when(movieRepository.existsByTitleIgnoreCase("Test")).thenReturn(false);

        Movie savedMovie = mock(Movie.class);
        when(savedMovie.getId()).thenReturn(1L);
        when(savedMovie.getTitle()).thenReturn("Test");
        when(savedMovie.getDurationMinutes()).thenReturn(136);

        when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);

        MovieDto result = movieService.create(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test",result.title());
        assertEquals(136, result.durationMinutes());
    }
}
