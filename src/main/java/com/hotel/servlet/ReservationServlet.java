package com.hotel.servlet;

import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.service.ReservationService;
import com.hotel.service.RoomService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller managing room reservations with validation and Post/Redirect/Get workflow.
 */
@WebServlet(name = "ReservationServlet", urlPatterns = "/booking/new")
public class ReservationServlet extends HttpServlet {

    private final RoomService roomService = RoomService.getInstance();
    private final ReservationService reservationService = ReservationService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        populateFormContext(request);
        request.setAttribute("pageTitle", "Reserve Your Stay | KG Grand Hotel");
        request.getRequestDispatcher("/WEB-INF/views/reservation.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerName = request.getParameter("customerName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String roomId = request.getParameter("roomId");
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        String guestsStr = request.getParameter("guests");
        String specialRequests = request.getParameter("specialRequests");

        try {
            LocalDate checkIn = (checkInStr != null && !checkInStr.trim().isEmpty())
                    ? LocalDate.parse(checkInStr.trim()) : null;
            LocalDate checkOut = (checkOutStr != null && !checkOutStr.trim().isEmpty())
                    ? LocalDate.parse(checkOutStr.trim()) : null;

            int guests = 1;
            if (guestsStr != null && !guestsStr.trim().isEmpty()) {
                guests = Integer.parseInt(guestsStr.trim());
            }

            Reservation reservation = reservationService.createReservation(
                    customerName, email, phone, roomId, checkIn, checkOut, guests, specialRequests
            );

            // Post/Redirect/Get pattern to prevent duplicate submissions
            response.sendRedirect(request.getContextPath() + "/booking/confirmation?id=" +
                    URLEncoder.encode(reservation.getBookingId(), StandardCharsets.UTF_8));

        } catch (Exception e) {
            // Repopulate user input so they don't have to retype
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("param_customerName", customerName);
            request.setAttribute("param_email", email);
            request.setAttribute("param_phone", phone);
            request.setAttribute("param_roomId", roomId);
            request.setAttribute("param_checkIn", checkInStr);
            request.setAttribute("param_checkOut", checkOutStr);
            request.setAttribute("param_guests", guestsStr);
            request.setAttribute("param_specialRequests", specialRequests);

            populateFormContext(request);
            request.setAttribute("pageTitle", "Reserve Your Stay | KG Grand Hotel");
            request.getRequestDispatcher("/WEB-INF/views/reservation.jsp").forward(request, response);
        }
    }

    private void populateFormContext(HttpServletRequest request) {
        List<Room> activeRooms = roomService.getActiveRooms();
        request.setAttribute("activeRooms", activeRooms);

        String selectedRoomId = request.getParameter("roomId");
        if (selectedRoomId == null || selectedRoomId.trim().isEmpty()) {
            selectedRoomId = (String) request.getAttribute("param_roomId");
        }

        if (selectedRoomId != null && !selectedRoomId.trim().isEmpty()) {
            Optional<Room> selectedRoom = roomService.getRoomById(selectedRoomId.trim());
            selectedRoom.ifPresent(room -> request.setAttribute("selectedRoom", room));
        }

        // Set default dates if not already present (tomorrow and day after)
        if (request.getParameter("checkIn") != null) {
            request.setAttribute("defaultCheckIn", request.getParameter("checkIn"));
        } else if (request.getAttribute("param_checkIn") != null) {
            request.setAttribute("defaultCheckIn", request.getAttribute("param_checkIn"));
        } else {
            request.setAttribute("defaultCheckIn", LocalDate.now().plusDays(1).toString());
        }

        if (request.getParameter("checkOut") != null) {
            request.setAttribute("defaultCheckOut", request.getParameter("checkOut"));
        } else if (request.getAttribute("param_checkOut") != null) {
            request.setAttribute("defaultCheckOut", request.getAttribute("param_checkOut"));
        } else {
            request.setAttribute("defaultCheckOut", LocalDate.now().plusDays(3).toString());
        }

        String guests = request.getParameter("guests");
        if (guests == null) guests = (String) request.getAttribute("param_guests");
        if (guests == null) guests = "2";
        request.setAttribute("defaultGuests", guests);
    }
}
