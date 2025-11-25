package com.healthcare.gui;

import com.healthcare.dao.AppointmentDAO;
import com.healthcare.models.Appointment;
import com.healthcare.models.Doctor;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Dashboard for doctors.
 * Shows all appointments for the logged-in doctor in a table,
 * and allows updating the status to COMPLETED.
 */
public class DoctorDashboard extends JFrame {

    private final Doctor doctor;
    private final AppointmentDAO appointmentDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    // Single shared formatter for the date-time column
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DoctorDashboard(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor cannot be null");
        }

        this.doctor = doctor;
        this.appointmentDAO = new AppointmentDAO();

        setTitle("Doctor Dashboard - " + doctor.getName());
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        loadAppointments();
    }

    /**
     * Builds the UI (table + buttons panel).
     */
    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Top label / header
JLabel titleLabel = new JLabel("Appointments for " + doctor.getName(), SwingConstants.CENTER);
titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
add(titleLabel, BorderLayout.NORTH);
table.setRowHeight(24);
table.setShowGrid(true);
table.setGridColor(Color.LIGHT_GRAY);


        // Table setup
        String[] columnNames = {"ID", "Patient ID", "Date & Time", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Make all cells non-editable from UI
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            // Ensure correct column class for better sorting / rendering
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0, 1 -> Integer.class; // ID, Patient ID
                    default -> String.class;    // Date & Time, Status
                };
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons panel
        JPanel bottomPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        JButton markCompletedButton = new JButton("Mark as Completed");

        refreshButton.addActionListener(e -> loadAppointments());
        markCompletedButton.addActionListener(e -> markSelectedAsCompleted());

        bottomPanel.add(refreshButton);
        bottomPanel.add(markCompletedButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads appointments for the current doctor and fills the table.
     */
    private void loadAppointments() {
        // Clear previous rows
        tableModel.setRowCount(0);

        List<Appointment> appointments = appointmentDAO.getAppointmentsForDoctor(doctor.getId());
        for (Appointment a : appointments) {
            String formattedDateTime = a.getAppointmentDateTime() != null
                    ? a.getAppointmentDateTime().format(DATE_TIME_FORMATTER)
                    : "N/A";

            Object[] row = new Object[]{
                    a.getId(),
                    a.getPatientId(),
                    formattedDateTime,
                    a.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Marks the selected appointment as COMPLETED (if not already).
     */
    private void markSelectedAsCompleted() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Please select an appointment first.", "No Selection");
            return;
        }

        Object idValue = tableModel.getValueAt(selectedRow, 0);
        Object statusValue = tableModel.getValueAt(selectedRow, 3);

        if (!(idValue instanceof Integer) || !(statusValue instanceof String)) {
            showMessage("Invalid row data selected.", "Error");
            return;
        }

        int appointmentId = (Integer) idValue;
        String currentStatus = (String) statusValue;

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
            boolean updated = appointmentDAO.updateStatus(appointmentId, "COMPLETED");
            if (updated) {
                showMessage("Status updated successfully.", "Success");
                loadAppointments(); // refresh table
            } else {
                showMessage("Failed to update status. Please try again.", "Error");
            }
        }
    }

    private void showMessage(String msg, String title) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
