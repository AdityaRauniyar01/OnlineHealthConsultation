package com.healthcare.gui;

import com.healthcare.dao.AppointmentDAO;
import com.healthcare.models.Appointment;
import com.healthcare.models.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Shows all appointments for the logged-in patient.
 * Allows cancel and reschedule.
 */
public class PatientAppointmentsFrame extends JFrame {

    private final Patient patient;
    private final AppointmentDAO appointmentDAO;

    private JTable table;
    private DefaultTableModel tableModel;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PatientAppointmentsFrame(Patient patient) {
        this.patient = patient;
        this.appointmentDAO = new AppointmentDAO();

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

        // ✅ create table FIRST
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);                        // ✅ style AFTER creation
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton refreshBtn = new JButton("Refresh");
        JButton cancelBtn = new JButton("Cancel Appointment");
        JButton rescheduleBtn = new JButton("Reschedule");

        refreshBtn.addActionListener(e -> loadAppointments());
        cancelBtn.addActionListener(e -> cancelSelected());
        rescheduleBtn.addActionListener(e -> rescheduleSelected());

        btnPanel.add(refreshBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(rescheduleBtn);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadAppointments() {
        tableModel.setRowCount(0);

        List<Appointment> list = appointmentDAO.getAppointmentsForPatient(patient.getId());
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
        Object val = tableModel.getValueAt(row, 0);
        if (!(val instanceof Integer)) {
            JOptionPane.showMessageDialog(this, "Invalid selection.");
            return null;
        }
        return (Integer) val;
    }

    private void cancelSelected() {
        Integer id = getSelectedAppointmentId();
        if (id == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Cancel appointment ID " + id + "?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = appointmentDAO.updateStatus(id, "CANCELLED");
        if (ok) {
            JOptionPane.showMessageDialog(this, "Appointment cancelled.");
            loadAppointments();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to cancel appointment.");
        }
    }

    private void rescheduleSelected() {
        Integer id = getSelectedAppointmentId();
        if (id == null) return;

        String newDateTimeStr = JOptionPane.showInputDialog(
                this,
                "Enter new date & time (YYYY-MM-DDTHH:MM):",
                "2025-12-01T15:30"
        );
        if (newDateTimeStr == null || newDateTimeStr.isBlank()) {
            return; // user cancelled dialog
        }

        try {
            LocalDateTime newDt = LocalDateTime.parse(newDateTimeStr.trim());
            boolean ok = appointmentDAO.rescheduleAppointment(id, newDt);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Appointment rescheduled.");
                loadAppointments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reschedule appointment.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format.");
        }
    }
}
