package com.hotel.service;

import com.hotel.config.AppConfig;
import com.hotel.config.SecurityUtil;
import com.hotel.model.AdminUser;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service managing administrator authentication and credential verification.
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private static volatile AuthService instance;

    private final String adminUsername;
    private final String adminPasswordHash;
    private final String adminSalt;

    public AuthService() {
        this.adminUsername = AppConfig.getAdminUsername();
        this.adminSalt = SecurityUtil.generateSalt();
        this.adminPasswordHash = SecurityUtil.hashPassword(AppConfig.getAdminPassword(), this.adminSalt);
        LOGGER.info("AuthService initialized with administrative user: " + adminUsername);
    }

    public static AuthService getInstance() {
        if (instance == null) {
            synchronized (AuthService.class) {
                if (instance == null) {
                    instance = new AuthService();
                }
            }
        }
        return instance;
    }

    /**
     * Authenticates administrator credentials using constant-time cryptographic verification.
     */
    public Optional<AdminUser> authenticate(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }

        if (!adminUsername.equalsIgnoreCase(username.trim())) {
            return Optional.empty();
        }

        boolean valid = SecurityUtil.verifyPassword(password.trim(), this.adminSalt, this.adminPasswordHash);
        if (valid) {
            return Optional.of(new AdminUser(this.adminUsername, this.adminPasswordHash, this.adminSalt, "Hotel Administrator"));
        }

        return Optional.empty();
    }
}
