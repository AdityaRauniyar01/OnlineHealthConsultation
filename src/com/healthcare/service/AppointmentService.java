package com.healthcare.service;

import java.sql.Connection;
import java.util.List;

import com.healthcare.dao.AppointmentDAO;
import com.healthcare.dao.DBConnection;
import com.healthcare.models.Appointment;

/**
 * Service layer for Appointment-related operations.
 * Handles business logic and transaction management.
 */
public class AppointmentService {

    // ================================
    // 1️⃣ BOOK APPOINTMENT (ENHANCEMENT 1)
    // ================================
    public void bookAppointment(Appointment appointment) throws Exception {

        // ---- Business logic ----
        if (appointment.getStatus() == null || appointment.getStatus().isBlank()) {
            appointment.setStatus("BOOKED");
        }

        Connection con = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // START TRANSACTION

            AppointmentDAO dao = new AppointmentDAO();

            // ✅ Check doctor availability (prevent double booking)
            boolean available = dao.isDoctorAvailable(
                    con,
                    appointment.getDoctorId(),
                    appointment.getAppointmentDateTime()
            );

            if (!available) {
                throw new Exception("Doctor is already booked at the selected time.");
            }

            // Insert appointment
            dao.insertAppointment(con, appointment);

            con.commit(); // COMMIT

        } catch (Exception e) {
            if (con != null) {
                con.rollback(); // ROLLBACK
            }
            throw e; // propagate error to GUI
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }

    // ================================
    // 2️⃣ MARK APPOINTMENT AS COMPLETED
    // ================================
    public boolean markAppointmentCompleted(int appointmentId) {

        Connection con = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            AppointmentDAO dao = new AppointmentDAO();
            boolean ok = dao.updateStatus(con, appointmentId, "COMPLETED");

            con.commit();
            return ok;

        } catch (Exception e) {
            try {
                if (con != null) con.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;

        } finally {
            try {
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ================================
    // 3️⃣ CANCEL APPOINTMENT (ENHANCEMENT 2)
    // ================================
    public boolean cancelAppointment(int appointmentId) {

        Connection con = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // START TRANSACTION

            AppointmentDAO dao = new AppointmentDAO();
            boolean result = dao.cancelAppointment(con, appointmentId);

            con.commit(); // COMMIT
            return result;

        } catch (Exception e) {
            try {
                if (con != null) con.rollback(); // ROLLBACK
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;

        } finally {
            try {
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ================================
    // 4️⃣ FETCH PATIENT APPOINTMENTS (READ)
    // ================================
    public List<Appointment> getAppointmentsForPatient(int patientId) {
        AppointmentDAO dao = new AppointmentDAO();
        return dao.getAppointmentsForPatient(patientId);
    }

    // ================================
    // 5️⃣ FETCH DOCTOR APPOINTMENTS (READ)
    // ================================
    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        AppointmentDAO dao = new AppointmentDAO();
        return dao.getAppointmentsForDoctor(doctorId);
    }
}
