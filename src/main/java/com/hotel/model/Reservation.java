package com.hotel.model;

import java.io.Serializable;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;

/**
 * Domain model representing a customer reservation.
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

    private String bookingId;
    private String customerName;
    private String email;
    private String phone;
    private String roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int guests;
    private double pricePerNight;
    private double totalPrice;
    private String specialRequests;
    private ReservationStatus status = ReservationStatus.CONFIRMED;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Transient reference to room details for easy JSP rendering
    private transient Room room;

    public Reservation() {
    }

    public Reservation(String bookingId, String customerName, String email, String phone,
                       String roomId, LocalDate checkIn, LocalDate checkOut, int guests,
                       double pricePerNight, double totalPrice, String specialRequests,
                       ReservationStatus status, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guests = guests;
        this.pricePerNight = pricePerNight;
        this.totalPrice = totalPrice;
        this.specialRequests = specialRequests;
        this.status = status != null ? status : ReservationStatus.CONFIRMED;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public long getNumberOfNights() {
        if (checkIn != null && checkOut != null) {
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            return nights > 0 ? nights : 1;
        }
        return 0;
    }

    public String getFormattedPricePerNight() {
        Locale indiaLocale = new Locale("en", "IN");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(indiaLocale);
        return formatter.format(this.pricePerNight);
    }

    public String getFormattedTotalPrice() {
        Locale indiaLocale = new Locale("en", "IN");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(indiaLocale);
        return formatter.format(this.totalPrice);
    }

    public String getFormattedCheckIn() {
        return checkIn != null ? checkIn.format(DATE_FORMATTER) : "";
    }

    public String getFormattedCheckOut() {
        return checkOut != null ? checkOut.format(DATE_FORMATTER) : "";
    }

    public String getFormattedCreatedAt() {
        return createdAt != null ? createdAt.format(DATETIME_FORMATTER) : "";
    }

    public boolean isCancelled() {
        return this.status == ReservationStatus.CANCELLED;
    }

    public boolean isConfirmed() {
        return this.status == ReservationStatus.CONFIRMED;
    }

    public boolean isEligibleForCancellation() {
        if (isCancelled()) {
            return false;
        }
        // Can be cancelled if check-out has not passed
        LocalDate today = LocalDate.now();
        return checkIn != null && !today.isAfter(checkIn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(bookingId, that.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "bookingId='" + bookingId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", roomId='" + roomId + '\'' +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                '}';
    }
}
