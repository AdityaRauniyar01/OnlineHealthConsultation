package com.healthcare.gui;

import com.healthcare.dao.UserDAO;
import com.healthcare.models.Doctor;
import com.healthcare.models.Patient;
import com.healthcare.models.User;
import java.awt.*;
import javax.swing.*;

/**
 * Login screen for both Patients and Doctors.
 * Uses UserDAO → database-backed authentication.
 */
public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        super("Login");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 220);
        setLocationRelativeTo(null);

        initUI();
    }

    /**
     * Builds the UI layout using GridBagLayout.
     */
private void initUI() {
    JPanel container = new JPanel(new BorderLayout());
    container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel title = new JLabel("Login", SwingConstants.CENTER);
    title.setFont(new Font("Segoe UI", Font.BOLD, 20));
    container.add(title, BorderLayout.NORTH);

    JPanel form = new JPanel(new GridLayout(2, 2, 10, 15));

    form.add(new JLabel("Username:"));
    form.add(usernameField);

    form.add(new JLabel("Password:"));
    form.add(passwordField);

    container.add(form, BorderLayout.CENTER);

    JPanel btnPanel = new JPanel();
    JButton loginBtn = new JButton("Login");
    JButton registerBtn = new JButton("Register Sample User");

    btnPanel.add(loginBtn);
    btnPanel.add(registerBtn);

    container.add(btnPanel, BorderLayout.SOUTH);

    add(container);

    loginBtn.addActionListener(e -> doLogin());
    registerBtn.addActionListener(e -> {
        Patient p = new Patient(0, "patient2", "pass2", "Jane Doe", "+111222333");
        boolean ok = userDAO.create(p);
        JOptionPane.showMessageDialog(this, ok ?
                "Sample patient created!" :
                "User already exists!");
    });
}

    /**
     * Creates a sample patient user quickly (for testing without registration screen).
     */
    private void registerSampleUser() {
        Patient p = new Patient(0, "patient2", "pass2", "Jane Doe", "+1987654321");

        boolean ok = userDAO.create(p);

        JOptionPane.showMessageDialog(
                this,
                ok ? "Sample user created: patient2 / pass2"
                   : "User already exists",
                "Info",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Login process:
     * - Validate input
     * - Check DB for user
     * - Compare passwords
     * - Open Patient or Doctor dashboard
     */
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Simple field validation
        if (username.isEmpty()) {
            showError("Enter username");
            return;
        }
        if (password.isEmpty()) {
            showError("Enter password");
            return;
        }

        userDAO.findByUsername(username).ifPresentOrElse(user -> {
            if (!user.getPassword().equals(password)) {
                showError("Invalid password");
                return;
            }

            // Open correct dashboard
            openDashboardFor(user);

        }, () -> showError("User not found"));
    }

    /**
     * Opens respective dashboard based on user role.
     */
    private void openDashboardFor(User user) {
        if (user instanceof Patient p) {
            new PatientDashboard(p).setVisible(true);
            dispose();
        }
        else if (user instanceof Doctor d) {
            new DoctorDashboard(d).setVisible(true);
            dispose();
        }
        else {
            showError("Unknown user type.");
        }
    }

    /**
     * Helper for showing error messages.
     */
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
