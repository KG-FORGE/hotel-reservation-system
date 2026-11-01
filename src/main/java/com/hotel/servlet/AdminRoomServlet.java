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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller handling room catalog management, additions, modifications, and status toggles.
 */
@WebServlet(name = "AdminRoomServlet", urlPatterns = "/admin/rooms")
public class AdminRoomServlet extends HttpServlet {

    private final RoomService roomService = RoomService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("new".equalsIgnoreCase(action)) {
            request.setAttribute("isEdit", false);
            request.setAttribute("room", new Room());
            request.setAttribute("roomTypes", RoomType.values());
            request.setAttribute("pageTitle", "Add New Room | KG Grand Hotel Admin");
            request.getRequestDispatcher("/WEB-INF/views/admin-room-form.jsp").forward(request, response);
            return;
        }

        if ("edit".equalsIgnoreCase(action)) {
            String roomId = request.getParameter("id");
            Optional<Room> roomOpt = roomService.getRoomById(roomId);
            if (roomOpt.isPresent()) {
                request.setAttribute("isEdit", true);
                request.setAttribute("room", roomOpt.get());
                request.setAttribute("roomTypes", RoomType.values());
                request.setAttribute("pageTitle", "Edit Room #" + roomId + " | KG Grand Hotel Admin");
                request.getRequestDispatcher("/WEB-INF/views/admin-room-form.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("errorMessage", "Room not found: " + roomId);
            }
        }

        // Default: display all rooms
        List<Room> allRooms = roomService.getAllRooms();
        request.setAttribute("rooms", allRooms);

        String msg = request.getParameter("msg");
        if ("saved".equals(msg)) {
            request.setAttribute("successMessage", "Room record has been saved successfully.");
        } else if ("toggled".equals(msg)) {
            request.setAttribute("successMessage", "Room status has been updated.");
        }

        request.setAttribute("pageTitle", "Manage Rooms | KG Grand Hotel Admin");
        request.getRequestDispatcher("/WEB-INF/views/admin-rooms.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("toggle".equalsIgnoreCase(action)) {
            String roomId = request.getParameter("id");
            try {
                roomService.toggleRoomStatus(roomId);
                response.sendRedirect(request.getContextPath() + "/admin/rooms?msg=toggled");
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/admin/rooms?error=" + e.getMessage());
            }
            return;
        }

        // Save (Add or Update) Room
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("pricePerNight");
        String capacityStr = request.getParameter("capacity");
        String amenitiesStr = request.getParameter("amenities");
        String imageUrl = request.getParameter("imageUrl");
        boolean active = "true".equalsIgnoreCase(request.getParameter("active")) || "on".equalsIgnoreCase(request.getParameter("active"));
        boolean isEdit = "true".equalsIgnoreCase(request.getParameter("isEdit"));

        try {
            double price = Double.parseDouble(priceStr != null ? priceStr.trim() : "0");
            int capacity = Integer.parseInt(capacityStr != null ? capacityStr.trim() : "1");

            List<String> amenities = new ArrayList<>();
            if (amenitiesStr != null && !amenitiesStr.trim().isEmpty()) {
                amenities = Arrays.stream(amenitiesStr.split("[,;\\n]"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }

            Room room = new Room(id.trim(), name.trim(), type.trim(), description.trim(),
                    price, capacity, amenities, imageUrl != null ? imageUrl.trim() : "", active);

            roomService.saveRoom(room);
            response.sendRedirect(request.getContextPath() + "/admin/rooms?msg=saved");

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to save room: " + e.getMessage());
            request.setAttribute("isEdit", isEdit);
            Room draft = new Room();
            draft.setId(id);
            draft.setName(name);
            draft.setType(type);
            draft.setDescription(description);
            try { draft.setPricePerNight(Double.parseDouble(priceStr)); } catch (Exception ignored) {}
            try { draft.setCapacity(Integer.parseInt(capacityStr)); } catch (Exception ignored) {}
            draft.setImageUrl(imageUrl);
            draft.setActive(active);
            request.setAttribute("room", draft);
            request.setAttribute("roomTypes", RoomType.values());
            request.setAttribute("pageTitle", (isEdit ? "Edit Room" : "Add New Room") + " | KG Grand Hotel Admin");
            request.getRequestDispatcher("/WEB-INF/views/admin-room-form.jsp").forward(request, response);
        }
    }
}
