package com.university.view;

import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.Course;
import com.university.model.Department;
import com.university.model.Lecturer;
import com.university.service.CourseService;
import com.university.service.CourseServiceImpl;
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
import java.util.List;

public class CoursePanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(CoursePanel.class);

    private final CourseService courseService = new CourseServiceImpl();
    private final DepartmentService departmentService = new DepartmentServiceImpl();
    private final LecturerService lecturerService = new LecturerServiceImpl();
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Department> departments;
    private List<Lecturer> lecturers;
    private JLabel lblRecordCount;
    private JTextField txtSearch;

    public CoursePanel() {
        departments = departmentService.getAllDepartments();
        lecturers = lecturerService.getAllLecturers();
        initUI();
        loadCourses();
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
        JLabel lblTitle = new JLabel("Course Management");
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
        txtSearch.setToolTipText("Search by name, code, or ID...");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { searchCourses(); }
        });

        JButton btnSearch = new JButton("Search");
        Constants.styleButton(btnSearch, Constants.PRIMARY_COLOR);

        JButton btnAdd = new JButton("+ Add Course");
        Constants.styleButton(btnAdd, Constants.SUCCESS_COLOR);
        JButton btnRefresh = new JButton("Refresh");
        Constants.styleButton(btnRefresh, new Color(108, 117, 125));
        btnSearch.addActionListener(e -> searchCourses());
        btnAdd.addActionListener(e -> openAddDialog());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadCourses(); });

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
        String[] columns = {"ID", "Course Name", "Code", "Credits", "Department", "Lecturer", "Description", "Max Size"};
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

    private void loadCourses() {
        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() {
                departments = departmentService.getAllDepartments();
                lecturers = lecturerService.getAllLecturers();
                return courseService.getAllCourses();
            }

            @Override
            protected void done() {
                try {
                    List<Course> courses = get();
                    tableModel.setRowCount(0);
                    for (Course c : courses) {
                        tableModel.addRow(new Object[]{
                                c.getCourseId(), c.getCourseName(), c.getCourseCode(), c.getCredits(),
                                c.getDepartmentName(), c.getLecturerName(), c.getDescription(), c.getMaxEnrollment()
                        });
                    }
                    lblRecordCount.setText(courses.size() + " records found");
                } catch (Exception ex) {
                    logger.error("Error loading courses", ex);
                    JOptionPane.showMessageDialog(CoursePanel.this, "An error occurred while loading courses.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void searchCourses() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) { loadCourses(); return; }
        new SwingWorker<List<Course>, Void>() {
            @Override
            protected List<Course> doInBackground() {
                return courseService.searchCourses(keyword);
            }

            @Override
            protected void done() {
                try {
                    List<Course> courses = get();
                    tableModel.setRowCount(0);
                    for (Course c : courses) {
                        tableModel.addRow(new Object[]{
                                c.getCourseId(), c.getCourseName(), c.getCourseCode(), c.getCredits(),
                                c.getDepartmentName(), c.getLecturerName(), c.getDescription(), c.getMaxEnrollment()
                        });
                    }
                    lblRecordCount.setText(courses.size() + " records found");
                } catch (Exception ex) {
                    logger.error("Error searching courses", ex);
                    JOptionPane.showMessageDialog(CoursePanel.this, "An error occurred while searching.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public void openAddDialog() {
        JDialog dialog = createDialog("Add Course", 520, 550);
        JPanel form = createFormPanel();
        JTextField txtName = new JTextField(20);
        JTextField txtCode = new JTextField(20);
        JTextField txtCredits = new JTextField("3", 20);
        JComboBox<Department> cmbDept = new JComboBox<>();
        for (Department d : departments) cmbDept.addItem(d);
        JComboBox<Lecturer> cmbLec = new JComboBox<>();
        for (Lecturer l : lecturers) cmbLec.addItem(l);
        JTextField txtDesc = new JTextField(20);
        JTextField txtMax = new JTextField("50", 20);

        int row = 0;
        addFormRow(form, row++, "Course Name *:", txtName);
        addFormRow(form, row++, "Course Code *:", txtCode);
        addFormRow(form, row++, "Credits *:", txtCredits);
        addFormRow(form, row++, "Department:", cmbDept);
        addFormRow(form, row++, "Lecturer:", cmbLec);
        addFormRow(form, row++, "Description:", txtDesc);
        addFormRow(form, row++, "Max Enrollment:", txtMax);

        JButton btnSave = new JButton("Save");
        Constants.styleButton(btnSave, Constants.SUCCESS_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));

        btnSave.addActionListener(e -> {
            try {
                Course course = new Course();
                course.setCourseName(txtName.getText().trim());
                course.setCourseCode(txtCode.getText().trim());
                course.setCredits(Integer.parseInt(txtCredits.getText().trim()));
                course.setDepartmentId(((Department) cmbDept.getSelectedItem()).getDepartmentId());
                course.setLecturerId(((Lecturer) cmbLec.getSelectedItem()).getLecturerId());
                course.setDescription(txtDesc.getText().trim());
                course.setMaxEnrollment(Integer.parseInt(txtMax.getText().trim()));
                if (courseService.addCourse(course)) {
                    JOptionPane.showMessageDialog(dialog, "Course added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCourses(); dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed. Code may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DuplicateEntityException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Error adding course", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        addDialogButtons(dialog, form, btnSave, btnCancel);
    }

    private void editSelected() {
        int id = getSelectedId();
        if (id == -1) { JOptionPane.showMessageDialog(this, "Please select a course."); return; }
        Course course = courseService.getCourseById(id);
        if (course == null) return;

        JDialog dialog = createDialog("Edit Course", 520, 550);
        JPanel form = createFormPanel();
        JTextField txtName = new JTextField(course.getCourseName(), 20);
        JTextField txtCode = new JTextField(course.getCourseCode(), 20);
        JTextField txtCredits = new JTextField(String.valueOf(course.getCredits()), 20);
        JComboBox<Department> cmbDept = new JComboBox<>();
        for (Department d : departments) { cmbDept.addItem(d); if (d.getDepartmentId() == course.getDepartmentId()) cmbDept.setSelectedItem(d); }
        JComboBox<Lecturer> cmbLec = new JComboBox<>();
        for (Lecturer l : lecturers) { cmbLec.addItem(l); if (l.getLecturerId() == course.getLecturerId()) cmbLec.setSelectedItem(l); }
        JTextField txtDesc = new JTextField(course.getDescription(), 20);
        JTextField txtMax = new JTextField(String.valueOf(course.getMaxEnrollment()), 20);

        int r = 0;
        addFormRow(form, r++, "Course Name *:", txtName);
        addFormRow(form, r++, "Course Code *:", txtCode);
        addFormRow(form, r++, "Credits *:", txtCredits);
        addFormRow(form, r++, "Department:", cmbDept);
        addFormRow(form, r++, "Lecturer:", cmbLec);
        addFormRow(form, r++, "Description:", txtDesc);
        addFormRow(form, r++, "Max Enrollment:", txtMax);

        JButton btnUpdate = new JButton("Update");
        Constants.styleButton(btnUpdate, Constants.PRIMARY_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));
        btnUpdate.addActionListener(e -> {
            try {
                course.setCourseName(txtName.getText().trim());
                course.setCourseCode(txtCode.getText().trim());
                course.setCredits(Integer.parseInt(txtCredits.getText().trim()));
                course.setDepartmentId(((Department) cmbDept.getSelectedItem()).getDepartmentId());
                course.setLecturerId(((Lecturer) cmbLec.getSelectedItem()).getLecturerId());
                course.setDescription(txtDesc.getText().trim());
                course.setMaxEnrollment(Integer.parseInt(txtMax.getText().trim()));
                if (courseService.updateCourse(course)) {
                    JOptionPane.showMessageDialog(dialog, "Updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCourses(); dialog.dispose();
                } else { JOptionPane.showMessageDialog(dialog, "Failed.", "Error", JOptionPane.ERROR_MESSAGE); }
            } catch (DuplicateEntityException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Error updating course", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        addDialogButtons(dialog, form, btnUpdate, btnCancel);
    }

    private void deleteSelected() {
        int id = getSelectedId();
        if (id == -1) { JOptionPane.showMessageDialog(this, "Please select a course."); return; }
        int row = table.getSelectedRow();
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete course: " + name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (courseService.deleteCourse(id)) { JOptionPane.showMessageDialog(this, "Deleted!", "Success", JOptionPane.INFORMATION_MESSAGE); loadCourses(); }
            else { JOptionPane.showMessageDialog(this, "Failed.", "Error", JOptionPane.ERROR_MESSAGE); }
        }
    }

    private void viewSelected() {
        int id = getSelectedId();
        if (id == -1) { JOptionPane.showMessageDialog(this, "Please select a course."); return; }
        Course c = courseService.getCourseById(id);
        if (c == null) return;

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        infoPanel.setBackground(Constants.CARD_BG);
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        addInfoRow(infoPanel, "Course ID:", String.valueOf(c.getCourseId()));
        addInfoRow(infoPanel, "Course Name:", c.getCourseName());
        addInfoRow(infoPanel, "Course Code:", c.getCourseCode());
        addInfoRow(infoPanel, "Credits:", String.valueOf(c.getCredits()));
        addInfoRow(infoPanel, "Department:", c.getDepartmentName() != null ? c.getDepartmentName() : "N/A");
        addInfoRow(infoPanel, "Lecturer:", c.getLecturerName() != null ? c.getLecturerName() : "N/A");
        addInfoRow(infoPanel, "Description:", c.getDescription() != null ? c.getDescription() : "N/A");
        addInfoRow(infoPanel, "Max Enrollment:", String.valueOf(c.getMaxEnrollment()));
        JOptionPane.showMessageDialog(this, infoPanel, "Course Details - " + c.getCourseName(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label); lbl.setFont(Constants.LABEL_BOLD); lbl.setForeground(Constants.TEXT_SECONDARY); panel.add(lbl);
        JLabel v = new JLabel(value); v.setFont(Constants.FIELD_FONT); v.setForeground(Constants.TEXT_PRIMARY); panel.add(v);
    }

    private JDialog createDialog(String title, int w, int h) { JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true); d.setSize(w, h); d.setLocationRelativeTo(this); return d; }
    private JPanel createFormPanel() { JPanel p = new JPanel(new GridBagLayout()); p.setBackground(Constants.CARD_BG); p.setBorder(new EmptyBorder(20, 25, 10, 25)); return p; }
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
