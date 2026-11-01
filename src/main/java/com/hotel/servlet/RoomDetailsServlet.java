package com.hotel.servlet;

import com.hotel.model.Room;
import com.hotel.service.ReservationService;
import com.hotel.service.RoomService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller for viewing detailed room specifications, amenities, and availability status.
 */
@WebServlet(name = "RoomDetailsServlet", urlPatterns = "/rooms/details")
public class RoomDetailsServlet extends HttpServlet {

    private final RoomService roomService = RoomService.getInstance();
    private final ReservationService reservationService = ReservationService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String roomId = request.getParameter("id");
        if (roomId == null || roomId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/rooms");
            return;
        }

        Optional<Room> roomOpt = roomService.getRoomById(roomId.trim());
        if (roomOpt.isEmpty()) {
            request.setAttribute("errorMessage", "Requested room could not be found.");
            request.getRequestDispatcher("/WEB-INF/views/error-404.jsp").forward(request, response);
            return;
        }

        Room room = roomOpt.get();
        request.setAttribute("room", room);

        // Optional prefilled dates for checking room availability
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        String guestsStr = request.getParameter("guests");

        if (checkInStr != null && !checkInStr.trim().isEmpty() &&
            checkOutStr != null && !checkOutStr.trim().isEmpty()) {
            try {
                LocalDate in = LocalDate.parse(checkInStr.trim());
                LocalDate out = LocalDate.parse(checkOutStr.trim());
                boolean isAvail = reservationService.isRoomAvailable(room.getId(), in, out);
                request.setAttribute("isAvailable", isAvail);
                request.setAttribute("checkedDates", true);
            } catch (Exception ignored) {}
        }

        request.setAttribute("checkIn", checkInStr);
        request.setAttribute("checkOut", checkOutStr);
        request.setAttribute("guests", guestsStr != null ? guestsStr : "2");
        request.setAttribute("pageTitle", room.getName() + " | KG Grand Hotel");

        request.getRequestDispatcher("/WEB-INF/views/room-details.jsp").forward(request, response);
    }
}
