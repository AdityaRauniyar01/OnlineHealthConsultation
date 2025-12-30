package com.healthcare.gui;

import com.healthcare.models.Appointment;
import com.healthcare.models.Patient;
import com.healthcare.service.AppointmentService;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Shows all appointments for the logged-in patient.
 * Allows cancellation of appointments.
 * Uses color coding for appointment status.
 */
public class PatientAppointmentsFrame extends JFrame {

    private final Patient patient;
    private final AppointmentService appointmentService;

    private JTable table;
    private DefaultTableModel tableModel;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PatientAppointmentsFrame(Patient patient) {
        this.patient = patient;
        this.appointmentService = new AppointmentService();

        setTitle("My Appointments - " + patient.getName());
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadAppointments();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("My Appointments", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Doctor ID", "Date & Time", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
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
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        // ✅ ENHANCEMENT 3: STATUS COLOR CODING
        table.getColumnModel().getColumn(3)
                .setCellRenderer(new StatusColorRenderer());

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton cancelBtn = new JButton("Cancel Appointment");

        refreshBtn.addActionListener(e -> loadAppointments());
        cancelBtn.addActionListener(e -> cancelSelected());

        btnPanel.add(refreshBtn);
        btnPanel.add(cancelBtn);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadAppointments() {
        tableModel.setRowCount(0);

        List<Appointment> list =
                appointmentService.getAppointmentsForPatient(patient.getId());

        for (Appointment a : list) {
            String dt = a.getAppointmentDateTime() != null
                    ? a.getAppointmentDateTime().format(FORMATTER)
                    : "N/A";

            tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getDoctorId(),
                    dt,
                    a.getStatus()
            });
        }
    }

    private Integer getSelectedAppointmentId() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return null;
        }
        return (Integer) tableModel.getValueAt(row, 0);
    }

    // ENHANCEMENT 2: CANCEL APPOINTMENT
    private void cancelSelected() {
        Integer id = getSelectedAppointmentId();
        if (id == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Cancel appointment ID " + id + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = appointmentService.cancelAppointment(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Appointment cancelled successfully.");
            loadAppointments();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to cancel appointment.");
        }
    }

    // 🎨 CUSTOM RENDERER FOR STATUS COLUMN
    private static class StatusColorRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (!isSelected && value != null) {
                String status = value.toString();

                switch (status) {
                    case "BOOKED":
                        c.setForeground(new Color(0, 102, 204)); // Blue
                        break;
                    case "CANCELLED":
                        c.setForeground(Color.RED);
                        break;
                    case "COMPLETED":
                        c.setForeground(new Color(0, 153, 0)); // Green
                        break;
                    default:
                        c.setForeground(Color.BLACK);
                }
            }

            return c;
        }
    }
}
