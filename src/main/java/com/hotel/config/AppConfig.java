package com.hotel.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Centralized application configuration.
 * Handles storage path resolution, security defaults, and environment variables.
 */
public final class AppConfig {

    public static final String SYS_PROP_DATA_DIR = "hotel.data.dir";
    public static final String ENV_DATA_DIR = "HOTEL_DATA_DIR";

    public static final String ENV_ADMIN_USER = "HOTEL_ADMIN_USER";
    public static final String ENV_ADMIN_PASS = "HOTEL_ADMIN_PASS";

    public static final String DEFAULT_ADMIN_USER = "admin";
    public static final String DEFAULT_ADMIN_PASS = "Admin@Hotel2026";

    private static volatile Path dataDirectory;

    private AppConfig() {
        // Utility class
    }

    /**
     * Resolves and ensures the data directory exists.
     * Prevents path traversal and validates write permissions.
     */
    public static synchronized Path getDataDirectory() {
        if (dataDirectory != null && Files.exists(dataDirectory)) {
            return dataDirectory;
        }

        String pathStr = System.getProperty(SYS_PROP_DATA_DIR);
        if (pathStr == null || pathStr.trim().isEmpty()) {
            pathStr = System.getenv(ENV_DATA_DIR);
        }

        Path resolvedPath;
        if (pathStr != null && !pathStr.trim().isEmpty()) {
            resolvedPath = Paths.get(pathStr.trim()).toAbsolutePath().normalize();
        } else {
            // Default to ./data relative to current working directory
            resolvedPath = Paths.get("data").toAbsolutePath().normalize();
        }

        try {
            if (!Files.exists(resolvedPath)) {
                Files.createDirectories(resolvedPath);
            }
        } catch (IOException e) {
            // Fallback to user home directory if current dir isn't writable
            resolvedPath = Paths.get(System.getProperty("user.home"), ".hotel-reservation-system", "data")
                    .toAbsolutePath().normalize();
            try {
                Files.createDirectories(resolvedPath);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to initialize hotel data directory: " + resolvedPath, ex);
            }
        }

        dataDirectory = resolvedPath;
        return dataDirectory;
    }

    /**
     * Explicitly sets data directory (useful for unit testing or Tomcat context init).
     */
    public static synchronized void setDataDirectory(Path dir) {
        if (dir != null) {
            dataDirectory = dir.toAbsolutePath().normalize();
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                throw new RuntimeException("Could not create data directory: " + dataDirectory, e);
            }
        }
    }

    /**
     * Resolves a safe file path within the data directory, preventing path traversal attacks.
     */
    public static File getSafeDataFile(String filename) {
        if (filename == null || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename prevents directory traversal: " + filename);
        }
        Path dir = getDataDirectory();
        Path filePath = dir.resolve(filename).normalize();
        if (!filePath.startsWith(dir)) {
            throw new SecurityException("Attempted path traversal outside data directory: " + filename);
        }
        return filePath.toFile();
    }

    public static File getRoomsFile() {
        return getSafeDataFile("rooms.xml");
    }

    public static File getReservationsFile() {
        return getSafeDataFile("reservations.xml");
    }

    public static String getAdminUsername() {
        String user = System.getenv(ENV_ADMIN_USER);
        if (user != null && !user.trim().isEmpty()) {
            return user.trim();
        }
        String propUser = System.getProperty("hotel.admin.user");
        if (propUser != null && !propUser.trim().isEmpty()) {
            return propUser.trim();
        }
        return DEFAULT_ADMIN_USER;
    }

    public static String getAdminPassword() {
        String pass = System.getenv(ENV_ADMIN_PASS);
        if (pass != null && !pass.trim().isEmpty()) {
            return pass.trim();
        }
        String propPass = System.getProperty("hotel.admin.pass");
        if (propPass != null && !propPass.trim().isEmpty()) {
            return propPass.trim();
        }
        return DEFAULT_ADMIN_PASS;
    }
}
