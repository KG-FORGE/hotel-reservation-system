package com.hotel.service;

import com.hotel.config.AppConfig;
import com.hotel.model.AdminUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Administrator Authentication Service Tests")
public class AuthServiceTest {

    @Test
    @DisplayName("Valid default credentials authenticate successfully")
    void testValidDefaultCredentials() {
        AuthService authService = AuthService.getInstance();
        Optional<AdminUser> user = authService.authenticate("admin", "Admin@Hotel2026");

        assertTrue(user.isPresent());
        assertTrue(user.get().getUsername().equalsIgnoreCase("admin"));
    }

    @Test
    @DisplayName("Invalid password is appropriately rejected")
    void testInvalidPasswordRejected() {
        AuthService authService = AuthService.getInstance();
        Optional<AdminUser> user = authService.authenticate("admin", "WrongPassword123");

        assertFalse(user.isPresent());
    }

    @Test
    @DisplayName("Non-existent username is appropriately rejected")
    void testInvalidUsernameRejected() {
        AuthService authService = AuthService.getInstance();
        Optional<AdminUser> user = authService.authenticate("intruder", "Admin@Hotel2026");

        assertFalse(user.isPresent());
    }

    @Test
    @DisplayName("Null credentials handled safely without exceptions")
    void testNullCredentialsHandled() {
        AuthService authService = AuthService.getInstance();
        assertFalse(authService.authenticate(null, null).isPresent());
        assertFalse(authService.authenticate("admin", null).isPresent());
        assertFalse(authService.authenticate(null, "Admin@Hotel2026").isPresent());
    }
}
