package com.hotel.storage;

import com.hotel.config.AppConfig;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import com.hotel.model.Room;
import com.hotel.model.RoomType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("XML Storage Engine Tests")
public class XMLStorageTest {

    @TempDir
    Path tempDir;

    private XMLStorage storage;

    @BeforeEach
    void setUp() {
        AppConfig.setDataDirectory(tempDir);
        storage = XMLStorage.getInstance();
        storage.resetToDefaults();
    }

    @Test
    @DisplayName("Default initialization seeds 6 distinct hotel rooms")
    void testSeedDefaultRooms() {
        List<Room> rooms = storage.getAllRooms();
        assertNotNull(rooms);
        assertEquals(6, rooms.size());

        Optional<Room> standard = storage.getRoomById("R001");
        assertTrue(standard.isPresent());
        assertEquals("Standard Room", standard.get().getName());
        assertEquals(1500.00, standard.get().getPricePerNight());

        Optional<Room> presidential = storage.getRoomById("R006");
        assertTrue(presidential.isPresent());
        assertEquals("Presidential Suite", presidential.get().getName());
        assertEquals(8000.00, presidential.get().getPricePerNight());
    }

    @Test
    @DisplayName("Room additions and updates persist reliably in XML")
    void testSaveAndModifyRoom() {
        Room newRoom = new Room("R007", "Royal Penthouse", RoomType.PRESIDENTIAL_SUITE.name(),
                "Spectacular top floor view", 12000.00, 4,
                List.of("Private Bar", "Infinity Pool"), "https://example.com/r7.jpg", true);

        storage.saveRoom(newRoom);

        Optional<Room> retrieved = storage.getRoomById("R007");
        assertTrue(retrieved.isPresent());
        assertEquals("Royal Penthouse", retrieved.get().getName());
        assertEquals(12000.00, retrieved.get().getPricePerNight());
        assertEquals(7, storage.getAllRooms().size());

        // Update the room
        retrieved.get().setPricePerNight(13500.00);
        retrieved.get().setDescription("Updated penthouse description");
        storage.saveRoom(retrieved.get());

        Optional<Room> updated = storage.getRoomById("R007");
        assertTrue(updated.isPresent());
        assertEquals(13500.00, updated.get().getPricePerNight());
        assertEquals("Updated penthouse description", updated.get().getDescription());
        assertEquals(7, storage.getAllRooms().size()); // No duplicate room created
    }

    @Test
    @DisplayName("Booking ID generation produces sequential BK-prefixed identifiers")
    void testBookingIdGeneration() {
        String id1 = storage.generateNextBookingId();
        assertEquals("BK100001", id1);

        Reservation r1 = new Reservation(id1, "Guest One", "g1@example.com", "9876543210",
                "R001", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), 2,
                1500.0, 1500.0, "", ReservationStatus.CONFIRMED, LocalDateTime.now());
        storage.saveReservation(r1);

        String id2 = storage.generateNextBookingId();
        assertEquals("BK100002", id2);
    }

    @Test
    @DisplayName("Reservation records are saved and queried accurately")
    void testSaveAndRetrieveReservation() {
        Reservation res = new Reservation("BK100001", "Ananya Verma", "ananya@example.com", "+91 98765 00000",
                "R002", LocalDate.now().plusDays(5), LocalDate.now().plusDays(8), 2,
                2500.00, 7500.00, "High floor requested", ReservationStatus.CONFIRMED, LocalDateTime.now());

        storage.saveReservation(res);

        Optional<Reservation> retrieved = storage.getReservationById("BK100001");
        assertTrue(retrieved.isPresent());
        assertEquals("Ananya Verma", retrieved.get().getCustomerName());
        assertEquals("ananya@example.com", retrieved.get().getEmail());
        assertEquals(7500.00, retrieved.get().getTotalPrice());
        assertEquals(ReservationStatus.CONFIRMED, retrieved.get().getStatus());
        assertEquals("High floor requested", retrieved.get().getSpecialRequests());
    }
}
