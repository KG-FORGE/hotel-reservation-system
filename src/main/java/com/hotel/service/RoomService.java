package com.hotel.service;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.storage.XMLStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service orchestrating room catalog, filtering, sorting, and administrative operations.
 */
public class RoomService {

    private static volatile RoomService instance;
    private final XMLStorage xmlStorage;

    public RoomService() {
        this(XMLStorage.getInstance());
    }

    public RoomService(XMLStorage xmlStorage) {
        this.xmlStorage = xmlStorage;
    }

    public static RoomService getInstance() {
        if (instance == null) {
            synchronized (RoomService.class) {
                if (instance == null) {
                    instance = new RoomService();
                }
            }
        }
        return instance;
    }

    public List<Room> getAllRooms() {
        return xmlStorage.getAllRooms();
    }

    public List<Room> getActiveRooms() {
        return xmlStorage.getAllRooms().stream()
                .filter(Room::isActive)
                .collect(Collectors.toList());
    }

    public Optional<Room> getRoomById(String id) {
        return xmlStorage.getRoomById(id);
    }

    /**
     * Search and filter rooms based on category, guest capacity, price range, search query, and sort order.
     */
    public List<Room> filterRooms(String type, Double maxPrice, Integer minCapacity,
                                  String query, String sortBy,
                                  LocalDate checkIn, LocalDate checkOut) {
        ReservationService reservationService = ReservationService.getInstance();

        return xmlStorage.getAllRooms().stream()
                .filter(Room::isActive)
                .filter(r -> {
                    if (type == null || type.trim().isEmpty() || "ALL".equalsIgnoreCase(type.trim())) {
                        return true;
                    }
                    return r.getType().equalsIgnoreCase(type.trim());
                })
                .filter(r -> {
                    if (maxPrice == null || maxPrice <= 0) return true;
                    return r.getPricePerNight() <= maxPrice;
                })
                .filter(r -> {
                    if (minCapacity == null || minCapacity <= 0) return true;
                    return r.getCapacity() >= minCapacity;
                })
                .filter(r -> {
                    if (query == null || query.trim().isEmpty()) return true;
                    String q = query.trim().toLowerCase(Locale.ENGLISH);
                    boolean matchName = r.getName() != null && r.getName().toLowerCase(Locale.ENGLISH).contains(q);
                    boolean matchDesc = r.getDescription() != null && r.getDescription().toLowerCase(Locale.ENGLISH).contains(q);
                    boolean matchAmenity = r.getAmenities() != null && r.getAmenities().stream()
                            .anyMatch(a -> a.toLowerCase(Locale.ENGLISH).contains(q));
                    return matchName || matchDesc || matchAmenity;
                })
                .filter(r -> {
                    if (checkIn != null && checkOut != null) {
                        return reservationService.isRoomAvailable(r.getId(), checkIn, checkOut);
                    }
                    return true;
                })
                .sorted(getComparator(sortBy))
                .collect(Collectors.toList());
    }

    private Comparator<Room> getComparator(String sortBy) {
        if ("price_asc".equalsIgnoreCase(sortBy)) {
            return Comparator.comparingDouble(Room::getPricePerNight);
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            return Comparator.comparingDouble(Room::getPricePerNight).reversed();
        } else if ("capacity_desc".equalsIgnoreCase(sortBy)) {
            return Comparator.comparingInt(Room::getCapacity).reversed();
        } else if ("name_asc".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(Room::getName, String.CASE_INSENSITIVE_ORDER);
        }
        // Default: display order by room ID
        return Comparator.comparing(Room::getId);
    }

    /**
     * Saves or updates a room after strict validation.
     */
    public void saveRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null");
        }
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Room ID is required");
        }
        if (room.getName() == null || room.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Room name is required");
        }
        if (room.getPricePerNight() <= 0) {
            throw new IllegalArgumentException("Price per night must be greater than zero");
        }
        if (room.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be at least 1 guest");
        }

        // Validate type exists
        RoomType.fromString(room.getType());

        xmlStorage.saveRoom(room);
    }

    /**
     * Toggles the active status of a room.
     */
    public boolean toggleRoomStatus(String roomId) {
        Optional<Room> roomOpt = xmlStorage.getRoomById(roomId);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            room.setActive(!room.isActive());
            xmlStorage.saveRoom(room);
            return room.isActive();
        }
        throw new IllegalArgumentException("Room not found with ID: " + roomId);
    }
}
