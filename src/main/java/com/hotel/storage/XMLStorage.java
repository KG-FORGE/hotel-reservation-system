package com.hotel.storage;

import com.hotel.config.AppConfig;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import com.hotel.model.Room;
import com.hotel.model.RoomType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-safe XML persistence engine using built-in Java DOM APIs.
 * Guarantees atomic writes, proper synchronization, and robust schema handling.
 */
public class XMLStorage {

    private static final Logger LOGGER = Logger.getLogger(XMLStorage.class.getName());
    private static volatile XMLStorage instance;

    private final ReentrantReadWriteLock roomsLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock reservationsLock = new ReentrantReadWriteLock();

    private XMLStorage() {
        initStorage();
    }

    public static XMLStorage getInstance() {
        if (instance == null) {
            synchronized (XMLStorage.class) {
                if (instance == null) {
                    instance = new XMLStorage();
                }
            }
        }
        return instance;
    }

    /**
     * Initializes storage files and default seed data if absent.
     */
    public synchronized void initStorage() {
        try {
            File roomsFile = AppConfig.getRoomsFile();
            if (!roomsFile.exists() || roomsFile.length() == 0) {
                LOGGER.info("Rooms XML file missing or empty. Seeding default room catalog...");
                seedDefaultRooms(roomsFile);
            }

            File reservationsFile = AppConfig.getReservationsFile();
            if (!reservationsFile.exists() || reservationsFile.length() == 0) {
                LOGGER.info("Reservations XML file missing or empty. Initializing empty storage...");
                Document doc = XMLUtil.createNewDocument();
                Element root = doc.createElement("reservations");
                doc.appendChild(root);
                XMLUtil.writeDocumentAtomically(doc, reservationsFile);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize XML storage files", e);
            throw new RuntimeException("XML Storage initialization failed", e);
        }
    }

    private void seedDefaultRooms(File roomsFile) throws Exception {
        Document doc = XMLUtil.createNewDocument();
        Element root = doc.createElement("rooms");
        doc.appendChild(root);

        List<Room> defaults = List.of(
                new Room("R001", "Standard Room", RoomType.STANDARD.name(),
                        "A thoughtfully designed, peaceful retreat featuring a plush queen-size bed, high-speed Wi-Fi, smart television, and luxury en-suite bathroom.",
                        1500.00, 2,
                        List.of("Free Wi-Fi", "Air Conditioning", "Smart TV", "Electric Kettle", "En-suite Bathroom"),
                        "https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1200&q=80",
                        true),

                new Room("R002", "Deluxe Room", RoomType.DELUXE.name(),
                        "An expansive sanctuary adorned with elegant furnishings, private balcony with scenic vistas, work desk, and dedicated climate control.",
                        2500.00, 2,
                        List.of("Free Wi-Fi", "King Bed", "Private Balcony", "Air Conditioning", "Mini Fridge", "Work Station"),
                        "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=1200&q=80",
                        true),

                new Room("R003", "Superior Room", RoomType.SUPERIOR.name(),
                        "Sophisticated comfort featuring handcrafted wooden decor, premium Italian linens, walk-in rain shower, and dedicated espresso bar.",
                        3200.00, 3,
                        List.of("Free High-Speed Wi-Fi", "Garden View", "Rain Shower", "Espresso Machine", "Electronic Safe", "Bathrobes"),
                        "https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=1200&q=80",
                        true),

                new Room("R004", "Executive Suite", RoomType.EXECUTIVE_SUITE.name(),
                        "Opulent suite tailored for discerning travelers. Features a separate private living salon, master bedroom, deep soaking jacuzzi tub, and lounge privileges.",
                        4500.00, 4,
                        List.of("Living Salon", "Executive Lounge Access", "Jacuzzi Bath", "High-Speed Wi-Fi", "Nespresso Bar", "Sound System"),
                        "https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1200&q=80",
                        true),

                new Room("R005", "Family Suite", RoomType.FAMILY_SUITE.name(),
                        "Designed specifically for families and groups, featuring two interconnected bedrooms, kids entertainment zone, kitchenette, and dining table.",
                        5000.00, 5,
                        List.of("Two Master Bedrooms", "Kids Play Area", "Kitchenette", "2 Smart 4K TVs", "Dining Area", "2 Full Bathrooms"),
                        "https://images.unsplash.com/photo-1595576508898-0ad5c879a061?auto=format&fit=crop&w=1200&q=80",
                        true),

                new Room("R006", "Presidential Suite", RoomType.PRESIDENTIAL_SUITE.name(),
                        "The pinnacle of luxury hospitality. Top-floor panoramic views, private terrace with infinity plunge pool, 24/7 dedicated butler, and bespoke dining.",
                        8000.00, 6,
                        List.of("Panoramic Penthouse Views", "Private Butler Service", "Private Plunge Pool", "Terrace Dining", "Master Whirlpool", "Chauffeur Service"),
                        "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?auto=format&fit=crop&w=1200&q=80",
                        true)
        );

        for (Room r : defaults) {
            appendRoomElement(doc, root, r);
        }

        XMLUtil.writeDocumentAtomically(doc, roomsFile);
    }

    // ==========================================
    // ROOM PERSISTENCE METHODS
    // ==========================================

    public List<Room> getAllRooms() {
        roomsLock.readLock().lock();
        try {
            List<Room> rooms = new ArrayList<>();
            File file = AppConfig.getRoomsFile();
            if (!file.exists() || file.length() == 0) {
                return rooms;
            }

            Document doc = XMLUtil.parseFile(file);
            NodeList roomNodes = doc.getElementsByTagName("room");
            for (int i = 0; i < roomNodes.getLength(); i++) {
                Element el = (Element) roomNodes.item(i);
                rooms.add(parseRoomElement(el));
            }
            return rooms;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error reading rooms from XML", e);
            throw new RuntimeException("Could not read rooms from storage", e);
        } finally {
            roomsLock.readLock().unlock();
        }
    }

    public Optional<Room> getRoomById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return getAllRooms().stream()
                .filter(r -> r.getId().equalsIgnoreCase(id.trim()))
                .findFirst();
    }

