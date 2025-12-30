package com.healthcare.gui;

import com.healthcare.models.Appointment;
import com.healthcare.models.Doctor;
import com.healthcare.service.AppointmentService;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Dashboard for doctors.
 * Shows all appointments for the logged-in doctor
 * and allows marking appointments as COMPLETED.
 */
public class DoctorDashboard extends JFrame {

    private final Doctor doctor;
    private final AppointmentService appointmentService;

    private JTable table;
    private DefaultTableModel tableModel;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DoctorDashboard(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }

        this.doctor = doctor;
        this.appointmentService = new AppointmentService();

        setTitle("Doctor Dashboard - " + doctor.getName());
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        loadAppointments();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel(
                "Appointments for " + doctor.getName(),
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Patient ID", "Date & Time", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0 || columnIndex == 1)
                        ? Integer.class : String.class;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        JButton markCompletedButton = new JButton("Mark as Completed");

        refreshButton.addActionListener(e -> loadAppointments());
        markCompletedButton.addActionListener(e -> markSelectedAsCompleted());

        bottomPanel.add(refreshButton);
        bottomPanel.add(markCompletedButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadAppointments() {
        tableModel.setRowCount(0);

        List<Appointment> appointments =
                appointmentService.getAppointmentsForDoctor(doctor.getId());

        for (Appointment a : appointments) {
            String formattedDateTime = a.getAppointmentDateTime() != null
                    ? a.getAppointmentDateTime().format(DATE_TIME_FORMATTER)
                    : "N/A";

            tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getPatientId(),
                    formattedDateTime,
                    a.getStatus()
            });
        }
    }

    private void markSelectedAsCompleted() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Please select an appointment first.", "No Selection");
            return;
        }

        int appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 3);

        if ("COMPLETED".equalsIgnoreCase(currentStatus)) {
            showMessage("This appointment is already marked as COMPLETED.", "Info");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Mark appointment ID " + appointmentId + " as COMPLETED?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean updated =
                    appointmentService.markAppointmentCompleted(appointmentId);

            if (updated) {
                showMessage("Status updated successfully.", "Success");
                loadAppointments();
            } else {
                showMessage("Failed to update status.", "Error");
            }
        }
    }

    private void showMessage(String msg, String title) {
        JOptionPane.showMessageDialog(this, msg, title,
                JOptionPane.INFORMATION_MESSAGE);
    }
}
