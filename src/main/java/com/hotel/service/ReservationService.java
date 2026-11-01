package com.hotel.service;

import com.hotel.config.SecurityUtil;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import com.hotel.model.Room;
import com.hotel.storage.XMLStorage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing room availability, reservation workflows,
 * date-overlap collision detection, and cancellation logic.
 */
public class ReservationService {

    private static volatile ReservationService instance;
    private final XMLStorage xmlStorage;
    private final Object bookingLock = new Object();

    public ReservationService() {
        this(XMLStorage.getInstance());
    }

    public ReservationService(XMLStorage xmlStorage) {
        this.xmlStorage = xmlStorage;
    }

    public static ReservationService getInstance() {
        if (instance == null) {
            synchronized (ReservationService.class) {
                if (instance == null) {
                    instance = new ReservationService();
                }
            }
        }
        return instance;
    }

    /**
     * Checks if a room is available for the given date range.
     * Date overlap rule:
     * Conflict exists if: newCheckIn < existingCheckOut AND newCheckOut > existingCheckIn
     * Check-out dates are exclusive.
     * Cancelled reservations are ignored.
     */
    public boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut) {
        return isRoomAvailable(roomId, checkIn, checkOut, null);
    }

    public boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut, String excludeBookingId) {
        if (roomId == null || checkIn == null || checkOut == null) {
            return false;
        }
        if (!checkOut.isAfter(checkIn)) {
            return false;
        }

        List<Reservation> reservations = xmlStorage.getAllReservations();
        for (Reservation res : reservations) {
            // Ignore cancelled bookings
            if (res.getStatus() == ReservationStatus.CANCELLED) {
                continue;
            }
            // Ignore currently edited booking if any
            if (excludeBookingId != null && res.getBookingId().equalsIgnoreCase(excludeBookingId)) {
                continue;
            }
            // Check only for the specified room
            if (!res.getRoomId().equalsIgnoreCase(roomId)) {
                continue;
            }

            LocalDate existingIn = res.getCheckIn();
            LocalDate existingOut = res.getCheckOut();

            // Overlap condition: (newCheckIn < existingCheckOut) AND (newCheckOut > existingCheckIn)
            boolean overlaps = checkIn.isBefore(existingOut) && checkOut.isAfter(existingIn);
            if (overlaps) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new hotel reservation atomically with synchronized concurrency protection.
     */
    public Reservation createReservation(String customerName, String email, String phone,
                                         String roomId, LocalDate checkIn, LocalDate checkOut,
                                         int guests, String specialRequests) {

        // 1. Server-side validation
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (!SecurityUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("A valid email address is required.");
        }
        if (!SecurityUtil.isValidPhone(phone)) {
            throw new IllegalArgumentException("A valid contact phone number (10-15 digits) is required.");
        }
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a room to reserve.");
        }
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Both check-in and check-out dates are required.");
        }

        LocalDate today = LocalDate.now();
        if (checkIn.isBefore(today)) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be strictly after the check-in date.");
        }
        if (guests <= 0) {
            throw new IllegalArgumentException("Number of guests must be at least 1.");
        }

        // Room verification
        Optional<Room> roomOpt = xmlStorage.getRoomById(roomId);
        if (roomOpt.isEmpty()) {
            throw new IllegalArgumentException("Selected room could not be found.");
        }
        Room room = roomOpt.get();
        if (!room.isActive()) {
            throw new IllegalArgumentException("This room is currently inactive and cannot accept new reservations.");
        }
        if (guests > room.getCapacity()) {
            throw new IllegalArgumentException(String.format("Guest count (%d) exceeds room capacity (%d).",
                    guests, room.getCapacity()));
        }

        // 2. Critical Section: Synchronized check-and-book to prevent race conditions
        synchronized (bookingLock) {
            if (!isRoomAvailable(roomId, checkIn, checkOut)) {
                throw new IllegalStateException("Selected room is no longer available for the specified dates. " +
                        "Please choose alternate dates or another room category.");
            }

            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            if (nights <= 0) nights = 1;

            double pricePerNight = room.getPricePerNight();
            double totalPrice = nights * pricePerNight;

            String bookingId = xmlStorage.generateNextBookingId();

            Reservation reservation = new Reservation(
                    bookingId,
                    customerName.trim(),
                    email.trim().toLowerCase(Locale.ENGLISH),
                    phone.trim(),
                    room.getId(),
                    checkIn,
                    checkOut,
                    guests,
                    pricePerNight,
                    totalPrice,
                    specialRequests != null ? specialRequests.trim() : "",
                    ReservationStatus.CONFIRMED,
                    LocalDateTime.now()
            );

            xmlStorage.saveReservation(reservation);
            reservation.setRoom(room);

            return reservation;
        }
    }

    /**
     * Retrieves a reservation by booking ID and matching customer email.
     */
    public Optional<Reservation> lookupReservation(String bookingId, String email) {
        if (bookingId == null || email == null) {
            return Optional.empty();
        }

        Optional<Reservation> resOpt = xmlStorage.getReservationById(bookingId.trim());
        if (resOpt.isPresent()) {
            Reservation res = resOpt.get();
            if (res.getEmail().equalsIgnoreCase(email.trim())) {
                xmlStorage.getRoomById(res.getRoomId()).ifPresent(res::setRoom);
                return Optional.of(res);
            }
        }
        return Optional.empty();
    }

    /**
     * Cancels an existing reservation after verifying customer email ownership.
     */
    public Reservation cancelReservation(String bookingId, String email) {
        if (bookingId == null || email == null) {
            throw new IllegalArgumentException("Booking reference and customer email are required for cancellation.");
        }

        synchronized (bookingLock) {
            Optional<Reservation> resOpt = xmlStorage.getReservationById(bookingId.trim());
            if (resOpt.isEmpty()) {
                throw new IllegalArgumentException("No booking found with reference ID: " + bookingId);
            }

            Reservation res = resOpt.get();
            if (!res.getEmail().equalsIgnoreCase(email.trim())) {
                throw new SecurityException("Provided email does not match the booking record.");
            }

            if (res.getStatus() == ReservationStatus.CANCELLED) {
                throw new IllegalStateException("This reservation has already been cancelled.");
            }

            if (!res.isEligibleForCancellation()) {
                throw new IllegalStateException("This reservation is no longer eligible for cancellation as the stay has commenced.");
            }

            res.setStatus(ReservationStatus.CANCELLED);
            xmlStorage.saveReservation(res);

            xmlStorage.getRoomById(res.getRoomId()).ifPresent(res::setRoom);
            return res;
        }
    }

    /**
     * Admin override cancellation.
     */
    public Reservation adminCancelReservation(String bookingId) {
        synchronized (bookingLock) {
            Optional<Reservation> resOpt = xmlStorage.getReservationById(bookingId.trim());
            if (resOpt.isEmpty()) {
                throw new IllegalArgumentException("No reservation found with reference: " + bookingId);
            }

            Reservation res = resOpt.get();
            if (res.getStatus() == ReservationStatus.CANCELLED) {
                throw new IllegalStateException("Reservation is already cancelled.");
            }

            res.setStatus(ReservationStatus.CANCELLED);
            xmlStorage.saveReservation(res);
            xmlStorage.getRoomById(res.getRoomId()).ifPresent(res::setRoom);
            return res;
        }
    }

    /**
     * Admin permanent deletion of a reservation record from XML storage.
     * Synchronized to ensure thread-safety with reservation creation and cancellation.
     */
    public boolean deleteReservation(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking reference is required for deletion.");
        }
        synchronized (bookingLock) {
            Optional<Reservation> resOpt = xmlStorage.getReservationById(bookingId.trim());
            if (resOpt.isEmpty()) {
                throw new IllegalArgumentException("No reservation found with reference ID: " + bookingId.trim());
            }
            return xmlStorage.deleteReservation(bookingId.trim());
        }
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> list = xmlStorage.getAllReservations();
        Map<String, Room> roomMap = xmlStorage.getAllRooms().stream()
                .collect(Collectors.toMap(Room::getId, r -> r, (a, b) -> a));

        list.forEach(res -> res.setRoom(roomMap.get(res.getRoomId())));
        list.sort(Comparator.comparing(Reservation::getCreatedAt).reversed());
        return list;
    }

    public Optional<Reservation> getReservationById(String bookingId) {
        Optional<Reservation> opt = xmlStorage.getReservationById(bookingId);
        opt.ifPresent(r -> xmlStorage.getRoomById(r.getRoomId()).ifPresent(r::setRoom));
        return opt;
    }

    /**
     * Filters reservations for the admin management view.
     */
    public List<Reservation> filterReservations(String status, String searchId, LocalDate filterDate) {
        return getAllReservations().stream()
                .filter(r -> {
                    if (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status.trim())) {
                        return true;
                    }
                    return r.getStatus().name().equalsIgnoreCase(status.trim());
                })
                .filter(r -> {
                    if (searchId == null || searchId.trim().isEmpty()) return true;
                    String q = searchId.trim().toLowerCase(Locale.ENGLISH);
                    return r.getBookingId().toLowerCase(Locale.ENGLISH).contains(q)
                            || r.getCustomerName().toLowerCase(Locale.ENGLISH).contains(q)
                            || r.getEmail().toLowerCase(Locale.ENGLISH).contains(q);
                })
                .filter(r -> {
                    if (filterDate == null) return true;
                    return !filterDate.isBefore(r.getCheckIn()) && !filterDate.isAfter(r.getCheckOut());
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculates high-level statistics for the admin dashboard.
     */
    public Map<String, Object> getDashboardStatistics() {
        List<Reservation> reservations = xmlStorage.getAllReservations();
        List<Room> rooms = xmlStorage.getAllRooms();

        long totalRooms = rooms.size();
        long activeRooms = rooms.stream().filter(Room::isActive).count();
        long totalReservations = reservations.size();

        long confirmedCount = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();

        long cancelledCount = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                .count();

        LocalDate today = LocalDate.now();
        long upcomingCheckIns = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .filter(r -> !r.getCheckIn().isBefore(today))
                .count();

        double totalRevenue = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .mapToDouble(Reservation::getTotalPrice)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRooms", totalRooms);
        stats.put("activeRooms", activeRooms);
        stats.put("totalReservations", totalReservations);
        stats.put("confirmedReservations", confirmedCount);
        stats.put("cancelledReservations", cancelledCount);
        stats.put("upcomingCheckIns", upcomingCheckIns);
        stats.put("totalRevenue", totalRevenue);

        // Indian Currency formatting for revenue
        Locale indiaLocale = new Locale("en", "IN");
        java.text.NumberFormat formatter = java.text.NumberFormat.getCurrencyInstance(indiaLocale);
        stats.put("formattedRevenue", formatter.format(totalRevenue));

        return stats;
    }
}
