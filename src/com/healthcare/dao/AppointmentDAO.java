package com.healthcare.dao;

import com.healthcare.models.Appointment;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    // ================================
    // INSERT appointment
    // ================================
    public void insertAppointment(Connection con, Appointment appointment) throws SQLException {

        String sql = "INSERT INTO appointments " +
                "(patient_id, doctor_id, appointment_datetime, status, notes) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps =
                     con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, appointment.getPatientId());
            ps.setInt(2, appointment.getDoctorId());
            ps.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDateTime()));
            ps.setString(4, appointment.getStatus());
            ps.setString(5, appointment.getNotes());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    appointment.setId(rs.getInt(1));
                }
            }
        }
    }

    // ================================
    // CHECK DOCTOR AVAILABILITY
    // ================================
   // ================================
// CHECK DOCTOR AVAILABILITY (20-MIN INTERVAL)
// ================================
public boolean isDoctorAvailable(Connection con, int doctorId,
                                 LocalDateTime requestedTime) throws SQLException {

    String sql = """
        SELECT COUNT(*)
        FROM appointments
        WHERE doctor_id = ?
        AND status = 'BOOKED'
        AND appointment_datetime BETWEEN ? AND ?
        """;

    // 20 minutes before & after requested time
    LocalDateTime start = requestedTime.minusMinutes(20);
    LocalDateTime end   = requestedTime.plusMinutes(20);

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, doctorId);
        ps.setTimestamp(2, Timestamp.valueOf(start));
        ps.setTimestamp(3, Timestamp.valueOf(end));

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) == 0; // available if no clash
            }
        }
    }
    return false;
}


    // ================================
    // UPDATE STATUS (COMPLETED / CANCELLED)
    // ================================
    public boolean updateStatus(Connection con, int appointmentId, String status)
            throws SQLException {

        String sql = "UPDATE appointments SET status = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        }
    }

    // ================================
    // CANCEL APPOINTMENT
    // ================================
    public boolean cancelAppointment(Connection con, int appointmentId)
            throws SQLException {

        String sql = "UPDATE appointments SET status = 'CANCELLED' WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            return ps.executeUpdate() > 0;
        }
    }

    // ================================
    // RESCHEDULE APPOINTMENT
    // ================================
    public void rescheduleAppointment(Connection con, int appointmentId,
                                      LocalDateTime newDateTime) throws SQLException {

        String sql = "UPDATE appointments SET appointment_datetime = ?, status = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(newDateTime));
            ps.setString(2, "BOOKED");
            ps.setInt(3, appointmentId);
            ps.executeUpdate();
        }
    }

    // ================================
    // READ OPERATIONS
    // ================================
    public List<Appointment> getAppointmentsForPatient(int patientId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_datetime DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching patient appointments: " + e.getMessage());
        }
        return list;
    }

    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? ORDER BY appointment_datetime ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctor appointments: " + e.getMessage());
        }
        return list;
    }

    private Appointment mapRowToAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setId(rs.getInt("id"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));

        Timestamp ts = rs.getTimestamp("appointment_datetime");
        if (ts != null) {
            a.setAppointmentDateTime(ts.toLocalDateTime());
        }

        a.setStatus(rs.getString("status"));
        a.setNotes(rs.getString("notes"));
        return a;
    }
}
