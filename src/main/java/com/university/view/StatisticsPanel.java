package com.university.view;

import com.university.service.StatisticsService;
import com.university.service.StatisticsServiceImpl;
import com.university.utility.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StatisticsPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsPanel.class);

    private final StatisticsService statisticsService = new StatisticsServiceImpl();

    private JLabel lblTotalStudents;
    private JLabel lblTotalLecturers;
    private JLabel lblTotalCourses;
    private JLabel lblTotalDepartments;
    private JLabel lblTotalEnrollments;
    private DefaultTableModel courseTableModel;
    private DefaultTableModel deptTableModel;

    public StatisticsPanel() {
        initUI();
        refreshData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Constants.LIGHT_COLOR);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Constants.LIGHT_COLOR);

        JLabel lblTitle = new JLabel("Dashboard");
        lblTitle.setFont(Constants.TITLE_FONT);
        lblTitle.setForeground(Constants.TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JLabel lblSubtitle = new JLabel("Overview of university data");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(Constants.TEXT_SECONDARY);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(Constants.LIGHT_COLOR);

        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        statsPanel.setBackground(Constants.LIGHT_COLOR);
        statsPanel.setPreferredSize(new Dimension(0, 110));

        JPanel cardStudents = createStatCard("Total Students", "0", Constants.PRIMARY_COLOR, "STU");
        JPanel cardLecturers = createStatCard("Total Lecturers", "0", Constants.SUCCESS_COLOR, "LEC");
        JPanel cardCourses = createStatCard("Total Courses", "0", Constants.WARNING_COLOR, "CRS");
        JPanel cardDepartments = createStatCard("Departments", "0", new Color(156, 39, 176), "DEP");
        JPanel cardEnrollments = createStatCard("Enrollments", "0", Constants.INFO_COLOR, "ENR");

        statsPanel.add(cardStudents);
        statsPanel.add(cardLecturers);
        statsPanel.add(cardCourses);
        statsPanel.add(cardDepartments);
        statsPanel.add(cardEnrollments);

        centerPanel.add(statsPanel, BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        tablesPanel.setBackground(Constants.LIGHT_COLOR);

        JPanel leftPanel = createTableCard("Students per Course");
        String[] courseColumns = {"Course Code", "Course Name", "Students"};
        courseTableModel = new DefaultTableModel(courseColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable courseTable = new JTable(courseTableModel);
        Constants.styleTable(courseTable);
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        courseTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        leftPanel.add(new JScrollPane(courseTable), BorderLayout.CENTER);

        JPanel rightPanel = createTableCard("Students per Department");
        String[] deptColumns = {"Dept Code", "Department Name", "Students"};
        deptTableModel = new DefaultTableModel(deptColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable deptTable = new JTable(deptTableModel);
        Constants.styleTable(deptTable);
        deptTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        deptTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        deptTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        rightPanel.add(new JScrollPane(deptTable), BorderLayout.CENTER);

        tablesPanel.add(leftPanel);
        tablesPanel.add(rightPanel);
        centerPanel.add(tablesPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        new SwingWorker<Void, Void>() {
            private int totalStudents;
            private int totalLecturers;
            private int totalCourses;
            private int totalDepartments;
            private int totalEnrollments;
            private List<Object[]> courseData;
            private List<Object[]> deptData;

            @Override
            protected Void doInBackground() {
                totalStudents = statisticsService.getStudentCount();
                totalLecturers = statisticsService.getLecturerCount();
                totalCourses = statisticsService.getCourseCount();
                totalDepartments = statisticsService.getDepartmentCount();
                totalEnrollments = statisticsService.getEnrollmentCount();
                courseData = statisticsService.getStudentsPerCourse();
                deptData = statisticsService.getStudentsPerDepartment();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    lblTotalStudents.setText(String.valueOf(totalStudents));
                    lblTotalLecturers.setText(String.valueOf(totalLecturers));
                    lblTotalCourses.setText(String.valueOf(totalCourses));
                    lblTotalDepartments.setText(String.valueOf(totalDepartments));
                    lblTotalEnrollments.setText(String.valueOf(totalEnrollments));

                    courseTableModel.setRowCount(0);
                    for (Object[] row : courseData) {
                        courseTableModel.addRow(row);
                    }

                    deptTableModel.setRowCount(0);
                    for (Object[] row : deptData) {
                        deptTableModel.addRow(row);
                    }
                } catch (Exception ex) {
                    logger.error("Failed to refresh statistics", ex);
                    JOptionPane.showMessageDialog(StatisticsPanel.this, "Failed to load statistics.");
                }
            }
        }.execute();
    }

    private JPanel createTableCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                new EmptyBorder(12, 15, 12, 15)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Constants.HEADING_FONT);
        lblTitle.setForeground(Constants.TEXT_PRIMARY);
        card.add(lblTitle, BorderLayout.NORTH);

        return card;
    }

    private JPanel createStatCard(String title, String value, Color color, String prefix) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                new EmptyBorder(15, 18, 15, 18)));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(4, 0));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(Constants.STAT_NUMBER);
        lblValue.setForeground(color);

        JLabel lblLabel = new JLabel(title);
        lblLabel.setFont(Constants.STAT_LABEL);
        lblLabel.setForeground(Constants.TEXT_SECONDARY);

        textPanel.add(lblValue);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblLabel);

        card.add(colorBar, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        switch (prefix) {
            case "STU": lblTotalStudents = lblValue; break;
            case "LEC": lblTotalLecturers = lblValue; break;
            case "CRS": lblTotalCourses = lblValue; break;
            case "DEP": lblTotalDepartments = lblValue; break;
            case "ENR": lblTotalEnrollments = lblValue; break;
        }

        return card;
    }
}
