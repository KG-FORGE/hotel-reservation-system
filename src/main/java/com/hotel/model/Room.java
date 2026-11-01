package com.hotel.model;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Domain model representing a hotel room.
 */
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String type; // Key corresponding to RoomType (e.g. "DELUXE")
    private String description;
    private double pricePerNight;
    private int capacity;
    private List<String> amenities = new ArrayList<>();
    private String imageUrl;
    private boolean active = true;

    public Room() {
    }

    public Room(String id, String name, String type, String description,
                double pricePerNight, int capacity, List<String> amenities,
                String imageUrl, boolean active) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        if (amenities != null) {
            this.amenities = new ArrayList<>(amenities);
        }
        this.imageUrl = imageUrl;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeDisplayName() {
        return RoomType.fromString(type).getDisplayName();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities != null ? new ArrayList<>(amenities) : new ArrayList<>();
    }

    public void addAmenity(String amenity) {
        if (amenity != null && !amenity.trim().isEmpty() && !this.amenities.contains(amenity.trim())) {
            this.amenities.add(amenity.trim());
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFormattedPrice() {
        Locale indiaLocale = new Locale("en", "IN");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(indiaLocale);
        return formatter.format(this.pricePerNight);
    }

    public String getAmenitiesDisplay() {
        if (amenities == null || amenities.isEmpty()) {
            return "Standard amenities";
        }
        return String.join(", ", amenities);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", pricePerNight=" + pricePerNight +
                ", capacity=" + capacity +
                ", active=" + active +
                '}';
    }
}
