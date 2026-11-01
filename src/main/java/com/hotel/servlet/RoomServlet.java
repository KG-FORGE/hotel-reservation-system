package com.hotel.servlet;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.service.RoomService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for browsing, searching, and filtering hotel rooms.
 */
@WebServlet(name = "RoomServlet", urlPatterns = "/rooms")
public class RoomServlet extends HttpServlet {

    private final RoomService roomService = RoomService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");
        String maxPriceStr = request.getParameter("maxPrice");
        String capacityStr = request.getParameter("capacity");
        String query = request.getParameter("query");
        String sortBy = request.getParameter("sortBy");
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");

        Double maxPrice = null;
        if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
            try {
                maxPrice = Double.parseDouble(maxPriceStr.trim());
            } catch (NumberFormatException ignored) {}
        }

        Integer capacity = null;
        if (capacityStr != null && !capacityStr.trim().isEmpty()) {
            try {
                capacity = Integer.parseInt(capacityStr.trim());
            } catch (NumberFormatException ignored) {}
        }

        LocalDate checkIn = null;
        LocalDate checkOut = null;
        if (checkInStr != null && !checkInStr.trim().isEmpty() &&
            checkOutStr != null && !checkOutStr.trim().isEmpty()) {
            try {
                checkIn = LocalDate.parse(checkInStr.trim());
                checkOut = LocalDate.parse(checkOutStr.trim());
            } catch (Exception ignored) {}
        }

        List<Room> rooms = roomService.filterRooms(type, maxPrice, capacity, query, sortBy, checkIn, checkOut);

        request.setAttribute("rooms", rooms);
        request.setAttribute("roomTypes", RoomType.values());
        request.setAttribute("selectedType", type);
        request.setAttribute("selectedMaxPrice", maxPrice);
        request.setAttribute("selectedCapacity", capacity);
        request.setAttribute("searchQuery", query);
        request.setAttribute("selectedSortBy", sortBy);
        request.setAttribute("checkIn", checkInStr);
        request.setAttribute("checkOut", checkOutStr);
        request.setAttribute("pageTitle", "Our Rooms & Suites | KG Grand Hotel");

        request.getRequestDispatcher("/WEB-INF/views/rooms.jsp").forward(request, response);
    }
}
