package com.hotel.model;

/**
 * Status of a hotel reservation.
 */
public enum ReservationStatus {
    CONFIRMED("Confirmed", "badge-success"),
    CANCELLED("Cancelled", "badge-danger");

    private final String displayName;
    private final String cssClass;

    ReservationStatus(String displayName, String cssClass) {
        this.displayName = displayName;
        this.cssClass = cssClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCssClass() {
        return cssClass;
    }

    public static ReservationStatus fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return CONFIRMED;
        }
        for (ReservationStatus status : ReservationStatus.values()) {
            if (status.name().equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        return CONFIRMED;
    }
}
