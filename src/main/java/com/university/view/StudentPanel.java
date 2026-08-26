package com.university.view;

import com.university.dao.LookupDao;
import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.Department;
import com.university.model.Student;
import com.university.service.DepartmentService;
import com.university.service.DepartmentServiceImpl;
import com.university.service.StudentService;
import com.university.service.StudentServiceImpl;
import com.university.utility.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class StudentPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(StudentPanel.class);

    private final StudentService studentService = new StudentServiceImpl();
    private final DepartmentService departmentService = new DepartmentServiceImpl();
    private final LookupDao lookupDao = new LookupDao();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private List<Department> departments;
    private JLabel lblRecordCount;

    public StudentPanel() {
        initUI();
        loadDataInBackground();
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

        JLabel lblTitle = new JLabel("Student Management");
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
            public void keyReleased(java.awt.event.KeyEvent evt) { searchStudents(); }
        });

        JButton btnSearch = new JButton("Search");
        Constants.styleButton(btnSearch, Constants.PRIMARY_COLOR);

        JButton btnAdd = new JButton("+ Register Student");
        Constants.styleButton(btnAdd, Constants.SUCCESS_COLOR);

        JButton btnRefresh = new JButton("Refresh");
        Constants.styleButton(btnRefresh, new Color(108, 117, 125));

        btnSearch.addActionListener(e -> searchStudents());
        btnAdd.addActionListener(e -> openAddDialog());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadDataInBackground(); });

        actionPanel.add(txtSearch);
        actionPanel.add(btnSearch);
        actionPanel.add(Box.createHorizontalStrut(5));
        actionPanel.add(btnAdd);
        actionPanel.add(btnRefresh);

        headerPanel.add(actionPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Constants.CARD_BG);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                new EmptyBorder(0, 0, 0, 0)));

        String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "DOB",
                "Gender", "Enrolled", "Department", "GPA", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        Constants.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Constants.CARD_BG);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Constants.CARD_BG);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Constants.BORDER_COLOR),
                new EmptyBorder(10, 15, 10, 15)));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftButtons.setOpaque(false);

        JButton btnView = new JButton("View Details");
        Constants.styleButton(btnView, Constants.INFO_COLOR);

        JButton btnEdit = new JButton("Edit");
        Constants.styleButton(btnEdit, Constants.PRIMARY_COLOR);

        JButton btnDelete = new JButton("Delete");
        Constants.styleButton(btnDelete, Constants.DANGER_COLOR);

        btnView.addActionListener(e -> viewSelectedStudent());
        btnEdit.addActionListener(e -> editSelectedStudent());
        btnDelete.addActionListener(e -> deleteSelectedStudent());

        leftButtons.add(btnView);
        leftButtons.add(btnEdit);
        leftButtons.add(btnDelete);

        bottomPanel.add(leftButtons, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadDataInBackground() {
        new SwingWorker<List<Student>, Void>() {
            @Override
            protected List<Student> doInBackground() {
                departments = departmentService.getAllDepartments();
                return studentService.getAllStudents();
            }

            @Override
            protected void done() {
                try {
                    List<Student> students = get();
                    tableModel.setRowCount(0);
                    for (Student s : students) {
                        tableModel.addRow(new Object[]{
                                s.getStudentId(), s.getFirstName(), s.getLastName(),
                                s.getEmail(), s.getPhone(),
                                s.getDateOfBirth() != null ? s.getDateOfBirth().toString() : "",
                                s.getGender(),
                                s.getEnrollmentDate() != null ? s.getEnrollmentDate().toString() : "",
                                s.getDepartmentName(),
                                String.format("%.2f", s.getGpa()), s.getStatus()
                        });
                    }
                    lblRecordCount.setText(students.size() + " records found");
                } catch (Exception ex) {
                    lblRecordCount.setText("Error loading data");
                }
            }
        }.execute();
    }

    private void searchStudents() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) { loadDataInBackground(); return; }

        new SwingWorker<List<Student>, Void>() {
            @Override
            protected List<Student> doInBackground() {
                return studentService.searchStudents(keyword);
            }

            @Override
            protected void done() {
                try {
                    List<Student> students = get();
                    tableModel.setRowCount(0);
                    for (Student s : students) {
                        tableModel.addRow(new Object[]{
                                s.getStudentId(), s.getFirstName(), s.getLastName(),
                                s.getEmail(), s.getPhone(),
                                s.getDateOfBirth() != null ? s.getDateOfBirth().toString() : "",
                                s.getGender(),
                                s.getEnrollmentDate() != null ? s.getEnrollmentDate().toString() : "",
                                s.getDepartmentName(),
                                String.format("%.2f", s.getGpa()), s.getStatus()
                        });
                    }
                    lblRecordCount.setText(students.size() + " records found");
                } catch (Exception ex) {
                    lblRecordCount.setText("Error searching");
                }
            }
        }.execute();
    }

    private int getSelectedId() {
        int row = table.getSelectedRow();
        if (row == -1) return -1;
        Object val = tableModel.getValueAt(row, 0);
        return val instanceof Number ? ((Number) val).intValue() : -1;
    }

    public void openAddDialog() {
        JDialog dialog = createDialog("Register New Student", 520, 620);
        JPanel form = createFormPanel();

        JTextField txtFirstName = new JTextField(20);
        JTextField txtLastName = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JTextField txtPhone = new JTextField(20);
        JTextField txtDob = new JTextField(20);
        JComboBox<String> cmbGender = new JComboBox<>(lookupDao.getOptionsArray("gender"));
        JTextField txtEnrollDate = new JTextField(java.time.LocalDate.now().toString(), 20);
        JComboBox<Department> cmbDept = new JComboBox<>();
        if (departments != null) {
            for (Department d : departments) cmbDept.addItem(d);
        }
        JTextField txtGpa = new JTextField("0.00", 20);
        JComboBox<String> cmbStatus = new JComboBox<>(lookupDao.getOptionsArray("student_status"));

        int row = 0;
        addFormRow(form, row++, "First Name *:", txtFirstName);
        addFormRow(form, row++, "Last Name *:", txtLastName);
        addFormRow(form, row++, "Email *:", txtEmail);
        addFormRow(form, row++, "Phone *:", txtPhone);
        addFormRow(form, row++, "Date of Birth *:", txtDob);
        addFormRow(form, row++, "Gender *:", cmbGender);
        addFormRow(form, row++, "Enrollment Date *:", txtEnrollDate);
        addFormRow(form, row++, "Department:", cmbDept);
        addFormRow(form, row++, "GPA (0.00-4.00):", txtGpa);
        addFormRow(form, row++, "Status:", cmbStatus);

        JButton btnSave = new JButton("Register Student");
        Constants.styleButton(btnSave, Constants.SUCCESS_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));

        btnSave.addActionListener(e -> {
            try {
                Student student = new Student();
                student.setFirstName(txtFirstName.getText().trim());
                student.setLastName(txtLastName.getText().trim());
                student.setEmail(txtEmail.getText().trim());
                student.setPhone(txtPhone.getText().trim());
                student.setDateOfBirth(Date.valueOf(txtDob.getText().trim()));
                student.setGender((String) cmbGender.getSelectedItem());
                student.setEnrollmentDate(Date.valueOf(txtEnrollDate.getText().trim()));
                Department dept = (Department) cmbDept.getSelectedItem();
                if (dept != null) student.setDepartmentId(dept.getDepartmentId());
                student.setGpa(Double.parseDouble(txtGpa.getText().trim()));
                student.setStatus((String) cmbStatus.getSelectedItem());

                if (studentService.addStudent(student)) {
                    JOptionPane.showMessageDialog(dialog, "Student registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDataInBackground();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to register student. Email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DuplicateEntityException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Error saving student", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());

        addDialogButtons(dialog, form, btnSave, btnCancel);
    }

    private void editSelectedStudent() {
        int studentId = getSelectedId();
        if (studentId == -1) { JOptionPane.showMessageDialog(this, "Please select a student to edit."); return; }
        Student student = studentService.getStudentById(studentId);
        if (student == null) return;

        JDialog dialog = createDialog("Edit Student - " + student.getFullName(), 520, 620);
        JPanel form = createFormPanel();

        JTextField txtFirstName = new JTextField(student.getFirstName(), 20);
        JTextField txtLastName = new JTextField(student.getLastName(), 20);
        JTextField txtEmail = new JTextField(student.getEmail(), 20);
        JTextField txtPhone = new JTextField(student.getPhone(), 20);
        JTextField txtDob = new JTextField(student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : "", 20);
        JComboBox<String> cmbGender = new JComboBox<>(lookupDao.getOptionsArray("gender"));
        cmbGender.setSelectedItem(student.getGender());
        JTextField txtEnrollDate = new JTextField(student.getEnrollmentDate() != null ? student.getEnrollmentDate().toString() : "", 20);
        JComboBox<Department> cmbDept = new JComboBox<>();
        if (departments != null) {
            for (Department d : departments) {
                cmbDept.addItem(d);
                if (d.getDepartmentId() == student.getDepartmentId()) cmbDept.setSelectedItem(d);
            }
        }
        JTextField txtGpa = new JTextField(String.valueOf(student.getGpa()), 20);
        JComboBox<String> cmbStatus = new JComboBox<>(lookupDao.getOptionsArray("student_status"));
        cmbStatus.setSelectedItem(student.getStatus());

        int rowIdx = 0;
        addFormRow(form, rowIdx++, "First Name *:", txtFirstName);
        addFormRow(form, rowIdx++, "Last Name *:", txtLastName);
        addFormRow(form, rowIdx++, "Email *:", txtEmail);
        addFormRow(form, rowIdx++, "Phone *:", txtPhone);
        addFormRow(form, rowIdx++, "Date of Birth *:", txtDob);
        addFormRow(form, rowIdx++, "Gender *:", cmbGender);
        addFormRow(form, rowIdx++, "Enrollment Date *:", txtEnrollDate);
        addFormRow(form, rowIdx++, "Department:", cmbDept);
        addFormRow(form, rowIdx++, "GPA (0.00-4.00):", txtGpa);
        addFormRow(form, rowIdx++, "Status:", cmbStatus);

        JButton btnUpdate = new JButton("Update Student");
        Constants.styleButton(btnUpdate, Constants.PRIMARY_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));

        btnUpdate.addActionListener(e -> {
            try {
                student.setFirstName(txtFirstName.getText().trim());
                student.setLastName(txtLastName.getText().trim());
                student.setEmail(txtEmail.getText().trim());
                student.setPhone(txtPhone.getText().trim());
                student.setDateOfBirth(Date.valueOf(txtDob.getText().trim()));
                student.setGender((String) cmbGender.getSelectedItem());
                student.setEnrollmentDate(Date.valueOf(txtEnrollDate.getText().trim()));
                Department dept = (Department) cmbDept.getSelectedItem();
                if (dept != null) student.setDepartmentId(dept.getDepartmentId());
                student.setGpa(Double.parseDouble(txtGpa.getText().trim()));
                student.setStatus((String) cmbStatus.getSelectedItem());

                if (studentService.updateStudent(student)) {
                    JOptionPane.showMessageDialog(dialog, "Student updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDataInBackground();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update student.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DuplicateEntityException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Error updating student", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        addDialogButtons(dialog, form, btnUpdate, btnCancel);
    }

    private void deleteSelectedStudent() {
        int studentId = getSelectedId();
        if (studentId == -1) { JOptionPane.showMessageDialog(this, "Please select a student to delete."); return; }
        int row = table.getSelectedRow();
        String name = tableModel.getValueAt(row, 1) + " " + tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student:\n" + name + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (studentService.deleteStudent(studentId)) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDataInBackground();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student. Student may have enrollment records.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewSelectedStudent() {
        int studentId = getSelectedId();
        if (studentId == -1) { JOptionPane.showMessageDialog(this, "Please select a student to view."); return; }
        Student s = studentService.getStudentById(studentId);
        if (s == null) return;

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        infoPanel.setBackground(Constants.CARD_BG);
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        addInfoRow(infoPanel, "Student ID:", String.valueOf(s.getStudentId()));
        addInfoRow(infoPanel, "Full Name:", s.getFullName());
        addInfoRow(infoPanel, "Email:", s.getEmail());
        addInfoRow(infoPanel, "Phone:", s.getPhone());
        addInfoRow(infoPanel, "Date of Birth:", s.getDateOfBirth() != null ? s.getDateOfBirth().toString() : "N/A");
        addInfoRow(infoPanel, "Gender:", s.getGender());
        addInfoRow(infoPanel, "Enrollment Date:", s.getEnrollmentDate() != null ? s.getEnrollmentDate().toString() : "N/A");
        addInfoRow(infoPanel, "Department:", s.getDepartmentName());
        addInfoRow(infoPanel, "GPA:", String.format("%.2f", s.getGpa()));
        addInfoRow(infoPanel, "Status:", s.getStatus());

        JOptionPane.showMessageDialog(this, infoPanel, "Student Details - " + s.getFullName(), JOptionPane.INFORMATION_MESSAGE);
    }

    private JDialog createDialog(String title, int w, int h) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(w, h);
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Constants.CARD_BG);
        panel.setBorder(new EmptyBorder(20, 25, 10, 25));
        return panel;
    }

    private void addFormRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(Constants.LABEL_BOLD);
        lbl.setForeground(Constants.TEXT_PRIMARY);
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        Constants.styleField(field);
        panel.add(field, gbc);
    }

    private void addDialogButtons(JDialog dialog, JPanel form, JButton btn1, JButton btn2) {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(Constants.LIGHT_COLOR);
        btnPanel.add(btn1);
        btnPanel.add(btn2);

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(Constants.LABEL_BOLD);
        lbl.setForeground(Constants.TEXT_SECONDARY);
        panel.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(Constants.FIELD_FONT);
        val.setForeground(Constants.TEXT_PRIMARY);
        panel.add(val);
    }
}
