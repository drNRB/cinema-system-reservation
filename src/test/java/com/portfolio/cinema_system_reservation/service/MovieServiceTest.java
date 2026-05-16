package com.portfolio.cinema_system_reservation.service;

import com.portfolio.cinema_system_reservation.dto.CreateMovieRequest;
import com.portfolio.cinema_system_reservation.dto.MovieDto;
import com.portfolio.cinema_system_reservation.exceptions.DuplicateResourceException;
import com.portfolio.cinema_system_reservation.exceptions.ResourceNotFoundException;
import com.portfolio.cinema_system_reservation.model.Movie;
import com.portfolio.cinema_system_reservation.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

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

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
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
        assertEquals("Test", result.title());
        assertEquals(136, result.durationMinutes());
    }

    @Test
    void list_ShouldReturnPageOfMovieDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Movie mockMovie = mock(Movie.class);

        when(mockMovie.getId()).thenReturn(1L);
        when(mockMovie.getTitle()).thenReturn("Inception");
        when(mockMovie.getDurationMinutes()).thenReturn(148);

        Page<Movie> moviePage = new PageImpl<>(List.of(mockMovie));
        when(movieRepository.findAll(pageable)).thenReturn(moviePage);

        Page<MovieDto> result = movieService.list(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Inception", result.getContent().get(0).title());
        assertEquals(148, result.getContent().get(0).durationMinutes());
    }

    @Test
    void getOrThrow_ShouldReturnMovie_WhenMovieExists() {
        Long movieId = 1L;
        Movie mockMovie = mock(Movie.class);
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));

        Movie result = movieService.getOrThrow(movieId);

        assertNotNull(result);
        assertEquals(mockMovie, result);
    }

    @Test
    void getOrThrow_ShouldThrowResourceNotFoundException_WhenMovieDoesNotExists() {
        Long movieId = 999L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> movieService.getOrThrow(movieId)
        );

        assertEquals("Movie not found: " + movieId, exception.getMessage());

    }
}
