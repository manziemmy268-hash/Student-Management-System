package com.university.view;

import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.Department;
import com.university.service.DepartmentService;
import com.university.service.DepartmentServiceImpl;
import com.university.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DepartmentPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentPanel.class);

    private final DepartmentService departmentService = new DepartmentServiceImpl();
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblRecordCount;
    private JTextField txtSearch;

    public DepartmentPanel() {
        initUI();
        loadDepartments();
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

        JLabel lblTitle = new JLabel("Department Management");
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
        txtSearch.setToolTipText("Search by name or code...");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { searchDepartments(); }
        });

        JButton btnSearch = new JButton("Search");
        Constants.styleButton(btnSearch, Constants.PRIMARY_COLOR);

        JButton btnAdd = new JButton("+ Add Department");
        Constants.styleButton(btnAdd, Constants.SUCCESS_COLOR);
        JButton btnRefresh = new JButton("Refresh");
        Constants.styleButton(btnRefresh, new Color(108, 117, 125));
        btnSearch.addActionListener(e -> searchDepartments());
        btnAdd.addActionListener(e -> openAddDialog());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadDepartments(); });

        actionPanel.add(txtSearch);
        actionPanel.add(btnSearch);
        actionPanel.add(Box.createHorizontalStrut(5));
        actionPanel.add(btnAdd);
        actionPanel.add(btnRefresh);
        headerPanel.add(actionPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1));
        String[] columns = {"ID", "Department Name", "Code", "Location", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        Constants.styleTable(table);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        bottomPanel.setBackground(Color.WHITE);
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

    private void loadDepartments() {
        new SwingWorker<List<Department>, Void>() {
            @Override
            protected List<Department> doInBackground() {
                return departmentService.getAllDepartments();
            }

            @Override
            protected void done() {
                try {
                    List<Department> depts = get();
                    tableModel.setRowCount(0);
                    for (Department d : depts) {
                        tableModel.addRow(new Object[]{d.getDepartmentId(), d.getDepartmentName(), d.getDepartmentCode(), d.getLocation(), d.getPhone()});
                    }
                    lblRecordCount.setText(depts.size() + " records found");
                } catch (Exception ex) {
                    logger.error("Error loading departments", ex);
                    JOptionPane.showMessageDialog(DepartmentPanel.this, "An error occurred while loading departments.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void searchDepartments() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) { loadDepartments(); return; }
        new SwingWorker<List<Department>, Void>() {
            @Override
            protected List<Department> doInBackground() {
                return departmentService.searchDepartments(keyword);
            }

            @Override
            protected void done() {
                try {
                    List<Department> depts = get();
                    tableModel.setRowCount(0);
                    for (Department d : depts) {
                        tableModel.addRow(new Object[]{d.getDepartmentId(), d.getDepartmentName(), d.getDepartmentCode(), d.getLocation(), d.getPhone()});
                    }
                    lblRecordCount.setText(depts.size() + " records found");
                } catch (Exception ex) {
                    logger.error("Error searching departments", ex);
                    JOptionPane.showMessageDialog(DepartmentPanel.this, "An error occurred while searching.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public void openAddDialog() {
        JDialog dialog = createDialog("Add Department", 460, 380);
        JPanel form = createFormPanel();
        JTextField txtName = new JTextField(20);
        JTextField txtCode = new JTextField(20);
        JTextField txtLocation = new JTextField(20);
        JTextField txtPhone = new JTextField(20);
        int row = 0;
        addFormRow(form, row++, "Department Name *:", txtName);
        addFormRow(form, row++, "Department Code *:", txtCode);
        addFormRow(form, row++, "Location:", txtLocation);
        addFormRow(form, row++, "Phone:", txtPhone);

        JButton btnSave = new JButton("Save");
        Constants.styleButton(btnSave, Constants.SUCCESS_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));

        btnSave.addActionListener(e -> {
            try {
                Department dept = new Department();
                dept.setDepartmentName(txtName.getText().trim());
                dept.setDepartmentCode(txtCode.getText().trim());
                dept.setLocation(txtLocation.getText().trim());
                dept.setPhone(txtPhone.getText().trim());
                if (departmentService.addDepartment(dept)) {
                    JOptionPane.showMessageDialog(dialog, "Department added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDepartments();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed. Code may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DuplicateEntityException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Error adding department", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        addDialogButtons(dialog, form, btnSave, btnCancel);
    }

    private void editSelected() {
        int id = getSelectedId();
        if (id == -1) { JOptionPane.showMessageDialog(this, "Please select a department."); return; }
        Department dept = departmentService.getDepartmentById(id);
        if (dept == null) return;

        JDialog dialog = createDialog("Edit Department", 460, 380);
        JPanel form = createFormPanel();
        JTextField txtName = new JTextField(dept.getDepartmentName(), 20);
        JTextField txtCode = new JTextField(dept.getDepartmentCode(), 20);
        JTextField txtLocation = new JTextField(dept.getLocation(), 20);
        JTextField txtPhone = new JTextField(dept.getPhone(), 20);
        int r = 0;
        addFormRow(form, r++, "Department Name *:", txtName);
        addFormRow(form, r++, "Department Code *:", txtCode);
        addFormRow(form, r++, "Location:", txtLocation);
        addFormRow(form, r++, "Phone:", txtPhone);

        JButton btnUpdate = new JButton("Update");
        Constants.styleButton(btnUpdate, Constants.PRIMARY_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));

        btnUpdate.addActionListener(e -> {
            try {
                dept.setDepartmentName(txtName.getText().trim());
                dept.setDepartmentCode(txtCode.getText().trim());
                dept.setLocation(txtLocation.getText().trim());
                dept.setPhone(txtPhone.getText().trim());
                if (departmentService.updateDepartment(dept)) {
                    JOptionPane.showMessageDialog(dialog, "Updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDepartments();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DuplicateEntityException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Error updating department", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        addDialogButtons(dialog, form, btnUpdate, btnCancel);
    }

    private void deleteSelected() {
        int id = getSelectedId();
        if (id == -1) { JOptionPane.showMessageDialog(this, "Please select a department."); return; }
        int row = table.getSelectedRow();
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete department: " + name + "?\nThis may affect linked records.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (departmentService.deleteDepartment(id)) {
                JOptionPane.showMessageDialog(this, "Deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDepartments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed. Department may have linked records.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewSelected() {
        int id = getSelectedId();
        if (id == -1) { JOptionPane.showMessageDialog(this, "Please select a department."); return; }
        Department d = departmentService.getDepartmentById(id);
        if (d == null) return;

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        addInfoRow(infoPanel, "Department ID:", String.valueOf(d.getDepartmentId()));
        addInfoRow(infoPanel, "Department Name:", d.getDepartmentName());
        addInfoRow(infoPanel, "Department Code:", d.getDepartmentCode());
        addInfoRow(infoPanel, "Location:", d.getLocation() != null ? d.getLocation() : "N/A");
        addInfoRow(infoPanel, "Phone:", d.getPhone() != null ? d.getPhone() : "N/A");
        JOptionPane.showMessageDialog(this, infoPanel, "Department Details - " + d.getDepartmentName(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label); lbl.setFont(Constants.LABEL_BOLD); lbl.setForeground(Constants.TEXT_SECONDARY); panel.add(lbl);
        JLabel v = new JLabel(value); v.setFont(Constants.FIELD_FONT); v.setForeground(Constants.TEXT_PRIMARY); panel.add(v);
    }

    private JDialog createDialog(String title, int w, int h) { JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true); d.setSize(w, h); d.setLocationRelativeTo(this); return d; }
    private JPanel createFormPanel() { JPanel p = new JPanel(new GridBagLayout()); p.setBackground(Color.WHITE); p.setBorder(new EmptyBorder(20, 25, 10, 25)); return p; }
    private void addFormRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(4, 5, 4, 5); gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; JLabel lbl = new JLabel(label); lbl.setFont(Constants.LABEL_BOLD); panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1; Constants.styleField(field); panel.add(field, gbc);
    }
    private void addDialogButtons(JDialog dialog, JPanel form, JButton b1, JButton b2) {
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); bp.setBackground(Constants.LIGHT_COLOR); bp.add(b1); bp.add(b2);
        dialog.setLayout(new BorderLayout()); dialog.add(form, BorderLayout.CENTER); dialog.add(bp, BorderLayout.SOUTH); dialog.setVisible(true);
    }
}
