package com.university.view;

import com.university.dao.*;
import com.university.model.User;
import com.university.database.DBConnection;
import com.university.utility.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DashboardFrame extends JFrame {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DashboardFrame.class);
    private boolean darkMode = true;
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private StudentPanel studentPanel;
    private LecturerPanel lecturerPanel;
    private DepartmentPanel departmentPanel;
    private CoursePanel coursePanel;
    private EnrollmentPanel enrollmentPanel;
    private StatisticsPanel statisticsPanel;
    private ReportPanel reportPanel;
    private UserPanel userPanel;
    private LecturerCoursePanel lecturerCoursePanel;
    private StudentEnrollPanel studentEnrollPanel;
    private JPanel sidebarPanel;
    private String activePanel = "STATISTICS";
    private Map<String, JLabel> sidebarLabels = new LinkedHashMap<>();
    private Map<String, JPanel> sidebarItems = new LinkedHashMap<>();

    public DashboardFrame(User user) {
        this.currentUser = user;
        initUI();
    }

    private boolean isAdmin() {
        return "Admin".equals(currentUser.getRole()) || "Staff".equals(currentUser.getRole());
    }

    private boolean isStudent() {
        return "Student".equals(currentUser.getRole());
    }

    private boolean isLecturer() {
        return "Lecturer".equals(currentUser.getRole());
    }

    private void initUI() {
        setTitle(Constants.APP_NAME);
        setSize(1280, 750);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                DBConnection.shutdown();
                System.exit(0);
            }
        });
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1100, 650));

        createMenuBar();

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Constants.LIGHT_COLOR);

        JPanel topBar = createTopBar();
        rootPanel.add(topBar, BorderLayout.NORTH);

        JPanel mainArea = new JPanel(new BorderLayout(0, 0));
        mainArea.setBackground(Constants.LIGHT_COLOR);

        sidebarPanel = createSidebar();
        mainArea.add(sidebarPanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Constants.LIGHT_COLOR);

        JPanel loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.setBackground(Constants.LIGHT_COLOR);
        JLabel loadingLabel = new JLabel("Loading Dashboard...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loadingPanel.add(loadingLabel, BorderLayout.CENTER);
        contentPanel.add(loadingPanel, "LOADING");
        cardLayout.show(contentPanel, "LOADING");

        mainArea.add(contentPanel, BorderLayout.CENTER);
        rootPanel.add(mainArea, BorderLayout.CENTER);
        add(rootPanel);

        loadPanelInBackground("STATISTICS");
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Constants.PRIMARY_COLOR);
        topBar.setPreferredSize(new Dimension(0, 55));
        topBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        JPanel leftSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftSection.setOpaque(false);

        JLabel lblApp = new JLabel(Constants.APP_NAME);
        lblApp.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblApp.setForeground(Color.WHITE);
        leftSection.add(lblApp);

        JLabel lblSep = new JLabel("|");
        lblSep.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblSep.setForeground(new Color(255, 255, 255, 120));
        leftSection.add(lblSep);

        String roleDisplay = currentUser.getRole();
        JLabel lblRole = new JLabel(roleDisplay);
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRole.setForeground(new Color(200, 230, 255));
        leftSection.add(lblRole);

        topBar.add(leftSection, BorderLayout.WEST);

        JPanel rightSection = new JPanel();
        rightSection.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightSection.setOpaque(false);

        JLabel lblUser = new JLabel("  " + currentUser.getFullName());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(Color.WHITE);
        rightSection.add(lblUser);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(Constants.BUTTON_FONT);
        btnLogout.setBackground(new Color(255, 193, 7));
        btnLogout.setForeground(Constants.TEXT_PRIMARY);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(true);
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 165, 32), 1),
                new EmptyBorder(6, 18, 6, 18)));
        btnLogout.setOpaque(true);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogout.setBackground(new Color(218, 165, 32)); }
            public void mouseExited(MouseEvent e) { btnLogout.setBackground(new Color(255, 193, 7)); }
        });
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Confirm Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });
        rightSection.add(btnLogout);

        topBar.add(rightSection, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Constants.DARK_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel lblMenu = new JLabel("  Navigation");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMenu.setForeground(new Color(150, 160, 180));
        lblMenu.setMaximumSize(new Dimension(220, 30));
        lblMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblMenu);
        sidebar.add(Box.createVerticalStrut(8));

        if (isAdmin()) {
            addSidebarItem(sidebar, "STATISTICS", "Dashboard");
            addSidebarItem(sidebar, "STUDENTS", "Students");
            addSidebarItem(sidebar, "LECTURERS", "Lecturers");
            addSidebarItem(sidebar, "DEPARTMENTS", "Departments");
            addSidebarItem(sidebar, "COURSES", "Courses");
            addSidebarItem(sidebar, "ENROLLMENTS", "Enrollments");
            addSidebarItem(sidebar, "USERS", "User Management");
            addSidebarItem(sidebar, "REPORTS", "Reports");
        } else if (isLecturer()) {
            addSidebarItem(sidebar, "STATISTICS", "Dashboard");
            addSidebarItem(sidebar, "LECTURER_COURSES", "My Courses & Grades");
            addSidebarItem(sidebar, "REPORTS", "Reports");
        } else if (isStudent()) {
            addSidebarItem(sidebar, "STATISTICS", "Dashboard");
            addSidebarItem(sidebar, "STUDENT_ENROLL", "Courses & Enrollments");
        }

        sidebar.add(Box.createVerticalGlue());

        JPanel userCard = new JPanel();
        userCard.setLayout(new BoxLayout(userCard, BoxLayout.Y_AXIS));
        userCard.setBackground(new Color(255, 255, 255, 10));
        userCard.setBorder(new EmptyBorder(10, 12, 10, 12));
        userCard.setMaximumSize(new Dimension(220, 60));
        userCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUserName = new JLabel(currentUser.getFullName());
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUserName.setForeground(Color.WHITE);
        userCard.add(lblUserName);

        JLabel lblUserRole = new JLabel(currentUser.getRole());
        lblUserRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUserRole.setForeground(new Color(150, 170, 200));
        userCard.add(lblUserRole);

        sidebar.add(userCard);

        return sidebar;
    }

    private void addSidebarItem(JPanel sidebar, String panelKey, String label) {
        JPanel item = new JPanel(new BorderLayout());
        item.setMaximumSize(new Dimension(220, 42));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setBackground(new Color(33, 40, 56));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.setBorder(new EmptyBorder(0, 15, 0, 10));

        String iconText;
        switch (panelKey) {
            case "STATISTICS":     iconText = "\u2302"; break;
            case "STUDENTS":       iconText = "\u263A"; break;
            case "LECTURERS":      iconText = "\u2605"; break;
            case "DEPARTMENTS":    iconText = "\u2302"; break;
            case "COURSES":        iconText = "\u2660"; break;
            case "ENROLLMENTS":    iconText = "\u2630"; break;
            case "USERS":          iconText = "\u2699"; break;
            case "REPORTS":        iconText = "\u2616"; break;
            case "LECTURER_COURSES": iconText = "\u2660"; break;
            case "STUDENT_ENROLL":   iconText = "\u2630"; break;
            default:               iconText = "\u25A0"; break;
        }

        JLabel lblIcon = new JLabel(iconText);
        lblIcon.setFont(new Font("Segoe UI Symbols", Font.PLAIN, 14));
        lblIcon.setForeground(new Color(150, 160, 180));
        lblIcon.setPreferredSize(new Dimension(25, 0));
        item.add(lblIcon, BorderLayout.WEST);

        JLabel lblText = new JLabel(label);
        lblText.setFont(Constants.SIDEBAR_FONT);
        lblText.setForeground(new Color(200, 210, 225));
        item.add(lblText, BorderLayout.CENTER);

        sidebarLabels.put(panelKey, lblText);
        sidebarItems.put(panelKey, item);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPanel(panelKey);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!activePanel.equals(panelKey)) {
                    item.setBackground(new Color(45, 55, 75));
                    lblText.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!activePanel.equals(panelKey)) {
                    item.setBackground(new Color(33, 40, 56));
                    lblText.setForeground(new Color(200, 210, 225));
                }
            }
        });

        sidebar.add(item);
        sidebar.add(Box.createVerticalStrut(2));
    }

    private void highlightSidebar(String panelKey) {
        activePanel = panelKey;
        for (Map.Entry<String, JPanel> entry : sidebarItems.entrySet()) {
            JPanel itemPanel = entry.getValue();
            JLabel textLabel = sidebarLabels.get(entry.getKey());
            if (entry.getKey().equals(panelKey)) {
                itemPanel.setBackground(Constants.SIDEBAR_ACTIVE);
                textLabel.setForeground(Color.WHITE);
                textLabel.setFont(Constants.SIDEBAR_BOLD);
            } else {
                itemPanel.setBackground(new Color(33, 40, 56));
                textLabel.setForeground(new Color(200, 210, 225));
                textLabel.setFont(Constants.SIDEBAR_FONT);
            }
        }
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(Constants.MENU_FONT);
        menuBar.setBackground(Constants.CARD_BG);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Constants.BORDER_COLOR));

        JMenu menuHome = createStyledMenu("Home");
        JMenuItem itemDashboard = createMenuItem("Dashboard");
        JMenuItem itemLogout = createMenuItem("Logout");
        itemDashboard.addActionListener(e -> showPanel("STATISTICS"));
        itemLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
        menuHome.add(itemDashboard);
        menuHome.addSeparator();
        menuHome.add(itemLogout);
        menuBar.add(menuHome);

        if (isAdmin()) {
            JMenu menuStudents = createStyledMenu("Students");
            JMenuItem itemManageStudents = createMenuItem("Manage Students");
            JMenuItem itemRegisterStudent = createMenuItem("Register Student");
            itemManageStudents.addActionListener(e -> showPanel("STUDENTS"));
            itemRegisterStudent.addActionListener(e -> {
                showPanel("STUDENTS");
                SwingUtilities.invokeLater(() -> studentPanel.openAddDialog());
            });
            menuStudents.add(itemManageStudents);
            menuStudents.addSeparator();
            menuStudents.add(itemRegisterStudent);
            menuBar.add(menuStudents);

            JMenu menuLecturers = createStyledMenu("Lecturers");
            JMenuItem itemManageLecturers = createMenuItem("Manage Lecturers");
            JMenuItem itemAddLecturer = createMenuItem("Add Lecturer");
            itemManageLecturers.addActionListener(e -> showPanel("LECTURERS"));
            itemAddLecturer.addActionListener(e -> {
                showPanel("LECTURERS");
                SwingUtilities.invokeLater(() -> lecturerPanel.openAddDialog());
            });
            menuLecturers.add(itemManageLecturers);
            menuLecturers.addSeparator();
            menuLecturers.add(itemAddLecturer);
            menuBar.add(menuLecturers);

            JMenu menuAcademics = createStyledMenu("Academics");
            JMenuItem itemDepartments = createMenuItem("Departments");
            JMenuItem itemCourses = createMenuItem("Courses");
            itemDepartments.addActionListener(e -> showPanel("DEPARTMENTS"));
            itemCourses.addActionListener(e -> showPanel("COURSES"));
            menuAcademics.add(itemDepartments);
            menuAcademics.addSeparator();
            menuAcademics.add(itemCourses);
            menuBar.add(menuAcademics);

            JMenu menuEnrollments = createStyledMenu("Enrollments");
            JMenuItem itemManageEnrollments = createMenuItem("Manage Enrollments");
            JMenuItem itemNewEnrollment = createMenuItem("New Enrollment");
            itemManageEnrollments.addActionListener(e -> showPanel("ENROLLMENTS"));
            itemNewEnrollment.addActionListener(e -> {
                showPanel("ENROLLMENTS");
                SwingUtilities.invokeLater(() -> enrollmentPanel.openAddDialog());
            });
            menuEnrollments.add(itemManageEnrollments);
            menuEnrollments.addSeparator();
            menuEnrollments.add(itemNewEnrollment);
            menuBar.add(menuEnrollments);

            JMenu menuUsers = createStyledMenu("System");
            JMenuItem itemManageUsers = createMenuItem("User Management");
            itemManageUsers.addActionListener(e -> showPanel("USERS"));
            menuUsers.add(itemManageUsers);
            menuBar.add(menuUsers);

            JMenu menuReports = createStyledMenu("Reports");
            JMenuItem itemStatistics = createMenuItem("Statistics");
            JMenuItem itemReportGen = createMenuItem("Generate Reports");
            itemStatistics.addActionListener(e -> {
                showPanel("STATISTICS");
                if (statisticsPanel != null) statisticsPanel.refreshData();
            });
            itemReportGen.addActionListener(e -> {
                showPanel("REPORTS");
                if (reportPanel != null) reportPanel.refreshData();
            });
            menuReports.add(itemStatistics);
            menuReports.addSeparator();
            menuReports.add(itemReportGen);
            menuBar.add(menuReports);

        } else if (isLecturer()) {
            JMenu menuCourses = createStyledMenu("My Courses");
            JMenuItem itemMyCourses = createMenuItem("View My Courses & Grades");
            itemMyCourses.addActionListener(e -> showPanel("LECTURER_COURSES"));
            menuCourses.add(itemMyCourses);
            menuBar.add(menuCourses);

            JMenu menuReports = createStyledMenu("Reports");
            JMenuItem itemReportGen = createMenuItem("Generate Reports");
            itemReportGen.addActionListener(e -> {
                showPanel("REPORTS");
                if (reportPanel != null) reportPanel.refreshData();
            });
            menuReports.add(itemReportGen);
            menuBar.add(menuReports);

        } else if (isStudent()) {
            JMenu menuCourses = createStyledMenu("Courses");
            JMenuItem itemBrowseCourses = createMenuItem("Browse & Enroll");
            itemBrowseCourses.addActionListener(e -> showPanel("STUDENT_ENROLL"));
            menuCourses.add(itemBrowseCourses);
            menuBar.add(menuCourses);
        }

        JMenu menuView = createStyledMenu("View");
        JMenuItem itemToggleTheme = createMenuItem("Toggle Theme");
        itemToggleTheme.addActionListener(e -> toggleTheme());
        menuView.add(itemToggleTheme);
        menuBar.add(menuView);

        setJMenuBar(menuBar);
    }

    private JMenu createStyledMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setForeground(Constants.TEXT_PRIMARY);
        menu.setFont(Constants.MENU_BOLD);
        return menu;
    }

    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(Constants.MENU_FONT);
        return item;
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        try {
            Supplier<javax.swing.plaf.basic.BasicLookAndFeel> theme = darkMode ? FlatDarkLaf::new : FlatLightLaf::new;
            UIManager.setLookAndFeel(theme.get());
            SwingUtilities.updateComponentTreeUI(this);
            this.pack();
        } catch (Exception ex) {
            logger.error("Failed to switch theme", ex);
        }
    }

    public void showPanel(String name) {
        highlightSidebar(name);

        switch (name) {
            case "STATISTICS":
                if (statisticsPanel != null) { cardLayout.show(contentPanel, name); statisticsPanel.refreshData(); return; }
                break;
            case "STUDENTS":
                if (studentPanel != null) { cardLayout.show(contentPanel, name); return; }
                break;
            case "LECTURERS":
                if (lecturerPanel != null) { cardLayout.show(contentPanel, name); return; }
                break;
            case "DEPARTMENTS":
                if (departmentPanel != null) { cardLayout.show(contentPanel, name); return; }
                break;
            case "COURSES":
                if (coursePanel != null) { cardLayout.show(contentPanel, name); return; }
                break;
            case "ENROLLMENTS":
                if (enrollmentPanel != null) { cardLayout.show(contentPanel, name); return; }
                break;
            case "REPORTS":
                if (reportPanel != null) { cardLayout.show(contentPanel, name); reportPanel.refreshData(); return; }
                break;
            case "USERS":
                if (userPanel != null) { cardLayout.show(contentPanel, name); return; }
                break;
            case "LECTURER_COURSES":
                if (lecturerCoursePanel != null) { cardLayout.show(contentPanel, name); return; }
                break;
            case "STUDENT_ENROLL":
                if (studentEnrollPanel != null) { cardLayout.show(contentPanel, name); return; }
                break;
        }

        loadPanelInBackground(name);
    }

    private void loadPanelInBackground(String name) {
        JLabel spinner = new JLabel("Loading " + name + "...", SwingConstants.CENTER);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JPanel spinnerPanel = new JPanel(new BorderLayout());
        spinnerPanel.add(spinner, BorderLayout.CENTER);
        contentPanel.add(spinnerPanel, name + "_LOADING");
        cardLayout.show(contentPanel, name + "_LOADING");

        new SwingWorker<JPanel, Void>() {
            @Override
            protected JPanel doInBackground() {
                switch (name) {
                    case "STUDENTS":          return new StudentPanel();
                    case "LECTURERS":         return new LecturerPanel();
                    case "DEPARTMENTS":       return new DepartmentPanel();
                    case "COURSES":           return new CoursePanel();
                    case "ENROLLMENTS":       return new EnrollmentPanel();
                    case "REPORTS":           return new ReportPanel();
                    case "STATISTICS":        return new StatisticsPanel();
                    case "USERS":             return new UserPanel();
                    case "LECTURER_COURSES":  return new LecturerCoursePanel(currentUser.getLecturerId());
                    case "STUDENT_ENROLL":    return new StudentEnrollPanel(currentUser.getStudentId());
                    default:                  return new JPanel();
                }
            }

            @Override
            protected void done() {
                try {
                    JPanel panel = get();
                    switch (name) {
                        case "STUDENTS":          studentPanel          = (StudentPanel) panel; break;
                        case "LECTURERS":         lecturerPanel         = (LecturerPanel) panel; break;
                        case "DEPARTMENTS":       departmentPanel       = (DepartmentPanel) panel; break;
                        case "COURSES":           coursePanel           = (CoursePanel) panel; break;
                        case "ENROLLMENTS":       enrollmentPanel       = (EnrollmentPanel) panel; break;
                        case "REPORTS":           reportPanel           = (ReportPanel) panel; break;
                        case "STATISTICS":        statisticsPanel       = (StatisticsPanel) panel; break;
                        case "USERS":             userPanel             = (UserPanel) panel; break;
                        case "LECTURER_COURSES":  lecturerCoursePanel   = (LecturerCoursePanel) panel; break;
                        case "STUDENT_ENROLL":    studentEnrollPanel    = (StudentEnrollPanel) panel; break;
                    }
                    contentPanel.remove(spinnerPanel);
                    contentPanel.add(panel, name);
                    cardLayout.show(contentPanel, name);
                    if ("REPORTS".equals(name) && reportPanel != null) reportPanel.refreshData();
                    if ("STATISTICS".equals(name) && statisticsPanel != null) statisticsPanel.refreshData();
                } catch (Exception e) {
                    contentPanel.remove(spinnerPanel);
                    JLabel errorLabel = new JLabel("Failed to load " + name, SwingConstants.CENTER);
                    errorLabel.setForeground(Constants.DANGER_COLOR);
                    contentPanel.add(errorLabel, name + "_ERROR");
                    cardLayout.show(contentPanel, name + "_ERROR");
                }
            }
        }.execute();
    }
}