    public void saveRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Room and room ID cannot be null or empty");
        }

        roomsLock.writeLock().lock();
        try {
            File file = AppConfig.getRoomsFile();
            Document doc;
            Element root;

            if (file.exists() && file.length() > 0) {
                doc = XMLUtil.parseFile(file);
                root = doc.getDocumentElement();
            } else {
                doc = XMLUtil.createNewDocument();
                root = doc.createElement("rooms");
                doc.appendChild(root);
            }

            NodeList roomNodes = root.getElementsByTagName("room");
            Element existingElement = null;

            for (int i = 0; i < roomNodes.getLength(); i++) {
                Element el = (Element) roomNodes.item(i);
                String id = XMLUtil.getChildText(el, "id");
                if (id.equalsIgnoreCase(room.getId().trim())) {
                    existingElement = el;
                    break;
                }
            }

            if (existingElement != null) {
                root.removeChild(existingElement);
            }

            appendRoomElement(doc, root, room);
            XMLUtil.writeDocumentAtomically(doc, file);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to persist room: " + room.getId(), e);
            throw new RuntimeException("Failed to save room to XML storage", e);
        } finally {
            roomsLock.writeLock().unlock();
        }
    }

    private Room parseRoomElement(Element el) {
        String id = XMLUtil.getChildText(el, "id");
        String name = XMLUtil.getChildText(el, "name");
        String type = XMLUtil.getChildText(el, "type");
        String description = XMLUtil.getChildText(el, "description");
        double price = 0.0;
        try {
            price = Double.parseDouble(XMLUtil.getChildText(el, "pricePerNight"));
        } catch (NumberFormatException ignored) {}

        int capacity = 2;
        try {
            capacity = Integer.parseInt(XMLUtil.getChildText(el, "capacity"));
        } catch (NumberFormatException ignored) {}

        List<String> amenities = XMLUtil.getChildTextList(el, "amenities", "amenity");
        String imageUrl = XMLUtil.getChildText(el, "imageUrl");
        boolean active = Boolean.parseBoolean(XMLUtil.getChildText(el, "active"));

        return new Room(id, name, type, description, price, capacity, amenities, imageUrl, active);
    }

    private void appendRoomElement(Document doc, Element root, Room room) {
        Element roomEl = doc.createElement("room");

        XMLUtil.appendChildElement(doc, roomEl, "id", room.getId());
        XMLUtil.appendChildElement(doc, roomEl, "name", room.getName());
        XMLUtil.appendChildElement(doc, roomEl, "type", room.getType());
        XMLUtil.appendChildElement(doc, roomEl, "description", room.getDescription());
        XMLUtil.appendChildElement(doc, roomEl, "pricePerNight", String.format(java.util.Locale.US, "%.2f", room.getPricePerNight()));
        XMLUtil.appendChildElement(doc, roomEl, "capacity", String.valueOf(room.getCapacity()));

        Element amenitiesEl = doc.createElement("amenities");
        if (room.getAmenities() != null) {
            for (String amenity : room.getAmenities()) {
                XMLUtil.appendChildElement(doc, amenitiesEl, "amenity", amenity);
            }
        }
        roomEl.appendChild(amenitiesEl);

        XMLUtil.appendChildElement(doc, roomEl, "imageUrl", room.getImageUrl());
        XMLUtil.appendChildElement(doc, roomEl, "active", String.valueOf(room.isActive()));

        root.appendChild(roomEl);
    }

    // ==========================================
    // RESERVATION PERSISTENCE METHODS
    // ==========================================

    public List<Reservation> getAllReservations() {
        reservationsLock.readLock().lock();
        try {
            List<Reservation> reservations = new ArrayList<>();
            File file = AppConfig.getReservationsFile();
            if (!file.exists() || file.length() == 0) {
                return reservations;
            }

            Document doc = XMLUtil.parseFile(file);
            NodeList resNodes = doc.getElementsByTagName("reservation");
            for (int i = 0; i < resNodes.getLength(); i++) {
                Element el = (Element) resNodes.item(i);
                reservations.add(parseReservationElement(el));
            }
            return reservations;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error reading reservations from XML", e);
            throw new RuntimeException("Could not read reservations from storage", e);
        } finally {
            reservationsLock.readLock().unlock();
        }
    }

    public Optional<Reservation> getReservationById(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            return Optional.empty();
        }
        return getAllReservations().stream()
                .filter(r -> r.getBookingId().equalsIgnoreCase(bookingId.trim()))
                .findFirst();
    }

    public void saveReservation(Reservation reservation) {
        if (reservation == null || reservation.getBookingId() == null) {
            throw new IllegalArgumentException("Reservation and booking ID cannot be null");
        }

        reservationsLock.writeLock().lock();
        try {
            File file = AppConfig.getReservationsFile();
            Document doc;
            Element root;

            if (file.exists() && file.length() > 0) {
                doc = XMLUtil.parseFile(file);
                root = doc.getDocumentElement();
            } else {
                doc = XMLUtil.createNewDocument();
                root = doc.createElement("reservations");
                doc.appendChild(root);
            }

            NodeList resNodes = root.getElementsByTagName("reservation");
            Element existingElement = null;

            for (int i = 0; i < resNodes.getLength(); i++) {
                Element el = (Element) resNodes.item(i);
                String bId = XMLUtil.getChildText(el, "bookingId");
                if (bId.equalsIgnoreCase(reservation.getBookingId().trim())) {
                    existingElement = el;
                    break;
                }
            }

            if (existingElement != null) {
                root.removeChild(existingElement);
            }

            appendReservationElement(doc, root, reservation);
            XMLUtil.writeDocumentAtomically(doc, file);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to persist reservation: " + reservation.getBookingId(), e);
            throw new RuntimeException("Failed to save reservation to XML storage", e);
        } finally {
            reservationsLock.writeLock().unlock();
        }
    }

    public synchronized String generateNextBookingId() {
        reservationsLock.readLock().lock();
        try {
            List<Reservation> list = getAllReservations();
            long maxNum = 100000;
            for (Reservation r : list) {
                String id = r.getBookingId();
                if (id != null && id.startsWith("BK")) {
                    try {
                        long num = Long.parseLong(id.substring(2));
                        if (num > maxNum) {
                            maxNum = num;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            return "BK" + (maxNum + 1);
        } finally {
            reservationsLock.readLock().unlock();
        }
    }

    private Reservation parseReservationElement(Element el) {
        String bookingId = XMLUtil.getChildText(el, "bookingId");
        String customerName = XMLUtil.getChildText(el, "customerName");
        String email = XMLUtil.getChildText(el, "email");
        String phone = XMLUtil.getChildText(el, "phone");
        String roomId = XMLUtil.getChildText(el, "roomId");

        LocalDate checkIn = LocalDate.parse(XMLUtil.getChildText(el, "checkIn"));
        LocalDate checkOut = LocalDate.parse(XMLUtil.getChildText(el, "checkOut"));

        int guests = Integer.parseInt(XMLUtil.getChildText(el, "guests"));
        double pricePerNight = Double.parseDouble(XMLUtil.getChildText(el, "pricePerNight"));
        double totalPrice = Double.parseDouble(XMLUtil.getChildText(el, "totalPrice"));
        String specialRequests = XMLUtil.getChildText(el, "specialRequests");
        ReservationStatus status = ReservationStatus.fromString(XMLUtil.getChildText(el, "status"));

        LocalDateTime createdAt = LocalDateTime.now();
        String createdStr = XMLUtil.getChildText(el, "createdAt");
        if (!createdStr.isEmpty()) {
            try {
                createdAt = LocalDateTime.parse(createdStr);
            } catch (Exception ignored) {}
        }

        return new Reservation(bookingId, customerName, email, phone, roomId,
                checkIn, checkOut, guests, pricePerNight, totalPrice,
                specialRequests, status, createdAt);
    }

    private void appendReservationElement(Document doc, Element root, Reservation res) {
        Element resEl = doc.createElement("reservation");

        XMLUtil.appendChildElement(doc, resEl, "bookingId", res.getBookingId());
        XMLUtil.appendChildElement(doc, resEl, "customerName", res.getCustomerName());
        XMLUtil.appendChildElement(doc, resEl, "email", res.getEmail());
        XMLUtil.appendChildElement(doc, resEl, "phone", res.getPhone());
        XMLUtil.appendChildElement(doc, resEl, "roomId", res.getRoomId());
        XMLUtil.appendChildElement(doc, resEl, "checkIn", res.getCheckIn().toString());
        XMLUtil.appendChildElement(doc, resEl, "checkOut", res.getCheckOut().toString());
        XMLUtil.appendChildElement(doc, resEl, "guests", String.valueOf(res.getGuests()));
        XMLUtil.appendChildElement(doc, resEl, "pricePerNight", String.format(java.util.Locale.US, "%.2f", res.getPricePerNight()));
        XMLUtil.appendChildElement(doc, resEl, "totalPrice", String.format(java.util.Locale.US, "%.2f", res.getTotalPrice()));
        XMLUtil.appendChildElement(doc, resEl, "specialRequests", res.getSpecialRequests() != null ? res.getSpecialRequests() : "");
        XMLUtil.appendChildElement(doc, resEl, "status", res.getStatus().name());
        XMLUtil.appendChildElement(doc, resEl, "createdAt", res.getCreatedAt().toString());

        root.appendChild(resEl);
    }

    /**
     * Resets repository data to seed state (useful in testing).
     */
    public synchronized void resetToDefaults() {
        roomsLock.writeLock().lock();
        reservationsLock.writeLock().lock();
        try {
            File rFile = AppConfig.getRoomsFile();
            if (rFile.exists()) rFile.delete();
            File bFile = AppConfig.getReservationsFile();
            if (bFile.exists()) bFile.delete();
            initStorage();
        } finally {
            reservationsLock.writeLock().unlock();
            roomsLock.writeLock().unlock();
        }
    }
}
