package com.university.view;

import com.university.dao.LookupDao;
import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.Course;
import com.university.model.Enrollment;
import com.university.model.Student;
import com.university.service.CourseService;
import com.university.service.CourseServiceImpl;
import com.university.service.EnrollmentService;
import com.university.service.EnrollmentServiceImpl;
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

public class EnrollmentPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentPanel.class);

    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private final StudentService studentService = new StudentServiceImpl();
    private final CourseService courseService = new CourseServiceImpl();
    private final LookupDao lookupDao = new LookupDao();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblRecordCount;

    public EnrollmentPanel() {
        initUI();
        loadEnrollments();
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
        JLabel lblTitle = new JLabel("Enrollment Management");
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
        txtSearch.setToolTipText("Search by student or course name...");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { searchEnrollments(); }
        });

        JButton btnSearch = new JButton("Search");
        Constants.styleButton(btnSearch, Constants.PRIMARY_COLOR);
        JButton btnAdd = new JButton("+ New Enrollment");
        Constants.styleButton(btnAdd, Constants.SUCCESS_COLOR);
        JButton btnRefresh = new JButton("Refresh");
        Constants.styleButton(btnRefresh, new Color(108, 117, 125));
        btnSearch.addActionListener(e -> searchEnrollments());
        btnAdd.addActionListener(e -> openAddDialog());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadEnrollments(); });

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
        String[] columns = {"ID", "Student", "Course", "Code", "Date", "Semester", "Year", "Grade", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        Constants.styleTable(table);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        bottomPanel.setBackground(Constants.CARD_BG);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Constants.BORDER_COLOR));
        JButton btnEdit = new JButton("Update Grade/Status");
        Constants.styleButton(btnEdit, Constants.PRIMARY_COLOR);
        JButton btnDelete = new JButton("Delete");
        Constants.styleButton(btnDelete, Constants.DANGER_COLOR);
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private int getSelectedId() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment.");
            return -1;
        }
        Object val = tableModel.getValueAt(row, 0);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        throw new IllegalStateException("Unexpected value in ID column");
    }

    private void loadEnrollments() {
        new SwingWorker<List<Enrollment>, Void>() {
            @Override
            protected List<Enrollment> doInBackground() {
                return enrollmentService.getAllEnrollments();
            }

            @Override
            protected void done() {
                try {
                    List<Enrollment> enrollments = get();
                    tableModel.setRowCount(0);
                    for (Enrollment e : enrollments) {
                        tableModel.addRow(new Object[]{
                                e.getEnrollmentId(), e.getStudentName(), e.getCourseName(), e.getCourseCode(),
                                e.getEnrollmentDate() != null ? e.getEnrollmentDate().toString() : "",
                                e.getSemester(), e.getAcademicYear(), e.getGrade() != null ? e.getGrade() : "-",
                                e.getStatus()
                        });
                    }
                    lblRecordCount.setText(enrollments.size() + " records found");
                } catch (Exception ex) {
                    logger.error("Failed to load enrollments", ex);
                    JOptionPane.showMessageDialog(EnrollmentPanel.this, "Failed to load enrollments.");
                }
            }
        }.execute();
    }

    private void searchEnrollments() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) { loadEnrollments(); return; }
        new SwingWorker<List<Enrollment>, Void>() {
            @Override
            protected List<Enrollment> doInBackground() {
                return enrollmentService.searchEnrollments(keyword);
            }

            @Override
            protected void done() {
                try {
                    List<Enrollment> enrollments = get();
                    tableModel.setRowCount(0);
                    for (Enrollment e : enrollments) {
                        tableModel.addRow(new Object[]{
                                e.getEnrollmentId(), e.getStudentName(), e.getCourseName(), e.getCourseCode(),
                                e.getEnrollmentDate() != null ? e.getEnrollmentDate().toString() : "",
                                e.getSemester(), e.getAcademicYear(), e.getGrade() != null ? e.getGrade() : "-",
                                e.getStatus()
                        });
                    }
                    lblRecordCount.setText(enrollments.size() + " records found");
                } catch (Exception ex) {
                    logger.error("Failed to search enrollments", ex);
                    JOptionPane.showMessageDialog(EnrollmentPanel.this, "Failed to search enrollments.");
                }
            }
        }.execute();
    }

    public void openAddDialog() {
        JDialog dialog = createDialog("New Enrollment", 500, 470);
        JPanel form = createFormPanel();
        List<Student> students = studentService.getAllStudents();
        List<Course> courses = courseService.getAllCourses();

        JComboBox<Student> cmbStudent = new JComboBox<>();
        for (Student s : students) cmbStudent.addItem(s);
        JComboBox<Course> cmbCourse = new JComboBox<>();
        for (Course c : courses) cmbCourse.addItem(c);
        JTextField txtDate = new JTextField(java.time.LocalDate.now().toString(), 20);
        JComboBox<String> cmbSemester = new JComboBox<>(lookupDao.getOptionsArray("semester"));
        JComboBox<String> cmbYear = new JComboBox<>(lookupDao.getOptionsArray("academic_year"));
        JComboBox<String> cmbStatus = new JComboBox<>(lookupDao.getOptionsArray("enrollment_status"));

        int row = 0;
        addFormRow(form, row++, "Student *:", cmbStudent);
        addFormRow(form, row++, "Course *:", cmbCourse);
        addFormRow(form, row++, "Enrollment Date *:", txtDate);
        addFormRow(form, row++, "Semester:", cmbSemester);
        addFormRow(form, row++, "Academic Year:", cmbYear);
        addFormRow(form, row++, "Status:", cmbStatus);

        JButton btnSave = new JButton("Enroll Student");
        Constants.styleButton(btnSave, Constants.SUCCESS_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));

        btnSave.addActionListener(e -> {
            try {
                if (cmbStudent.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a student.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (cmbCourse.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a course.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Enrollment enrollment = new Enrollment();
                enrollment.setStudentId(((Student) cmbStudent.getSelectedItem()).getStudentId());
                enrollment.setCourseId(((Course) cmbCourse.getSelectedItem()).getCourseId());
                enrollment.setEnrollmentDate(Date.valueOf(txtDate.getText().trim()));
                enrollment.setSemester((String) cmbSemester.getSelectedItem());
                enrollment.setAcademicYear((String) cmbYear.getSelectedItem());
                enrollment.setStatus((String) cmbStatus.getSelectedItem());
                if (enrollmentService.addEnrollment(enrollment)) {
                    JOptionPane.showMessageDialog(dialog, "Student enrolled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadEnrollments();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed. Student may already be enrolled in this course for this semester.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DuplicateEntityException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Failed to add enrollment", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        addDialogButtons(dialog, form, btnSave, btnCancel);
    }

    private void editSelected() {
        int enrollmentId;
        try {
            enrollmentId = getSelectedId();
        } catch (IllegalStateException ex) {
            return;
        }
        if (enrollmentId == -1) return;

        int row = table.getSelectedRow();
        JDialog dialog = createDialog("Update Enrollment", 400, 280);
        JPanel form = createFormPanel();
        JComboBox<String> cmbGrade = new JComboBox<>(lookupDao.getOptionsArray("grade"));
        cmbGrade.setSelectedItem(tableModel.getValueAt(row, 7));
        JComboBox<String> cmbStatus = new JComboBox<>(lookupDao.getOptionsArray("enrollment_status"));
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 8));

        addFormRow(form, 0, "Grade:", cmbGrade);
        addFormRow(form, 1, "Status:", cmbStatus);

        JButton btnUpdate = new JButton("Update");
        Constants.styleButton(btnUpdate, Constants.PRIMARY_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));

        btnUpdate.addActionListener(e -> {
            try {
                Enrollment enrollment = new Enrollment();
                enrollment.setEnrollmentId(enrollmentId);
                enrollment.setGrade((String) cmbGrade.getSelectedItem());
                enrollment.setStatus((String) cmbStatus.getSelectedItem());
                if (enrollmentService.updateEnrollment(enrollment)) {
                    JOptionPane.showMessageDialog(dialog, "Enrollment updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadEnrollments();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DuplicateEntityException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Failed to update enrollment", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        addDialogButtons(dialog, form, btnUpdate, btnCancel);
    }

    private void deleteSelected() {
        int id;
        try {
            id = getSelectedId();
        } catch (IllegalStateException ex) {
            return;
        }
        if (id == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this enrollment?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (enrollmentService.deleteEnrollment(id)) {
                JOptionPane.showMessageDialog(this, "Deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEnrollments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JDialog createDialog(String title, int w, int h) { JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true); d.setSize(w, h); d.setLocationRelativeTo(this); return d; }
    private JPanel createFormPanel() { JPanel p = new JPanel(new GridBagLayout()); p.setBackground(Constants.CARD_BG); p.setBorder(new EmptyBorder(20, 25, 10, 25)); return p; }
    private void addFormRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(8, 5, 8, 5); gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; JLabel lbl = new JLabel(label); lbl.setFont(Constants.LABEL_BOLD); panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1; Constants.styleField(field); panel.add(field, gbc);
    }
    private void addDialogButtons(JDialog dialog, JPanel form, JButton b1, JButton b2) {
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); bp.setBackground(Constants.LIGHT_COLOR); bp.add(b1); bp.add(b2);
        dialog.setLayout(new BorderLayout()); dialog.add(form, BorderLayout.CENTER); dialog.add(bp, BorderLayout.SOUTH); dialog.setVisible(true);
    }
}
