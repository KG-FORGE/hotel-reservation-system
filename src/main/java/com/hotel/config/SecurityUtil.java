package com.hotel.config;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Security and validation utilities.
 * Provides cryptographic hashing, CSRF generation, and server-side input validation.
 */
public final class SecurityUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    // RFC 5322 compliant regex for basic email format validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$"
    );

    // Standard phone regex: 10 to 15 digits, allowing optional leading '+' and separators
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9\\s\\-()]{10,18}$"
    );

    // Alpha-numeric booking ID pattern
    private static final Pattern BOOKING_ID_PATTERN = Pattern.compile(
            "^BK[0-9]{6}$"
    );

    private SecurityUtil() {
    }

    /**
     * Generates a cryptographically secure random salt in Base64 encoding.
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        SECURE_RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a password using PBKDF2WithHmacSHA256 with the specified salt.
     */
    public static String hashPassword(String password, String salt) {
        if (password == null || salt == null) {
            throw new IllegalArgumentException("Password and salt cannot be null");
        }
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password with PBKDF2", e);
        }
    }

    /**
     * Verifies a plain text password against a stored salt and hash in constant time.
     */
    public static boolean verifyPassword(String plainPassword, String salt, String expectedHash) {
        if (plainPassword == null || salt == null || expectedHash == null) {
            return false;
        }
        try {
            String calculatedHash = hashPassword(plainPassword, salt);
            byte[] a = calculatedHash.getBytes();
            byte[] b = expectedHash.getBytes();
            return MessageDigest.isEqual(a, b);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generates a cryptographically secure CSRF token.
     */
    public static String generateCsrfToken() {
        byte[] token = new byte[32];
        SECURE_RANDOM.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

    /**
     * Validates email format.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates phone format.
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String trimmed = phone.trim();
        // Count actual digits
        long digitCount = trimmed.chars().filter(Character::isDigit).count();
        return digitCount >= 10 && digitCount <= 15 && PHONE_PATTERN.matcher(trimmed).matches();
    }

    /**
     * Validates booking ID format (e.g. BK100001).
     */
    public static boolean isValidBookingId(String bookingId) {
        return bookingId != null && BOOKING_ID_PATTERN.matcher(bookingId.trim().toUpperCase()).matches();
    }

    /**
     * Basic HTML escaping to neutralize XSS in dynamic outputs if rendered outside JSTL c:out.
     */
    public static String escapeHtml(String input) {
        if (input == null) return "";
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#x27;"); break;
                default: sb.append(c); break;
            }
        }
        return sb.toString();
    }
}
