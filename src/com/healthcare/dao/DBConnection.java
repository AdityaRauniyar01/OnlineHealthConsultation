package com.healthcare.dao;

import com.healthcare.utils.DBConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralized database connection helper.
 * Uses DBConfig for configuration values.
 *
 * Make sure MySQL Connector/J is added to your project:
 * mysql-connector-j-8.x.x.jar
 */
public class DBConnection {

    // Static block loads MySQL driver when class is loaded
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver Loaded Successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver NOT found.");
            e.printStackTrace();
        }
    }

    /**
     * Returns a Connection object to the database.
     *
     * @return active SQL connection
     * @throws SQLException if credentials or DB are incorrect
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DBConfig.URL,
                DBConfig.USER,
                DBConfig.PASSWORD
        );
    }
}
