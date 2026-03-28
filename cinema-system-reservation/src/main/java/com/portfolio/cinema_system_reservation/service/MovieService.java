package com.portfolio.cinema_system_reservation.service;

import com.portfolio.cinema_system_reservation.dto.CreateMovieRequest;
import com.portfolio.cinema_system_reservation.dto.MovieDto;
import com.portfolio.cinema_system_reservation.exceptions.ResourceNotFoundException;
import com.portfolio.cinema_system_reservation.model.Movie;
import com.portfolio.cinema_system_reservation.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Transactional
    public MovieDto create(CreateMovieRequest request) {
        if (movieRepository.existsByTitleIgnoreCase(request.title())) {
            throw new IllegalArgumentException("Movie already exists: " + request.title());
        }

        Movie movie = new Movie(request.title().trim(), request.durationMinutes());
        Movie saved = movieRepository.save(movie);

        return new MovieDto(saved.getId(), saved.getTitle(), saved.getDurationMinutes());
    }

    @Transactional(readOnly = true)
    public Page<MovieDto> list(Pageable pageable) {
        return movieRepository.findAll(pageable)
                .map(movie -> new MovieDto(movie.getId(), movie.getTitle(), movie.getDurationMinutes()));
    }

    @Transactional(readOnly = true)
    public Movie getOrThrow(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + id));
    }


}
