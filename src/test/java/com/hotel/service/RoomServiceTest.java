package com.hotel.service;

import com.hotel.config.AppConfig;
import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.storage.XMLStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Room Service & Catalog Filtering Tests")
public class RoomServiceTest {

    @TempDir
    Path tempDir;

    private RoomService roomService;
    private XMLStorage storage;

    @BeforeEach
    void setUp() {
        AppConfig.setDataDirectory(tempDir);
        storage = XMLStorage.getInstance();
        storage.resetToDefaults();
        roomService = new RoomService(storage);
    }

    @Test
    @DisplayName("Filter rooms by category returns only matched room types")
    void testFilterByCategory() {
        List<Room> deluxeRooms = roomService.filterRooms("DELUXE", null, null, null, null, null, null);
        assertFalse(deluxeRooms.isEmpty());
        for (Room r : deluxeRooms) {
            assertEquals("DELUXE", r.getType());
        }
    }

    @Test
    @DisplayName("Filter rooms by maximum price restricts results properly")
    void testFilterByMaxPrice() {
        List<Room> budgetRooms = roomService.filterRooms(null, 3000.00, null, null, null, null, null);
        assertFalse(budgetRooms.isEmpty());
        for (Room r : budgetRooms) {
            assertTrue(r.getPricePerNight() <= 3000.00);
        }
    }

    @Test
    @DisplayName("Filter rooms by guest capacity returns rooms that accommodate the party")
    void testFilterByCapacity() {
        List<Room> familyRooms = roomService.filterRooms(null, null, 4, null, null, null, null);
        assertFalse(familyRooms.isEmpty());
        for (Room r : familyRooms) {
            assertTrue(r.getCapacity() >= 4);
        }
    }

    @Test
    @DisplayName("Keyword search matches room description or amenities")
    void testKeywordSearch() {
        List<Room> jacuzziRooms = roomService.filterRooms(null, null, null, "jacuzzi", null, null, null);
        assertFalse(jacuzziRooms.isEmpty());
        for (Room r : jacuzziRooms) {
            boolean inDesc = r.getDescription().toLowerCase().contains("jacuzzi");
            boolean inAmenities = r.getAmenities().stream().anyMatch(a -> a.toLowerCase().contains("jacuzzi"));
            assertTrue(inDesc || inAmenities);
        }
    }

    @Test
    @DisplayName("Sorting by price orders rooms in ascending or descending order")
    void testSorting() {
        List<Room> asc = roomService.filterRooms(null, null, null, null, "price_asc", null, null);
        for (int i = 0; i < asc.size() - 1; i++) {
            assertTrue(asc.get(i).getPricePerNight() <= asc.get(i + 1).getPricePerNight());
        }

        List<Room> desc = roomService.filterRooms(null, null, null, null, "price_desc", null, null);
        for (int i = 0; i < desc.size() - 1; i++) {
            assertTrue(desc.get(i).getPricePerNight() >= desc.get(i + 1).getPricePerNight());
        }
    }

    @Test
    @DisplayName("Toggling room status disables room from active listing")
    void testToggleRoomStatus() {
        boolean newStatus = roomService.toggleRoomStatus("R001");
        assertFalse(newStatus);

        Room r001 = roomService.getRoomById("R001").orElseThrow();
        assertFalse(r001.isActive());

        // Room should not appear in active rooms listing
        List<Room> activeRooms = roomService.getActiveRooms();
        assertFalse(activeRooms.stream().anyMatch(r -> r.getId().equals("R001")));

        // Toggle back to active
        boolean restoredStatus = roomService.toggleRoomStatus("R001");
        assertTrue(restoredStatus);
        assertTrue(roomService.getActiveRooms().stream().anyMatch(r -> r.getId().equals("R001")));
    }
}
