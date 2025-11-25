package com.healthcare.models;

import java.time.LocalDateTime;

/**
 * Appointment model representing the 'appointments' table.
 * Fields match database column names for easy DAO mapping.
 */
public class Appointment {

    private int id;
    private int patientId;
    private int doctorId;
    private LocalDateTime appointmentDateTime;   // matches DB column appointment_datetime
    private String status;                       // BOOKED / COMPLETED / CANCELLED
    private String notes;                        // optional

    public Appointment() {
    }

    public Appointment(int id, int patientId, int doctorId,
                       LocalDateTime appointmentDateTime, String status, String notes) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDateTime = appointmentDateTime;
        this.status = status;
        this.notes = notes;
    }

    // ----------- Getters & Setters -----------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Optional: Useful for debugging
    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", appointmentDateTime=" + appointmentDateTime +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
