package com.university.view;

import com.university.dao.LookupDao;
import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.Department;
import com.university.model.Lecturer;
import com.university.service.DepartmentService;
import com.university.service.DepartmentServiceImpl;
import com.university.service.LecturerService;
import com.university.service.LecturerServiceImpl;
import com.university.utility.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class LecturerPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(LecturerPanel.class);

    private final LecturerService lecturerService = new LecturerServiceImpl();
    private final DepartmentService departmentService = new DepartmentServiceImpl();
    private final LookupDao lookupDao = new LookupDao();
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Department> departments;
    private JLabel lblRecordCount;
    private JTextField txtSearch;

    public LecturerPanel() {
        departments = departmentService.getAllDepartments();
        initUI();
        loadLecturers();
    }

    private int getSelectedId() {
        int row = table.getSelectedRow();
        if (row == -1) return -1;
        Object val = tableModel.getValueAt(row, 0);
        return val instanceof Number ? ((Number) val).intValue() : -1;
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Constants.LIGHT_COLOR);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
        headerPanel.setBackground(Constants.LIGHT_COLOR);

        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setBackground(Constants.LIGHT_COLOR);

        JLabel lblTitle = new JLabel("Lecturer Management");
        lblTitle.setFont(Constants.TITLE_FONT);
        lblTitle.setForeground(Constants.TEXT_PRIMARY);
        titleSection.add(lblTitle);

        lblRecordCount = new JLabel("0 records found");
        lblRecordCount.setFont(Constants.STAT_LABEL);
        lblRecordCount.setForeground(Constants.TEXT_SECONDARY);
        titleSection.add(lblRecordCount);

        headerPanel.add(titleSection, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setBackground(Constants.LIGHT_COLOR);

        txtSearch = new JTextField(18);
        txtSearch.setFont(Constants.FIELD_FONT);
        txtSearch.setPreferredSize(new Dimension(220, 36));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        txtSearch.setToolTipText("Search by ID, name, or email...");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { searchLecturers(); }
        });

        JButton btnSearch = new JButton("Search");
        Constants.styleButton(btnSearch, Constants.PRIMARY_COLOR);

        JButton btnAdd = new JButton("+ Add Lecturer");
        Constants.styleButton(btnAdd, Constants.SUCCESS_COLOR);
        JButton btnRefresh = new JButton("Refresh");
        Constants.styleButton(btnRefresh, new Color(108, 117, 125));
        btnSearch.addActionListener(e -> searchLecturers());
        btnAdd.addActionListener(e -> openAddDialog());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadLecturers(); });

        actionPanel.add(txtSearch);
        actionPanel.add(btnSearch);
        actionPanel.add(Box.createHorizontalStrut(5));
        actionPanel.add(btnAdd);
        actionPanel.add(btnRefresh);
        headerPanel.add(actionPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Constants.CARD_BG);
        tableCard.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1));

        String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "Hire Date", "Department", "Rank"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        Constants.styleTable(table);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        bottomPanel.setBackground(Constants.CARD_BG);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Constants.BORDER_COLOR));

        JButton btnView = new JButton("View Details");
        Constants.styleButton(btnView, Constants.INFO_COLOR);
        JButton btnEdit = new JButton("Edit");
        Constants.styleButton(btnEdit, Constants.PRIMARY_COLOR);
        JButton btnDelete = new JButton("Delete");
        Constants.styleButton(btnDelete, Constants.DANGER_COLOR);
        btnView.addActionListener(e -> viewSelected());
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());

        bottomPanel.add(btnView);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadLecturers() {
        new SwingWorker<List<Lecturer>, Void>() {
            @Override
            protected List<Lecturer> doInBackground() {
                departments = departmentService.getAllDepartments();
                return lecturerService.getAllLecturers();
            }

            @Override
            protected void done() {
                try {
                    List<Lecturer> lecturers = get();
                    tableModel.setRowCount(0);
                    for (Lecturer l : lecturers) {
                        tableModel.addRow(new Object[]{
                                l.getLecturerId(), l.getFirstName(), l.getLastName(),
                                l.getEmail(), l.getPhone(),
                                l.getHireDate() != null ? l.getHireDate().toString() : "",
                                l.getDepartmentName(), l.getRank()
                        });
                    }
                    lblRecordCount.setText(lecturers.size() + " records found");
                } catch (Exception ex) {
                    logger.error("Error loading lecturers", ex);
                    JOptionPane.showMessageDialog(LecturerPanel.this, "An error occurred while loading lecturers.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void searchLecturers() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) { loadLecturers(); return; }
        new SwingWorker<List<Lecturer>, Void>() {
            @Override
            protected List<Lecturer> doInBackground() {
                return lecturerService.searchLecturers(keyword);
            }

            @Override
            protected void done() {
                try {
                    List<Lecturer> lecturers = get();
                    tableModel.setRowCount(0);
                    for (Lecturer l : lecturers) {
                        tableModel.addRow(new Object[]{
                                l.getLecturerId(), l.getFirstName(), l.getLastName(),
                                l.getEmail(), l.getPhone(),
                                l.getHireDate() != null ? l.getHireDate().toString() : "",
                                l.getDepartmentName(), l.getRank()
                        });
                    }
                    lblRecordCount.setText(lecturers.size() + " records found");
                } catch (Exception ex) {
                    logger.error("Error searching lecturers", ex);
                    JOptionPane.showMessageDialog(LecturerPanel.this, "An error occurred while searching.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public void openAddDialog() {
        JDialog dialog = createDialog("Add New Lecturer", 500, 500);
        JPanel form = createFormPanel();

        JTextField txtFirstName = new JTextField(20);
        JTextField txtLastName = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JTextField txtPhone = new JTextField(20);
        JTextField txtHireDate = new JTextField(java.time.LocalDate.now().toString(), 20);
        JComboBox<Department> cmbDept = new JComboBox<>();
        for (Department d : departments) cmbDept.addItem(d);
        JComboBox<String> cmbRank = new JComboBox<>(lookupDao.getOptionsArray("lecturer_rank"));

        int row = 0;
        addFormRow(form, row++, "First Name *:", txtFirstName);
        addFormRow(form, row++, "Last Name *:", txtLastName);
        addFormRow(form, row++, "Email *:", txtEmail);
        addFormRow(form, row++, "Phone:", txtPhone);
        addFormRow(form, row++, "Hire Date *:", txtHireDate);
        addFormRow(form, row++, "Department:", cmbDept);
        addFormRow(form, row++, "Rank:", cmbRank);

        JButton btnSave = new JButton("Add Lecturer");
        Constants.styleButton(btnSave, Constants.SUCCESS_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));

        btnSave.addActionListener(e -> {
            try {
                Lecturer lec = new Lecturer();
                lec.setFirstName(txtFirstName.getText().trim());
                lec.setLastName(txtLastName.getText().trim());
                lec.setEmail(txtEmail.getText().trim());
                lec.setPhone(txtPhone.getText().trim());
                lec.setHireDate(Date.valueOf(txtHireDate.getText().trim()));
                lec.setDepartmentId(((Department) cmbDept.getSelectedItem()).getDepartmentId());
                lec.setRank((String) cmbRank.getSelectedItem());

                if (lecturerService.addLecturer(lec)) {
                    JOptionPane.showMessageDialog(dialog, "Lecturer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadLecturers();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add lecturer.\nEmail may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DuplicateEntityException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Error adding lecturer", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        addDialogButtons(dialog, form, btnSave, btnCancel);
    }

    private void editSelected() {
        int id = getSelectedId();
        if (id == -1) { JOptionPane.showMessageDialog(this, "Please select a lecturer to edit."); return; }
        Lecturer lec = lecturerService.getLecturerById(id);
        if (lec == null) return;

        JDialog dialog = createDialog("Edit Lecturer - " + lec.getFullName(), 500, 500);
        JPanel form = createFormPanel();

        JTextField txtFirstName = new JTextField(lec.getFirstName(), 20);
        JTextField txtLastName = new JTextField(lec.getLastName(), 20);
        JTextField txtEmail = new JTextField(lec.getEmail(), 20);
        JTextField txtPhone = new JTextField(lec.getPhone(), 20);
        JTextField txtHireDate = new JTextField(lec.getHireDate() != null ? lec.getHireDate().toString() : "", 20);
        JComboBox<Department> cmbDept = new JComboBox<>();
        for (Department d : departments) {
            cmbDept.addItem(d);
            if (d.getDepartmentId() == lec.getDepartmentId()) cmbDept.setSelectedItem(d);
        }
        JComboBox<String> cmbRank = new JComboBox<>(lookupDao.getOptionsArray("lecturer_rank"));
        cmbRank.setSelectedItem(lec.getRank());

        int r = 0;
        addFormRow(form, r++, "First Name *:", txtFirstName);
        addFormRow(form, r++, "Last Name *:", txtLastName);
        addFormRow(form, r++, "Email *:", txtEmail);
        addFormRow(form, r++, "Phone:", txtPhone);
        addFormRow(form, r++, "Hire Date *:", txtHireDate);
        addFormRow(form, r++, "Department:", cmbDept);
        addFormRow(form, r++, "Rank:", cmbRank);

        JButton btnUpdate = new JButton("Update Lecturer");
        Constants.styleButton(btnUpdate, Constants.PRIMARY_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));

        btnUpdate.addActionListener(e -> {
            try {
                lec.setFirstName(txtFirstName.getText().trim());
                lec.setLastName(txtLastName.getText().trim());
                lec.setEmail(txtEmail.getText().trim());
                lec.setPhone(txtPhone.getText().trim());
                lec.setHireDate(Date.valueOf(txtHireDate.getText().trim()));
                lec.setDepartmentId(((Department) cmbDept.getSelectedItem()).getDepartmentId());
                lec.setRank((String) cmbRank.getSelectedItem());
                if (lecturerService.updateLecturer(lec)) {
                    JOptionPane.showMessageDialog(dialog, "Lecturer updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadLecturers();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DuplicateEntityException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Error updating lecturer", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        addDialogButtons(dialog, form, btnUpdate, btnCancel);
    }

    private void deleteSelected() {
        int id = getSelectedId();
        if (id == -1) { JOptionPane.showMessageDialog(this, "Please select a lecturer."); return; }
        int row = table.getSelectedRow();
        String name = tableModel.getValueAt(row, 1) + " " + tableModel.getValueAt(row, 2);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete lecturer: " + name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (lecturerService.deleteLecturer(id)) {
                JOptionPane.showMessageDialog(this, "Lecturer deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadLecturers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewSelected() {
        int id = getSelectedId();
        if (id == -1) { JOptionPane.showMessageDialog(this, "Please select a lecturer."); return; }
        Lecturer l = lecturerService.getLecturerById(id);
        if (l == null) return;

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        infoPanel.setBackground(Constants.CARD_BG);
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        addInfoRow(infoPanel, "Lecturer ID:", String.valueOf(l.getLecturerId()));
        addInfoRow(infoPanel, "Full Name:", l.getFullName());
        addInfoRow(infoPanel, "Email:", l.getEmail());
        addInfoRow(infoPanel, "Phone:", l.getPhone());
        addInfoRow(infoPanel, "Hire Date:", l.getHireDate() != null ? l.getHireDate().toString() : "N/A");
        addInfoRow(infoPanel, "Department:", l.getDepartmentName());
        addInfoRow(infoPanel, "Rank:", l.getRank());
        JOptionPane.showMessageDialog(this, infoPanel, "Lecturer Details - " + l.getFullName(), JOptionPane.INFORMATION_MESSAGE);
    }

    private JDialog createDialog(String title, int w, int h) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        d.setSize(w, h); d.setLocationRelativeTo(this); return d;
    }

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Constants.CARD_BG);
        p.setBorder(new EmptyBorder(20, 25, 10, 25));
        return p;
    }

    private void addFormRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(Constants.LABEL_BOLD);
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        Constants.styleField(field);
        panel.add(field, gbc);
    }

    private void addDialogButtons(JDialog dialog, JPanel form, JButton b1, JButton b2) {
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bp.setBackground(Constants.LIGHT_COLOR);
        bp.add(b1); bp.add(b2);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(bp, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel l = new JLabel(label); l.setFont(Constants.LABEL_BOLD); l.setForeground(Constants.TEXT_SECONDARY); panel.add(l);
        JLabel v = new JLabel(value); v.setFont(Constants.FIELD_FONT); v.setForeground(Constants.TEXT_PRIMARY); panel.add(v);
    }
}
