package com.hotel.security;

import com.hotel.config.AppConfig;
import com.hotel.config.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Security & Validation Tests")
public class SecurityUtilTest {

    @Test
    @DisplayName("Password hashing produces unique hashes with different salts and verifies correctly")
    void testPasswordHashingAndVerification() {
        String password = "SecureAdminPassword!2026";
        String salt1 = SecurityUtil.generateSalt();
        String salt2 = SecurityUtil.generateSalt();

        assertNotEquals(salt1, salt2);

        String hash1 = SecurityUtil.hashPassword(password, salt1);
        String hash2 = SecurityUtil.hashPassword(password, salt2);

        assertNotEquals(hash1, hash2);

        assertTrue(SecurityUtil.verifyPassword(password, salt1, hash1));
        assertTrue(SecurityUtil.verifyPassword(password, salt2, hash2));
        assertFalse(SecurityUtil.verifyPassword("WrongPassword", salt1, hash1));
        assertFalse(SecurityUtil.verifyPassword(password, salt1, hash2));
    }

    @Test
    @DisplayName("CSRF Token generation creates distinct non-empty strings")
    void testCsrfTokenGeneration() {
        String token1 = SecurityUtil.generateCsrfToken();
        String token2 = SecurityUtil.generateCsrfToken();

        assertNotNull(token1);
        assertNotNull(token2);
        assertTrue(token1.length() >= 32);
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Email format validation accepts standard emails and rejects malformed inputs")
    void testEmailValidation() {
        assertTrue(SecurityUtil.isValidEmail("guest@example.com"));
        assertTrue(SecurityUtil.isValidEmail("john.doe+stay@hotel.co.in"));
        assertFalse(SecurityUtil.isValidEmail("invalid-email"));
        assertFalse(SecurityUtil.isValidEmail("@missing-username.com"));
        assertFalse(SecurityUtil.isValidEmail("missing-domain@"));
        assertFalse(SecurityUtil.isValidEmail(null));
        assertFalse(SecurityUtil.isValidEmail(""));
    }

    @Test
    @DisplayName("Phone format validation accepts standard 10-15 digit numbers")
    void testPhoneValidation() {
        assertTrue(SecurityUtil.isValidPhone("9876543210"));
        assertTrue(SecurityUtil.isValidPhone("+91 98765 43210"));
        assertTrue(SecurityUtil.isValidPhone("+1-800-555-0199"));
        assertFalse(SecurityUtil.isValidPhone("12345")); // Too short
        assertFalse(SecurityUtil.isValidPhone("abc-def-ghij"));
        assertFalse(SecurityUtil.isValidPhone(null));
    }

    @Test
    @DisplayName("Path traversal defense throws exception on directory traversal attempts")
    void testPathTraversalDefense() {
        assertThrows(IllegalArgumentException.class, () -> AppConfig.getSafeDataFile("../secret.txt"));
        assertThrows(IllegalArgumentException.class, () -> AppConfig.getSafeDataFile("subdir/rooms.xml"));
        assertThrows(IllegalArgumentException.class, () -> AppConfig.getSafeDataFile("..\\windows-system.xml"));
    }

    @Test
    @DisplayName("HTML escaping sanitizes dangerous characters")
    void testHtmlEscaping() {
        String dangerous = "<script>alert('XSS & Hack');</script>";
        String safe = SecurityUtil.escapeHtml(dangerous);
        assertFalse(safe.contains("<script>"));
        assertTrue(safe.contains("&lt;script&gt;"));
        assertTrue(safe.contains("&amp;"));
    }
}
