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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Reservation Service & Collision Detection Tests")
public class ReservationServiceTest {

    @TempDir
    Path tempDir;

    private ReservationService reservationService;
    private RoomService roomService;
    private XMLStorage storage;

    @BeforeEach
    void setUp() {
        AppConfig.setDataDirectory(tempDir);
        storage = XMLStorage.getInstance();
        storage.resetToDefaults();
        reservationService = new ReservationService(storage);
        roomService = new RoomService(storage);
    }

    @Test
    @DisplayName("Valid reservation calculates total price accurately and saves to XML")
    void testCreateValidReservation() {
        LocalDate in = LocalDate.now().plusDays(5);
        LocalDate out = LocalDate.now().plusDays(8); // 3 nights

        Reservation res = reservationService.createReservation(
                "Siddharth Rao", "sid@example.com", "9876543210",
                "R002", in, out, 2, "Quiet room please"
        );

        assertNotNull(res);
        assertNotNull(res.getBookingId());
        assertTrue(res.getBookingId().startsWith("BK"));
        assertEquals(3, res.getNumberOfNights());
        assertEquals(2500.00, res.getPricePerNight());
        assertEquals(7500.00, res.getTotalPrice());
        assertEquals(ReservationStatus.CONFIRMED, res.getStatus());

        // Verify persisted record in storage
        Optional<Reservation> fromStorage = storage.getReservationById(res.getBookingId());
        assertTrue(fromStorage.isPresent());
        assertEquals(7500.00, fromStorage.get().getTotalPrice());
    }

