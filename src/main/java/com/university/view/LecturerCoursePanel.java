package com.university.view;

import com.university.model.Course;
import com.university.model.Enrollment;
import com.university.service.CourseService;
import com.university.service.CourseServiceImpl;
import com.university.service.EnrollmentService;
import com.university.service.EnrollmentServiceImpl;
import com.university.utility.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class LecturerCoursePanel extends JPanel {

    private final int lecturerId;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    private DefaultTableModel courseTableModel;
    private JTable courseTable;
    private DefaultTableModel enrollmentTableModel;
    private JTable enrollmentTable;
    private JLabel courseCountLabel;
    private JLabel selectedCourseLabel;
    private JButton refreshButton;
    private JButton updateGradeButton;

    private List<Course> currentCourses;

    public LecturerCoursePanel(int lecturerId) {
        this.lecturerId = lecturerId;
        this.courseService = new CourseServiceImpl();
        this.enrollmentService = new EnrollmentServiceImpl();

        setLayout(new BorderLayout());
        setBackground(Constants.CARD_BG);

        initializeUI();
        loadData();
    }

    private void initializeUI() {
        JPanel topPanel = createTopSection();
        JPanel bottomPanel = createBottomSection();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTopSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Constants.CARD_BG);
        panel.setBorder(new EmptyBorder(15, 15, 10, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.CARD_BG);

        JLabel titleLabel = new JLabel("My Courses");
        titleLabel.setFont(Constants.TITLE_FONT);
        titleLabel.setForeground(Constants.TEXT_PRIMARY);

        courseCountLabel = new JLabel("0 courses assigned");
        courseCountLabel.setFont(Constants.LABEL_FONT);
        courseCountLabel.setForeground(Constants.TEXT_SECONDARY);

        refreshButton = new JButton("Refresh");
        Constants.styleButton(refreshButton, Constants.PRIMARY_COLOR);
        refreshButton.setFont(Constants.BUTTON_FONT);
        refreshButton.addActionListener(e -> loadData());

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(Constants.CARD_BG);
        titleRow.add(titleLabel, BorderLayout.WEST);
        titleRow.add(refreshButton, BorderLayout.EAST);

        headerPanel.add(titleRow, BorderLayout.NORTH);
        headerPanel.add(courseCountLabel, BorderLayout.CENTER);

        String[] courseColumns = {"ID", "Course Code", "Course Name", "Credits", "Department", "Enrolled/Max", "Description"};
        courseTableModel = new DefaultTableModel(courseColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(courseTableModel);
        Constants.styleTable(courseTable);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onCourseSelected();
            }
        });

        JScrollPane courseScrollPane = new JScrollPane(courseTable);
        courseScrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1));
        courseScrollPane.getViewport().setBackground(Constants.CARD_BG);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(courseScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Constants.CARD_BG);
        panel.setBorder(new EmptyBorder(10, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.CARD_BG);

        selectedCourseLabel = new JLabel("Select a course to view enrolled students");
        selectedCourseLabel.setFont(Constants.HEADING_FONT);
        selectedCourseLabel.setForeground(Constants.TEXT_PRIMARY);

        updateGradeButton = new JButton("Update Grade");
        Constants.styleButton(updateGradeButton, Constants.SUCCESS_COLOR);
        updateGradeButton.setFont(Constants.BUTTON_FONT);
        updateGradeButton.setEnabled(false);
        updateGradeButton.addActionListener(e -> openGradeDialog());

        JPanel bottomHeaderRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomHeaderRow.setBackground(Constants.CARD_BG);
        bottomHeaderRow.add(selectedCourseLabel);

        headerPanel.add(bottomHeaderRow, BorderLayout.WEST);
        headerPanel.add(updateGradeButton, BorderLayout.EAST);

        String[] enrollmentColumns = {"ID", "Student", "Course", "Semester", "Year", "Grade", "Status"};
        enrollmentTableModel = new DefaultTableModel(enrollmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enrollmentTable = new JTable(enrollmentTableModel);
        Constants.styleTable(enrollmentTable);
        enrollmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enrollmentTable.getSelectionModel().addListSelectionListener(e -> {
            updateGradeButton.setEnabled(enrollmentTable.getSelectedRow() >= 0);
        });

        JScrollPane enrollmentScrollPane = new JScrollPane(enrollmentTable);
        enrollmentScrollPane.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1));
        enrollmentScrollPane.getViewport().setBackground(Constants.CARD_BG);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(enrollmentScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadData() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        refreshButton.setEnabled(false);

        new SwingWorker<List<Course>, Void>() {
            private Map<Integer, Integer> courseCounts = java.util.Collections.emptyMap();

            @Override
            protected List<Course> doInBackground() {
                List<Course> courses = courseService.getCoursesByLecturer(lecturerId);
                courseCounts = enrollmentService.getEnrollmentCountsByCourse();
                return courses;
            }

            @Override
            protected void done() {
                try {
                    currentCourses = get();
                    populateCourseTable(courseCounts);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LecturerCoursePanel.this,
                            "Error loading courses: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    refreshButton.setEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }

    private void populateCourseTable(Map<Integer, Integer> counts) {
        courseTableModel.setRowCount(0);
        if (currentCourses != null) {
            for (Course course : currentCourses) {
                int enrolled = counts.getOrDefault(course.getCourseId(), 0);
                String enrolledMax = enrolled + "/" + course.getMaxEnrollment();
                courseTableModel.addRow(new Object[]{
                        course.getCourseId(),
                        course.getCourseCode(),
                        course.getCourseName(),
                        course.getCredits(),
                        course.getDepartmentName(),
                        enrolledMax,
                        course.getDescription()
                });
            }
            courseCountLabel.setText(currentCourses.size() + " courses assigned");
        }
    }

    private void refreshCourseCounts() {
        new SwingWorker<Map<Integer, Integer>, Void>() {
            @Override
            protected Map<Integer, Integer> doInBackground() {
                return enrollmentService.getEnrollmentCountsByCourse();
            }

            @Override
            protected void done() {
                try {
                    populateCourseTable(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LecturerCoursePanel.this,
                            "Error refreshing course counts: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void onCourseSelected() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow < 0) {
            enrollmentTableModel.setRowCount(0);
            selectedCourseLabel.setText("Select a course to view enrolled students");
            updateGradeButton.setEnabled(false);
            return;
        }

        int courseId = (int) courseTableModel.getValueAt(selectedRow, 0);
        String courseName = (String) courseTableModel.getValueAt(selectedRow, 2);
        selectedCourseLabel.setText("Enrolled Students - " + courseName);
        updateGradeButton.setEnabled(false);

        loadEnrollments(courseId);
    }

    private void loadEnrollments(int courseId) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<List<Enrollment>, Void>() {
            @Override
            protected List<Enrollment> doInBackground() {
                return enrollmentService.getEnrollmentsByCourse(courseId);
            }

            @Override
            protected void done() {
                try {
                    List<Enrollment> enrollments = get();
                    populateEnrollmentTable(enrollments);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LecturerCoursePanel.this,
                            "Error loading enrollments: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }

    private void populateEnrollmentTable(List<Enrollment> enrollments) {
        enrollmentTableModel.setRowCount(0);
        if (enrollments != null) {
            for (Enrollment enrollment : enrollments) {
                enrollmentTableModel.addRow(new Object[]{
                        enrollment.getEnrollmentId(),
                        enrollment.getStudentName(),
                        enrollment.getCourseName(),
                        enrollment.getSemester(),
                        enrollment.getAcademicYear(),
                        enrollment.getGrade(),
                        enrollment.getStatus()
                });
            }
        }
    }

    private void openGradeDialog() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student enrollment to update.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int enrollmentId = (int) enrollmentTableModel.getValueAt(selectedRow, 0);
        String studentName = (String) enrollmentTableModel.getValueAt(selectedRow, 1);
        String courseName = (String) enrollmentTableModel.getValueAt(selectedRow, 2);
        String currentGrade = (String) enrollmentTableModel.getValueAt(selectedRow, 5);
        String currentStatus = (String) enrollmentTableModel.getValueAt(selectedRow, 6);

        String[] gradeOptions = {"", "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F"};
        String[] statusOptions = {"Enrolled", "Completed", "Dropped", "Failed"};

        JPanel dialogPanel = new JPanel(new GridBagLayout());
        dialogPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        dialogPanel.setBackground(Constants.CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialogPanel.add(new JLabel("Student:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel studentLabel = new JLabel(studentName);
        studentLabel.setFont(Constants.LABEL_BOLD);
        dialogPanel.add(studentLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        dialogPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel courseLabel = new JLabel(courseName);
        courseLabel.setFont(Constants.LABEL_BOLD);
        dialogPanel.add(courseLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        dialogPanel.add(new JLabel("Grade:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JComboBox<String> gradeCombo = new JComboBox<>(gradeOptions);
        gradeCombo.setFont(Constants.FIELD_FONT);
        if (currentGrade != null && !currentGrade.isEmpty()) {
            gradeCombo.setSelectedItem(currentGrade);
        }
        dialogPanel.add(gradeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        dialogPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        statusCombo.setFont(Constants.FIELD_FONT);
        if (currentStatus != null && !currentStatus.isEmpty()) {
            statusCombo.setSelectedItem(currentStatus);
        }
        dialogPanel.add(statusCombo, gbc);

        int result = JOptionPane.showConfirmDialog(
                this,
                dialogPanel,
                "Update Grade - " + studentName,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String newGrade = (String) gradeCombo.getSelectedItem();
            String newStatus = (String) statusCombo.getSelectedItem();
            updateEnrollment(enrollmentId, newGrade, newStatus);
        }
    }

    private void updateEnrollment(int enrollmentId, String grade, String status) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        updateGradeButton.setEnabled(false);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                Enrollment enrollment = new Enrollment();
                enrollment.setEnrollmentId(enrollmentId);
                enrollment.setGrade(grade);
                enrollment.setStatus(status);
                return enrollmentService.updateEnrollment(enrollment);
            }

            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(LecturerCoursePanel.this,
                                "Grade updated successfully.",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        int selectedRow = courseTable.getSelectedRow();
                        if (selectedRow >= 0) {
                            int courseId = (int) courseTableModel.getValueAt(selectedRow, 0);
                            loadEnrollments(courseId);
                            refreshCourseCounts();
                        }
                    } else {
                        JOptionPane.showMessageDialog(LecturerCoursePanel.this,
                                "Failed to update grade.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LecturerCoursePanel.this,
                            "Error updating grade: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    updateGradeButton.setEnabled(enrollmentTable.getSelectedRow() >= 0);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }
}
