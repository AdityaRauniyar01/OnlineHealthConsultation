package com.healthcare.dao;

import com.healthcare.models.Doctor;
import com.healthcare.models.Patient;
import com.healthcare.models.User;
import java.sql.*;
import java.util.Optional;

/**
 * UserDAO handles:
 * - Finding user by username
 * - Creating new users (patients/doctors)
 * - Mapping DB rows to correct Java objects
 *
 * Works with DB tables:
 *  - users
 *  - patients
 *  - doctors
 */
public class UserDAO {

    /**
     * Find a user by username.
     * Returns Optional<User> containing Patient OR Doctor object.
     */
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToUserObject(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error in findByUsername: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Creates a new user in the database.
     * This works for both Patient and Doctor objects.
     */
    public boolean create(User user) {
        if (user == null || user.getUsername() == null) {
            return false;
        }

        String insertUserSQL =
                "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement psUser = con.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS)) {

            // Insert into users table
            psUser.setString(1, user.getUsername());
            psUser.setString(2, user.getPassword());
            psUser.setString(3, user.getRole());
            int rows = psUser.executeUpdate();

            if (rows == 0) return false;

            // Get generated user ID
            int userId;
            try (ResultSet rs = psUser.getGeneratedKeys()) {
                rs.next();
                userId = rs.getInt(1);
            }

            user.setId(userId);

            // Insert into patients/doctors table based on role
            if (user instanceof Patient) {
                return insertPatient((Patient) user);
            } else if (user instanceof Doctor) {
                return insertDoctor((Doctor) user);
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    // ---------------------- Helper inserts ----------------------

    private boolean insertPatient(Patient p) throws SQLException {
        String sql = "INSERT INTO patients (user_id, name, contact) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getContact());

            return ps.executeUpdate() > 0;
        }
    }

    private boolean insertDoctor(Doctor d) throws SQLException {
        String sql = "INSERT INTO doctors (user_id, name, specialization) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, d.getId());
            ps.setString(2, d.getName());
            ps.setString(3, d.getSpecialization());

            return ps.executeUpdate() > 0;
        }
    }

    // ---------------------- User Mapper ----------------------

    private User mapToUserObject(ResultSet rs) throws SQLException {
        int userId = rs.getInt("id");
        String role = rs.getString("role");

        if ("PATIENT".equalsIgnoreCase(role)) {
            return getPatientDetails(userId, rs);
        } else if ("DOCTOR".equalsIgnoreCase(role)) {
            return getDoctorDetails(userId, rs);
        }

        // Fallback / admin
        User u = new User();
        u.setId(userId);
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRole(role);
        return u;
    }

    private Patient getPatientDetails(int userId, ResultSet userRow) throws SQLException {
        String sql = "SELECT * FROM patients WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Patient p = new Patient();
                    p.setId(userId);
                    p.setUsername(userRow.getString("username"));
                    p.setPassword(userRow.getString("password"));
                    p.setRole("PATIENT");
                    p.setName(rs.getString("name"));
                    p.setContact(rs.getString("contact"));
                    return p;
                }
            }
        }
        return null;
    }

   private Doctor getDoctorDetails(int userId, ResultSet userRow) throws SQLException {
    String sql = "SELECT * FROM doctors WHERE user_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, userId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Doctor d = new Doctor();

                // ✅ Use DOCTOR TABLE ID here, because appointments.doctor_id references this
                int doctorTableId = rs.getInt("id");
                d.setId(doctorTableId);

                d.setUsername(userRow.getString("username"));
                d.setPassword(userRow.getString("password"));
                d.setRole("DOCTOR");
                d.setName(rs.getString("name"));
                d.setSpecialization(rs.getString("specialization"));
                return d;
            }
        }
    }
    return null;
}

}
