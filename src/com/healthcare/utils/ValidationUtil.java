package com.healthcare.utils;

/**
 * Utility class containing common validation methods
 * used throughout the application (login, registration,
 * appointment booking, etc.).
 */
public final class ValidationUtil {

    // Prevent instantiation
    private ValidationUtil() { }

    /**
     * Basic email validation with regex.
     * Example valid: user@example.com
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;

        // Simple & effective email pattern
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    /**
     * Checks if a string is non-null and not empty/blank.
     */
    public static boolean isNonEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates mobile/contact number.
     * Example: 10 digits (Indian format)
     */
    public static boolean isValidMobile(String mobile) {
        if (mobile == null) return false;

        // Accepts only digits, 10-digit mobile numbers
        return mobile.matches("^[0-9]{10}$");
    }

    /**
     * Checks if a string is a valid integer number.
     */
    public static boolean isInteger(String value) {
        if (value == null || value.trim().isEmpty()) return false;

        return value.matches("^[0-9]+$");
    }

    /**
     * Checks if length is within a given range.
     */
    public static boolean isLengthBetween(String value, int min, int max) {
        if (value == null) return false;
        int len = value.trim().length();
        return len >= min && len <= max;
    }
}

