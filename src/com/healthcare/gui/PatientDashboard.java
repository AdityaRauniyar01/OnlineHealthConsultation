package com.healthcare.gui;

import com.healthcare.models.Patient;
import java.awt.*;
import javax.swing.*;

/**
 * Dashboard screen shown after a patient logs in.
 * From here, the patient can navigate to book appointments
 * (and later, view appointments, profile, etc.).
 */
public class PatientDashboard extends JFrame {

    private final Patient patient;

    public PatientDashboard(Patient patient) {
        super("Patient Dashboard");
        this.patient = patient;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initUI();
    }

    /**
     * Builds the dashboard UI.
     */
private void initUI() {
    JPanel main = new JPanel(new BorderLayout(15, 15));
    main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel title = new JLabel("Welcome, " + patient.getName(), SwingConstants.CENTER);
    title.setFont(new Font("Segoe UI", Font.BOLD, 22));
    main.add(title, BorderLayout.NORTH);

    JTextArea txt = new JTextArea();
    txt.setEditable(false);
    txt.setLineWrap(true);
    txt.setWrapStyleWord(true);

    txt.setText("""
            This is your patient dashboard.

            Features:
            • Book a new appointment
            • View upcoming & past appointments
            • Cancel or reschedule appointments

            """);

    main.add(new JScrollPane(txt), BorderLayout.CENTER);

    JPanel bottom = new JPanel();
    JButton bookBtn = new JButton("Book Appointment");
    JButton viewBtn = new JButton("View My Appointments");

    bookBtn.addActionListener(e -> new BookAppointmentFrame(patient).setVisible(true));
    viewBtn.addActionListener(e -> new PatientAppointmentsFrame(patient).setVisible(true));

    bottom.add(bookBtn);
    bottom.add(viewBtn);
    main.add(bottom, BorderLayout.SOUTH);

    add(main);
}

}
