package com.hotel.servlet;

import com.hotel.model.Reservation;
import com.hotel.service.ReservationService;
import com.hotel.service.RoomService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for the administrator analytics dashboard.
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = "/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final ReservationService reservationService = ReservationService.getInstance();
    private final RoomService roomService = RoomService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, Object> stats = reservationService.getDashboardStatistics();
        List<Reservation> recentReservations = reservationService.getAllReservations().stream()
                .limit(8)
                .collect(Collectors.toList());

        request.setAttribute("stats", stats);
        request.setAttribute("recentReservations", recentReservations);
        request.setAttribute("roomsCount", roomService.getAllRooms().size());
        request.setAttribute("pageTitle", "Admin Dashboard | KG Grand Hotel");

        request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
    }
}
