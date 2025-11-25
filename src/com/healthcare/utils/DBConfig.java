package com.healthcare.utils;

/**
 * Centralized database configuration.
 * Stores DB URL, username, and password used by DBConnection.
 *
 * NOTE:
 * - Update USER and PASSWORD based on your MySQL setup.
 * - DATABASE: healthcare_db (must already exist in MySQL)
 */
public final class DBConfig {

    // MySQL connection URL
    public static final String URL =
            "jdbc:mysql://localhost:3306/healthcare_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // MySQL user credentials
    public static final String USER = "healthapp";

    // ⚠ CHANGE THIS to your actual MySQL password before running the project
    public static final String PASSWORD = "health123";

    // Private constructor to prevent instantiation
    private DBConfig() { }
}