    @Test
    @DisplayName("Rejects reservation with check-in in the past")
    void testRejectPastCheckIn() {
        LocalDate past = LocalDate.now().minusDays(1);
        LocalDate future = LocalDate.now().plusDays(2);

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(
                        "Test User", "user@example.com", "9876543210",
                        "R001", past, future, 1, ""
                )
        );
    }

    @Test
    @DisplayName("Rejects reservation where check-out is before or equal to check-in")
    void testRejectInvalidDateRange() {
        LocalDate date = LocalDate.now().plusDays(3);

        // Same day check-out rejected
        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(
                        "Test User", "user@example.com", "9876543210",
                        "R001", date, date, 1, ""
                )
        );

        // Check-out earlier than check-in rejected
        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(
                        "Test User", "user@example.com", "9876543210",
                        "R001", date, date.minusDays(1), 1, ""
                )
        );
    }

    @Test
    @DisplayName("Enforces guest capacity limits per room")
    void testEnforceRoomCapacity() {
        LocalDate in = LocalDate.now().plusDays(2);
        LocalDate out = LocalDate.now().plusDays(4);

        // R001 Standard Room has capacity 2. Trying to book for 4 should fail.
        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(
                        "Large Group", "group@example.com", "9876543210",
                        "R001", in, out, 4, ""
                )
        );
    }

    @Test
    @DisplayName("Inactive rooms cannot accept new reservations")
    void testInactiveRoomCannotBeBooked() {
        roomService.toggleRoomStatus("R001"); // Disable R001
        LocalDate in = LocalDate.now().plusDays(2);
        LocalDate out = LocalDate.now().plusDays(4);

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.createReservation(
                        "Guest", "guest@example.com", "9876543210",
                        "R001", in, out, 2, ""
                )
        );
    }

    @Test
    @DisplayName("Date overlap collision detection prevents conflicting bookings")
    void testDateCollisionDetection() {
        LocalDate existingIn = LocalDate.now().plusDays(10);
        LocalDate existingOut = LocalDate.now().plusDays(15);

        reservationService.createReservation(
                "Primary Guest", "primary@example.com", "9876543210",
                "R003", existingIn, existingOut, 2, ""
        );

        // 1. Exact overlap
        assertFalse(reservationService.isRoomAvailable("R003", existingIn, existingOut));

        // 2. Interior overlap (11 to 14)
        assertFalse(reservationService.isRoomAvailable("R003", existingIn.plusDays(1), existingOut.minusDays(1)));

        // 3. Spanning overlap across check-in (8 to 12)
        assertFalse(reservationService.isRoomAvailable("R003", existingIn.minusDays(2), existingIn.plusDays(2)));

        // 4. Spanning overlap across check-out (13 to 18)
        assertFalse(reservationService.isRoomAvailable("R003", existingOut.minusDays(2), existingOut.plusDays(3)));

        // 5. Total engulfing (8 to 20)
        assertFalse(reservationService.isRoomAvailable("R003", existingIn.minusDays(2), existingOut.plusDays(5)));

        // Attempting to book overlapping dates throws IllegalStateException
        assertThrows(IllegalStateException.class, () ->
                reservationService.createReservation(
                        "Conflicting Guest", "conflict@example.com", "9876543210",
                        "R003", existingIn.plusDays(1), existingOut.minusDays(1), 2, ""
                )
        );
    }

    @Test
    @DisplayName("Back-to-back bookings are permitted with exclusive check-out")
    void testBackToBackReservationsAllowed() {
        LocalDate existingIn = LocalDate.now().plusDays(10);
        LocalDate existingOut = LocalDate.now().plusDays(15);

        reservationService.createReservation(
                "Primary Guest", "primary@example.com", "9876543210",
                "R004", existingIn, existingOut, 2, ""
        );

        // Case A: Prior guest checks out on the date new guest checks in
        assertTrue(reservationService.isRoomAvailable("R004", existingIn.minusDays(5), existingIn));

        // Case B: Subsequent guest checks in on the date existing guest checks out
        assertTrue(reservationService.isRoomAvailable("R004", existingOut, existingOut.plusDays(4)));

        // Actually booking back-to-back should succeed cleanly
        assertDoesNotThrow(() ->
                reservationService.createReservation(
                        "Subsequent Guest", "next@example.com", "9876543210",
                        "R004", existingOut, existingOut.plusDays(3), 2, ""
                )
        );
    }

    @Test
    @DisplayName("Booking lookup verifies both booking ID and customer email")
    void testLookupReservation() {
        LocalDate in = LocalDate.now().plusDays(2);
        LocalDate out = LocalDate.now().plusDays(4);

        Reservation res = reservationService.createReservation(
                "Ritu Sen", "ritu.sen@example.com", "9876543210",
                "R001", in, out, 2, ""
        );

        // Correct lookup
        Optional<Reservation> found = reservationService.lookupReservation(res.getBookingId(), "ritu.sen@example.com");
        assertTrue(found.isPresent());
        assertEquals(res.getBookingId(), found.get().getBookingId());

        // Incorrect email should protect guest privacy and return empty
        Optional<Reservation> unauthorized = reservationService.lookupReservation(res.getBookingId(), "hacker@example.com");
        assertTrue(unauthorized.isEmpty());

        // Non-existent booking ID
        Optional<Reservation> notFound = reservationService.lookupReservation("BK999999", "ritu.sen@example.com");
        assertTrue(notFound.isEmpty());
    }

    @Test
    @DisplayName("Cancellation updates XML status and frees room for subsequent bookings")
    void testCancelReservationAndFreeRoom() {
        LocalDate in = LocalDate.now().plusDays(12);
        LocalDate out = LocalDate.now().plusDays(16);

        Reservation res = reservationService.createReservation(
                "Karan Mehra", "karan@example.com", "9876543210",
                "R005", in, out, 3, ""
        );

        // Verify room is currently blocked
        assertFalse(reservationService.isRoomAvailable("R005", in, out));

        // Unauthorized cancel attempt with wrong email should fail
        assertThrows(SecurityException.class, () ->
                reservationService.cancelReservation(res.getBookingId(), "wrong@example.com")
        );

        // Authorized cancellation
        Reservation cancelled = reservationService.cancelReservation(res.getBookingId(), "karan@example.com");
        assertEquals(ReservationStatus.CANCELLED, cancelled.getStatus());

        // Cancellation persists in XML
        Reservation fromXml = storage.getReservationById(res.getBookingId()).orElseThrow();
        assertEquals(ReservationStatus.CANCELLED, fromXml.getStatus());

        // Repeated cancellation should be rejected
        assertThrows(IllegalStateException.class, () ->
                reservationService.cancelReservation(res.getBookingId(), "karan@example.com")
        );

        // Room is now available again for those exact dates!
        assertTrue(reservationService.isRoomAvailable("R005", in, out));

        // A new reservation can now be booked for the same dates!
        assertDoesNotThrow(() ->
                reservationService.createReservation(
                        "New Guest", "newguest@example.com", "9876543210",
                        "R005", in, out, 2, ""
                )
        );
    }
}
