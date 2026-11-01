package com.hotel.model;

/**
 * Standard categories of hotel rooms.
 */
public enum RoomType {
    STANDARD("Standard Room", "Comfortable and cozy room suitable for solo travelers or couples."),
    DELUXE("Deluxe Room", "Spacious room with modern amenities and scenic city or garden views."),
    SUPERIOR("Superior Room", "Enhanced comfort with premium bedding, work desk, and upgraded bathroom."),
    EXECUTIVE_SUITE("Executive Suite", "Luxurious suite featuring a separate living lounge and executive privileges."),
    FAMILY_SUITE("Family Suite", "Generous multi-bed suite tailored for family relaxation and privacy."),
    PRESIDENTIAL_SUITE("Presidential Suite", "The ultimate luxury with panoramic views, private dining, and bespoke service.");

    private final String displayName;
    private final String defaultDescription;

    RoomType(String displayName, String defaultDescription) {
        this.displayName = displayName;
        this.defaultDescription = defaultDescription;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }

    public static RoomType fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return STANDARD;
        }
        for (RoomType type : RoomType.values()) {
            if (type.name().equalsIgnoreCase(text.trim()) || type.displayName.equalsIgnoreCase(text.trim())) {
                return type;
            }
        }
        return STANDARD;
    }
}
