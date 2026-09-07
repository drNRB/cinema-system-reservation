package com.portfolio.cinema_system_reservation.service;

import com.portfolio.cinema_system_reservation.dto.CreateReservationRequest;
import com.portfolio.cinema_system_reservation.exceptions.SeatAlreadyReservedException;
import com.portfolio.cinema_system_reservation.model.Hall;
import com.portfolio.cinema_system_reservation.model.Movie;
import com.portfolio.cinema_system_reservation.model.Screening;
import com.portfolio.cinema_system_reservation.model.Seat;
import com.portfolio.cinema_system_reservation.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class ReservationServiceConcurrencyTest {
    private final List<Throwable> unexpectedErrors = Collections.synchronizedList(new ArrayList<>());

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private HallRepository hallRepository;
    @Autowired
    private ScreeningRepository screeningRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ReservationRepository reservationRepository;



    @Test
    void shouldPreventDoubleBookingUnderConcurrentLoad() throws InterruptedException {
        Movie movie = movieRepository.save(new Movie("Interstellar", 169));
        Hall hall = hallRepository.save(new Hall("Hall 1"));
        Seat seat = seatRepository.save(new Seat(hall, 5, 10));


        Screening screening = screeningRepository.save(
                new Screening(movie, hall, LocalDateTime.of(2099, 5, 1, 19, 0))
        );

        Long realScreeningId = screening.getId();
        Long realSeatId = seat.getId();

        CreateReservationRequest request1 = new CreateReservationRequest(realScreeningId, List.of(realSeatId), "User A");
        CreateReservationRequest request2 = new CreateReservationRequest(realScreeningId, List.of(realSeatId), "User B");

        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger successfulReservations = new AtomicInteger(0);
        AtomicInteger failedReservations = new AtomicInteger(0);

        Runnable task1 = createReservationTask(request1, startLatch, endLatch, successfulReservations, failedReservations);
        Runnable task2 = createReservationTask(request2, startLatch, endLatch, successfulReservations, failedReservations);

        executorService.submit(task1);
        executorService.submit(task2);

        startLatch.countDown();

        assertTrue(endLatch.await(30, TimeUnit.SECONDS), "The threads didn't end by the set time");
        executorService.shutdown();


        assertTrue(unexpectedErrors.isEmpty(), "Unexpected errors: " + unexpectedErrors);

        assertEquals(1, successfulReservations.get(), "Only one reservation should be successful.");
        assertEquals(1, failedReservations.get(), "Second reservation should fail with SeatAlreadyReservedException.");

        long totalReservationsInDb = reservationRepository.count();
        assertEquals(1, totalReservationsInDb, "There should be only one reservation in database.");

    }

    private Runnable createReservationTask(CreateReservationRequest request,
                                           CountDownLatch startLatch,
                                           CountDownLatch endLatch,
                                           AtomicInteger successCount,
                                           AtomicInteger failCount) {
        return () -> {
            try {
                startLatch.await();
                reservationService.create(request);
                successCount.incrementAndGet();
            } catch (SeatAlreadyReservedException e) {
                failCount.incrementAndGet();
            } catch (Exception e) {
                unexpectedErrors.add(e);
            } finally {
                endLatch.countDown();
            }

        };
    }


}
