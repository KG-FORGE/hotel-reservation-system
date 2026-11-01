package com.hotel.servlet;

import com.hotel.model.Reservation;
import com.hotel.model.ReservationStatus;
import com.hotel.service.ReservationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for viewing, filtering, searching, and managing hotel reservations.
 */
@WebServlet(name = "AdminReservationServlet", urlPatterns = "/admin/reservations")
public class AdminReservationServlet extends HttpServlet {

    private final ReservationService reservationService = ReservationService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String status = request.getParameter("status");
        String searchId = request.getParameter("searchId");
        String dateStr = request.getParameter("date");

        LocalDate filterDate = null;
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                filterDate = LocalDate.parse(dateStr.trim());
            } catch (Exception ignored) {}
        }

        List<Reservation> reservations = reservationService.filterReservations(status, searchId, filterDate);

        request.setAttribute("reservations", reservations);
        request.setAttribute("statuses", ReservationStatus.values());
        request.setAttribute("selectedStatus", status);
        request.setAttribute("searchId", searchId);
        request.setAttribute("filterDate", dateStr);

        String msg = request.getParameter("msg");
        if ("cancelled".equals(msg)) {
            request.setAttribute("successMessage", "Reservation status successfully updated to Cancelled.");
        }

        request.setAttribute("pageTitle", "Manage Reservations | KG Grand Hotel Admin");
        request.getRequestDispatcher("/WEB-INF/views/admin-reservations.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String bookingId = request.getParameter("bookingId");

        if ("cancel".equalsIgnoreCase(action) && bookingId != null) {
            try {
                reservationService.adminCancelReservation(bookingId);
                response.sendRedirect(request.getContextPath() + "/admin/reservations?msg=cancelled");
                return;
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/admin/reservations?error=" + e.getMessage());
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/reservations");
    }
}
