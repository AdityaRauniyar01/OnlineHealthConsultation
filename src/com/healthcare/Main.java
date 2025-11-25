package com.healthcare;

import com.healthcare.dao.UserDAO;
import com.healthcare.models.Patient;
import com.healthcare.utils.UITheme;
import javax.swing.SwingUtilities;

/**
 * Entry point of the Healthcare System application.
 * Launches the Login UI and optionally inserts a test user (only if not already present).
 */
public class Main {

    public static void main(String[] args) {

        UITheme.apply();

        // Initialize a test patient for quick login (only if not exists)
        seedSampleUser();

        // Launch Swing UI properly on EDT
        SwingUtilities.invokeLater(() -> {
            new com.healthcare.gui.LoginFrame().setVisible(true);
        });
    }

    /**
     * Inserts a sample patient ONLY if it doesn't already exist.
     */
    private static void seedSampleUser() {
        UserDAO userDAO = new UserDAO();

        // Check if sample user exists
        boolean exists = userDAO.findByUsername("patient").isPresent();

        if (!exists) {
            Patient sample = new Patient(0, "patient", "pass", "John Doe", "9876543210");
            boolean created = userDAO.create(sample);

            if (created) {
                System.out.println("Sample user created: username=patient, password=pass");
            } else {
                System.out.println("Failed to create sample user.");
            }
        } else {
            System.out.println("Sample user already exists.");
        }
    }
}
