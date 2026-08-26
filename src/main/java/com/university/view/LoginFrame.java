package com.university.view;

import com.university.controller.LoginController;
import com.university.utility.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;
    private LoginController loginController;

    public LoginFrame() {
        loginController = new LoginController();
        initUI();
    }

    private void initUI() {
        setTitle(Constants.APP_NAME + " - Login");
        setSize(850, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(Constants.CARD_BG);

        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Constants.PRIMARY_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setPreferredSize(new Dimension(420, 0));
        leftPanel.setLayout(new GridBagLayout());

        JPanel leftContent = new JPanel();
        leftContent.setOpaque(false);
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));

        JLabel lblIcon = new JLabel("\u2302");
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 72));
        lblIcon.setForeground(Color.WHITE);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAppName = new JLabel(Constants.APP_NAME);
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblAppName.setForeground(Color.WHITE);
        lblAppName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTagline = new JLabel("Student & Lecturer Management");
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTagline.setForeground(new Color(200, 220, 255));
        lblTagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftContent.add(lblIcon);
        leftContent.add(Box.createVerticalStrut(15));
        leftContent.add(lblAppName);
        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(lblTagline);

        leftPanel.add(leftContent);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Constants.CARD_BG);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JPanel formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setOpaque(false);

        JLabel lblLoginTitle = new JLabel("Sign In");
        lblLoginTitle.setFont(Constants.LOGIN_TITLE);
        lblLoginTitle.setForeground(Constants.TEXT_PRIMARY);
        lblLoginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLoginSubtitle = new JLabel("Enter your credentials to access the system");
        lblLoginSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLoginSubtitle.setForeground(Constants.TEXT_SECONDARY);
        lblLoginSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formWrapper.add(lblLoginTitle);
        formWrapper.add(Box.createVerticalStrut(5));
        formWrapper.add(lblLoginSubtitle);
        formWrapper.add(Box.createVerticalStrut(30));

        JLabel lblUser = new JLabel("USERNAME");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblUser.setForeground(Constants.TEXT_SECONDARY);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        formWrapper.add(lblUser);
        formWrapper.add(Box.createVerticalStrut(6));

        txtUsername = new JTextField(20);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setFont(Constants.FIELD_FONT);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        formWrapper.add(txtUsername);
        formWrapper.add(Box.createVerticalStrut(20));

        JLabel lblPass = new JLabel("PASSWORD");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPass.setForeground(Constants.TEXT_SECONDARY);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        formWrapper.add(lblPass);
        formWrapper.add(Box.createVerticalStrut(6));

        txtPassword = new JPasswordField(20);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setFont(Constants.FIELD_FONT);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        formWrapper.add(txtPassword);
        formWrapper.add(Box.createVerticalStrut(10));

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(Constants.DANGER_COLOR);
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        formWrapper.add(lblStatus);
        formWrapper.add(Box.createVerticalStrut(15));

        btnLogin = new JButton("Sign In");
        Constants.styleButton(btnLogin, Constants.PRIMARY_COLOR);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> performLogin());
        formWrapper.add(btnLogin);

        rightPanel.add(formWrapper);

        rootPanel.add(leftPanel, BorderLayout.WEST);
        rootPanel.add(rightPanel, BorderLayout.CENTER);

        add(rootPanel);

        txtPassword.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        char[] passwordChars = txtPassword.getPassword();
        String password = new String(passwordChars).trim();

        if (username.isEmpty() || password.isEmpty()) {
            Arrays.fill(passwordChars, '\0');
            lblStatus.setText("Please enter both username and password");
            return;
        }

        lblStatus.setText("Authenticating...");
        lblStatus.setForeground(Constants.INFO_COLOR);
        btnLogin.setEnabled(false);
        btnLogin.setText("Signing in...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return loginController.authenticate(username, password);
            }

            @Override
            protected void done() {
                Arrays.fill(passwordChars, '\0');
                try {
                    if (get()) {
                        lblStatus.setForeground(Constants.SUCCESS_COLOR);
                        lblStatus.setText("Login successful!");
                        dispose();
                        new DashboardFrame(loginController.getCurrentUser()).setVisible(true);
                    } else {
                        lblStatus.setForeground(Constants.DANGER_COLOR);
                        lblStatus.setText("Invalid username or password");
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }
                } catch (Exception ex) {
                    lblStatus.setForeground(Constants.DANGER_COLOR);
                    lblStatus.setText("Connection error. Please try again.");
                }
                btnLogin.setEnabled(true);
                btnLogin.setText("Sign In");
            }
        };
        worker.execute();
    }
}
