package com.hotel.service;

import com.hotel.config.AppConfig;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import com.hotel.storage.XMLStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
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

@DisplayName("Concurrency & Double-Booking Prevention Tests")
public class ConcurrencyBookingTest {

    @TempDir
    Path tempDir;

    private ReservationService reservationService;
    private XMLStorage storage;

    @BeforeEach
    void setUp() {
        AppConfig.setDataDirectory(tempDir);
        storage = XMLStorage.getInstance();
        storage.resetToDefaults();
        reservationService = new ReservationService(storage);
    }

    @Test
    @DisplayName("Simultaneous concurrent booking requests for overlapping dates allow strictly one success")
    void testConcurrentOverlappingBookings() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<String> successfulBookingIds = Collections.synchronizedList(new ArrayList<>());

        LocalDate checkIn = LocalDate.now().plusDays(20);
        LocalDate checkOut = LocalDate.now().plusDays(25);
        String targetRoomId = "R006"; // Presidential Suite

        for (int i = 0; i < threadCount; i++) {
            final int guestNum = i + 1;
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    // Wait for simultaneous start signal
                    startLatch.await();

                    Reservation res = reservationService.createReservation(
                            "Concurrent Guest " + guestNum,
                            "guest" + guestNum + "@example.com",
                            "987654321" + (guestNum % 10),
                            targetRoomId,
                            checkIn,
                            checkOut,
                            2,
                            "Thread test"
                    );

                    successCount.incrementAndGet();
                    successfulBookingIds.add(res.getBookingId());
                } catch (IllegalStateException e) {
                    // Expected rejection due to room already booked
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // Wait for all threads to be ready
        readyLatch.await(5, TimeUnit.SECONDS);
        // Fire start trigger simultaneously
        startLatch.countDown();
        // Wait for all threads to finish
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS), "Threads did not complete in time");
        executor.shutdown();

        // Critical assertion: EXACTLY 1 booking succeeded and 9 failed
        assertEquals(1, successCount.get(), "Exactly one concurrent booking must succeed");
        assertEquals(9, failureCount.get(), "Nine concurrent bookings must be rejected due to overlap");
        assertEquals(1, successfulBookingIds.size());

        // Verify storage state
        List<Reservation> allInStorage = storage.getAllReservations();
        long activePresidentialBookings = allInStorage.stream()
                .filter(r -> r.getRoomId().equals(targetRoomId))
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();

        assertEquals(1, activePresidentialBookings, "Only one confirmed reservation exists in XML storage");
    }
}
