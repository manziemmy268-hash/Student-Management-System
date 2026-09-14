package com.university.view;

import com.university.dao.LookupDao;
import com.university.exception.ValidationException;
import com.university.model.Course;
import com.university.model.Enrollment;
import com.university.service.CourseService;
import com.university.service.CourseServiceImpl;
import com.university.service.EnrollmentService;
import com.university.service.EnrollmentServiceImpl;
import com.university.utility.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class StudentEnrollPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(StudentEnrollPanel.class);

    private final int studentId;
    private final CourseService courseService = new CourseServiceImpl();
    private final EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private final LookupDao lookupDao = new LookupDao();

    private JTable coursesTable;
    private DefaultTableModel coursesTableModel;
    private JLabel lblCourseCount;

    private JTable enrollmentsTable;
    private DefaultTableModel enrollmentsTableModel;
    private JLabel lblEnrollmentSummary;

    private JTabbedPane tabbedPane;

    public StudentEnrollPanel(int studentId) {
        this.studentId = studentId;
        initUI();
        loadCourses();
        loadEnrollments();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Constants.LIGHT_COLOR);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(Constants.HEADING_FONT);

        JPanel browseTab = createBrowseTab();
        JPanel enrollmentsTab = createEnrollmentsTab();

        tabbedPane.addTab("Browse & Enroll", browseTab);
        tabbedPane.addTab("My Enrollments & Grades", enrollmentsTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBrowseTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Constants.LIGHT_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
        headerPanel.setBackground(Constants.LIGHT_COLOR);

        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setBackground(Constants.LIGHT_COLOR);
        JLabel lblTitle = new JLabel("Available Courses");
        lblTitle.setFont(Constants.TITLE_FONT);
        lblTitle.setForeground(Constants.TEXT_PRIMARY);
        titleSection.add(lblTitle);
        lblCourseCount = new JLabel("0 courses available");
        lblCourseCount.setFont(Constants.STAT_LABEL);
        lblCourseCount.setForeground(Constants.TEXT_SECONDARY);
        titleSection.add(lblCourseCount);
        headerPanel.add(titleSection, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setBackground(Constants.LIGHT_COLOR);
        JButton btnRefresh = new JButton("Refresh");
        Constants.styleButton(btnRefresh, new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> loadCourses());
        actionPanel.add(btnRefresh);
        headerPanel.add(actionPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Constants.CARD_BG);
        tableCard.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1));
        String[] columns = {"ID", "Code", "Name", "Credits", "Department", "Lecturer", "Max Size", "Enrolled"};
        coursesTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        coursesTable = new JTable(coursesTableModel);
        Constants.styleTable(coursesTable);
        tableCard.add(new JScrollPane(coursesTable), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        bottomPanel.setBackground(Constants.CARD_BG);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Constants.BORDER_COLOR));
        JButton btnEnroll = new JButton("Enroll in Selected Course");
        Constants.styleButton(btnEnroll, Constants.SUCCESS_COLOR);
        btnEnroll.addActionListener(e -> enrollInSelectedCourse());
        bottomPanel.add(btnEnroll);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createEnrollmentsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Constants.LIGHT_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
        headerPanel.setBackground(Constants.LIGHT_COLOR);

        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setBackground(Constants.LIGHT_COLOR);
        JLabel lblTitle = new JLabel("My Enrollments & Grades");
        lblTitle.setFont(Constants.TITLE_FONT);
        lblTitle.setForeground(Constants.TEXT_PRIMARY);
        titleSection.add(lblTitle);
        lblEnrollmentSummary = new JLabel("0 enrollments | GPA: --");
        lblEnrollmentSummary.setFont(Constants.STAT_LABEL);
        lblEnrollmentSummary.setForeground(Constants.TEXT_SECONDARY);
        titleSection.add(lblEnrollmentSummary);
        headerPanel.add(titleSection, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setBackground(Constants.LIGHT_COLOR);
        JButton btnRefresh = new JButton("Refresh");
        Constants.styleButton(btnRefresh, new Color(108, 117, 125));
        btnRefresh.addActionListener(e -> loadEnrollments());
        actionPanel.add(btnRefresh);
        headerPanel.add(actionPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Constants.CARD_BG);
        tableCard.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1));
        String[] columns = {"ID", "Course", "Code", "Semester", "Year", "Grade", "Status"};
        enrollmentsTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        enrollmentsTable = new JTable(enrollmentsTableModel);
        Constants.styleTable(enrollmentsTable);
        enrollmentsTable.getColumnModel().getColumn(5).setCellRenderer(new GradeCellRenderer());
        tableCard.add(new JScrollPane(enrollmentsTable), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    private void loadCourses() {
        new SwingWorker<List<Course>, Void>() {
            private Map<Integer, Integer> courseCounts = java.util.Collections.emptyMap();

            @Override
            protected List<Course> doInBackground() {
                List<Course> courses = courseService.getAvailableCourses();
                courseCounts = enrollmentService.getEnrollmentCountsByCourse();
                return courses;
            }

            @Override
            protected void done() {
                try {
                    List<Course> courses = get();
                    coursesTableModel.setRowCount(0);
                    for (Course c : courses) {
                        int enrolled = courseCounts.getOrDefault(c.getCourseId(), 0);
                        coursesTableModel.addRow(new Object[]{
                                c.getCourseId(),
                                c.getCourseCode(),
                                c.getCourseName(),
                                c.getCredits(),
                                c.getDepartmentName() != null ? c.getDepartmentName() : "N/A",
                                c.getLecturerName() != null ? c.getLecturerName() : "N/A",
                                c.getMaxEnrollment(),
                                enrolled
                        });
                    }
                    lblCourseCount.setText(courses.size() + " courses available");
                } catch (Exception ex) {
                    logger.error("Failed to load courses", ex);
                    JOptionPane.showMessageDialog(StudentEnrollPanel.this, "Failed to load courses.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadEnrollments() {
        new SwingWorker<List<Enrollment>, Void>() {
            @Override
            protected List<Enrollment> doInBackground() {
                return enrollmentService.getEnrollmentsByStudent(studentId);
            }

            @Override
            protected void done() {
                try {
                    List<Enrollment> enrollments = get();
                    enrollmentsTableModel.setRowCount(0);
                    for (Enrollment e : enrollments) {
                        enrollmentsTableModel.addRow(new Object[]{
                                e.getEnrollmentId(),
                                e.getCourseName(),
                                e.getCourseCode(),
                                e.getSemester(),
                                e.getAcademicYear(),
                                e.getGrade() != null ? e.getGrade() : "-",
                                e.getStatus()
                        });
                    }
                    double gpa = calculateGPA(enrollments);
                    String gpaText = gpa >= 0 ? String.format("%.2f", gpa) : "--";
                    lblEnrollmentSummary.setText(enrollments.size() + " enrollments | GPA: " + gpaText);
                } catch (Exception ex) {
                    logger.error("Failed to load enrollments", ex);
                    JOptionPane.showMessageDialog(StudentEnrollPanel.this, "Failed to load enrollments.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private double calculateGPA(List<Enrollment> enrollments) {
        double total = 0;
        int count = 0;
        for (Enrollment e : enrollments) {
            if (e.getGrade() != null && !e.getGrade().isEmpty() && !e.getGrade().equals("-")) {
                double points = gradeToPoints(e.getGrade());
                if (points >= 0) {
                    total += points;
                    count++;
                }
            }
        }
        return count > 0 ? total / count : -1;
    }

    private double gradeToPoints(String grade) {
        switch (grade) {
            case "A+": return 4.0;
            case "A":  return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B":  return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C":  return 2.0;
            case "C-": return 1.7;
            case "D+": return 1.3;
            case "D":  return 1.0;
            case "F":  return 0.0;
            default:   return -1;
        }
    }

    private void enrollInSelectedCourse() {
        int row = coursesTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to enroll in.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId = ((Number) coursesTableModel.getValueAt(row, 0)).intValue();
        String courseName = (String) coursesTableModel.getValueAt(row, 2);
        String courseCode = (String) coursesTableModel.getValueAt(row, 1);
        int maxEnrollment = ((Number) coursesTableModel.getValueAt(row, 6)).intValue();
        int currentEnrolled = ((Number) coursesTableModel.getValueAt(row, 7)).intValue();

        if (currentEnrolled >= maxEnrollment) {
            JOptionPane.showMessageDialog(this, "This course has reached its maximum enrollment capacity (" + maxEnrollment + ").", "Course Full", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = createDialog("Enroll in " + courseName, 450, 300);
        JPanel form = createFormPanel();

        JComboBox<String> cmbSemester = new JComboBox<>(lookupDao.getOptionsArray("semester"));
        JComboBox<String> cmbYear = new JComboBox<>(lookupDao.getOptionsArray("academic_year"));

        addFormRow(form, 0, "Semester *:", cmbSemester);
        addFormRow(form, 1, "Academic Year *:", cmbYear);

        JButton btnConfirm = new JButton("Confirm Enrollment");
        Constants.styleButton(btnConfirm, Constants.SUCCESS_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));

        btnConfirm.addActionListener(e -> {
            String semester = (String) cmbSemester.getSelectedItem();
            String academicYear = (String) cmbYear.getSelectedItem();

            if (semester == null || semester.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please select a semester.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (academicYear == null || academicYear.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please select an academic year.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (enrollmentService.isDuplicateEnrollment(studentId, courseId, semester, academicYear, -1)) {
                    JOptionPane.showMessageDialog(dialog, "You are already enrolled in this course for " + semester + " " + academicYear + ".", "Duplicate Enrollment", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Enrollment enrollment = new Enrollment();
                enrollment.setStudentId(studentId);
                enrollment.setCourseId(courseId);
                enrollment.setEnrollmentDate(Date.valueOf(LocalDate.now()));
                enrollment.setSemester(semester);
                enrollment.setAcademicYear(academicYear);
                enrollment.setGrade(null);
                enrollment.setStatus("Enrolled");

                if (enrollmentService.addEnrollment(enrollment)) {
                    JOptionPane.showMessageDialog(dialog, "Successfully enrolled in " + courseName + "!", "Enrollment Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadCourses();
                    loadEnrollments();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to enroll. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Failed to enroll in course", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());

        addDialogButtons(dialog, form, btnConfirm, btnCancel);
    }

    private JDialog createDialog(String title, int w, int h) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        d.setSize(w, h);
        d.setLocationRelativeTo(this);
        return d;
    }

    private JPanel createFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Constants.CARD_BG);
        p.setBorder(new EmptyBorder(20, 25, 10, 25));
        return p;
    }

    private void addFormRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(Constants.LABEL_BOLD);
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        Constants.styleField(field);
        panel.add(field, gbc);
    }

    private void addDialogButtons(JDialog dialog, JPanel form, JButton b1, JButton b2) {
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bp.setBackground(Constants.LIGHT_COLOR);
        bp.add(b1);
        bp.add(b2);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(bp, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private class GradeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                String grade = value != null ? value.toString() : "";
                if (grade.equals("A+") || grade.equals("A") || grade.equals("A-")) {
                    c.setForeground(new Color(46, 160, 67));
                } else if (grade.equals("B+") || grade.equals("B") || grade.equals("B-")) {
                    c.setForeground(new Color(25, 118, 210));
                } else if (grade.equals("C+") || grade.equals("C") || grade.equals("C-")) {
                    c.setForeground(new Color(245, 166, 35));
                } else if (grade.equals("D+") || grade.equals("D") || grade.equals("F")) {
                    c.setForeground(new Color(211, 47, 47));
                } else {
                    c.setForeground(Constants.TEXT_SECONDARY);
                }
                c.setBackground(Constants.CARD_BG);
            }
            setHorizontalAlignment(CENTER);
            return c;
        }
    }
}
