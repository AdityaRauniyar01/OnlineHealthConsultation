package com.healthcare.gui;

import com.healthcare.models.Appointment;
import com.healthcare.models.Patient;
import com.healthcare.service.AppointmentService;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

/**
 * UI Frame that allows a Patient to book an appointment with a doctor.
 * Acts as Controller: validates input and calls Service layer.
 */
public class BookAppointmentFrame extends JFrame {

    private final Patient patient;

    private JTextField doctorIdField;
    private JTextField dateTimeField;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public BookAppointmentFrame(Patient patient) {
        this.patient = patient;

        setTitle("Book Appointment");
        setSize(450, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Book Appointment", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        main.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 15));

        form.add(new JLabel("Doctor ID:"));
        doctorIdField = new JTextField();
        form.add(doctorIdField);

        form.add(new JLabel("Date & Time (YYYY-MM-DDTHH:MM):"));
        dateTimeField = new JTextField("2025-11-25T15:30");
        form.add(dateTimeField);

        main.add(form, BorderLayout.CENTER);

        JButton bookBtn = new JButton("Book Appointment");
        bookBtn.addActionListener(e -> handleBooking());
        main.add(bookBtn, BorderLayout.SOUTH);

        add(main);
    }

    private void handleBooking() {
        try {
            String doctorIdText = doctorIdField.getText().trim();
            if (doctorIdText.isEmpty()) {
                showError("Doctor ID cannot be empty.");
                return;
            }

            int doctorId = Integer.parseInt(doctorIdText);
            if (doctorId <= 0) {
                showError("Doctor ID must be a positive number.");
                return;
            }

            LocalDateTime appointmentDateTime;
            try {
                appointmentDateTime = LocalDateTime.parse(
                        dateTimeField.getText().trim(), FORMATTER);
            } catch (Exception ex) {
                showError("Invalid date format! Use YYYY-MM-DDTHH:MM");
                return;
            }

            if (appointmentDateTime.isBefore(LocalDateTime.now())) {
                showError("Appointment cannot be booked in the past.");
                return;
            }

            Appointment appt = new Appointment();
            appt.setPatientId(patient.getId());
            appt.setDoctorId(doctorId);
            appt.setAppointmentDateTime(appointmentDateTime);
            appt.setStatus("BOOKED");
            appt.setNotes(null);

            AppointmentService service = new AppointmentService();
            service.bookAppointment(appt); // may throw exception

            JOptionPane.showMessageDialog(
                    this,
                    "Appointment booked successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException e) {
            showError("Doctor ID must be a number.");
        } catch (Exception e) {
            // ✅ SHOW EXACT ERROR (doctor unavailable, DB error, etc.)
            showError(e.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
