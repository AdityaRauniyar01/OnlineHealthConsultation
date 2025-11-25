package com.healthcare.dao;

import com.healthcare.models.Appointment;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Appointment-related database operations.
 * Uses the 'appointments' table in the database.
 */
public class AppointmentDAO {

    /**
     * Books a new appointment by inserting it into the database.
     */
    public boolean bookAppointment(Appointment appointment) {
        if (appointment == null) {
            return false;
        }

        // Default status if none given
        if (appointment.getStatus() == null || appointment.getStatus().isBlank()) {
            appointment.setStatus("BOOKED");
        }

        String sql = "INSERT INTO appointments " +
                "(patient_id, doctor_id, appointment_datetime, status, notes) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, appointment.getPatientId());
            ps.setInt(2, appointment.getDoctorId());
            ps.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentDateTime()));
            ps.setString(4, appointment.getStatus());
            ps.setString(5, appointment.getNotes());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        appointment.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error while booking appointment: " + e.getMessage());
        }
        return false;
    }

    // Reschedule an appointment (patient changes date/time)
public boolean rescheduleAppointment(int appointmentId, LocalDateTime newDateTime) {
    String sql = "UPDATE appointments SET appointment_datetime = ?, status = ? WHERE id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setTimestamp(1, Timestamp.valueOf(newDateTime));
        ps.setString(2, "BOOKED"); // reset to BOOKED on reschedule
        ps.setInt(3, appointmentId);

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error while rescheduling appointment: " + e.getMessage());
    }
    return false;
}


    /**
     * Get all appointments for a given patient.
     */
    public List<Appointment> getAppointmentsForPatient(int patientId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_datetime DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Appointment a = mapRowToAppointment(rs);
                    list.add(a);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching patient appointments: " + e.getMessage());
        }
        return list;
    }

    /**
     * Get all appointments for a doctor.
     */
    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? ORDER BY appointment_datetime ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Appointment a = mapRowToAppointment(rs);
                    list.add(a);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching doctor appointments: " + e.getMessage());
        }

        return list;
    }

    /**
     * Update the appointment status (BOOKED -> COMPLETED).
     */
    public boolean updateStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, appointmentId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating appointment status: " + e.getMessage());
        }

        return false;
    }

    /**
     * Maps a result set row into Appointment object.
     */
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
