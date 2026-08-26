package com.university.view;

import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.User;
import com.university.service.UserService;
import com.university.service.UserServiceImpl;
import com.university.utility.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(UserPanel.class);

    private final UserService userService = new UserServiceImpl();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblRecordCount;

    public UserPanel() {
        initUI();
        loadUsers();
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

        JLabel lblTitle = new JLabel("User Management");
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
        txtSearch.setToolTipText("Search by name or username...");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { searchUsers(); }
        });

        JButton btnSearch = new JButton("Search");
        Constants.styleButton(btnSearch, Constants.PRIMARY_COLOR);

        JButton btnAdd = new JButton("+ Add User");
        Constants.styleButton(btnAdd, Constants.SUCCESS_COLOR);

        JButton btnRefresh = new JButton("Refresh");
        Constants.styleButton(btnRefresh, new Color(108, 117, 125));

        btnSearch.addActionListener(e -> searchUsers());
        btnAdd.addActionListener(e -> openAddDialog());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadUsers(); });

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

        String[] columns = {"ID", "Username", "Full Name", "Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        Constants.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Constants.BORDER_COLOR));

        JButton btnView = new JButton("View Details");
        Constants.styleButton(btnView, Constants.INFO_COLOR);

        JButton btnEdit = new JButton("Edit");
        Constants.styleButton(btnEdit, Constants.PRIMARY_COLOR);

        JButton btnChangePassword = new JButton("Change Password");
        Constants.styleButton(btnChangePassword, Constants.WARNING_COLOR);

        JButton btnDelete = new JButton("Delete");
        Constants.styleButton(btnDelete, Constants.DANGER_COLOR);

        btnView.addActionListener(e -> viewSelectedUser());
        btnEdit.addActionListener(e -> editSelectedUser());
        btnChangePassword.addActionListener(e -> changePasswordForSelectedUser());
        btnDelete.addActionListener(e -> deleteSelectedUser());

        bottomPanel.add(btnView);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnChangePassword);
        bottomPanel.add(btnDelete);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadUsers() {
        new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() {
                return userService.getAllUsers();
            }

            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    tableModel.setRowCount(0);
                    for (User u : users) {
                        tableModel.addRow(new Object[]{
                                u.getUserId(), u.getUsername(), u.getFullName(), u.getRole()
                        });
                    }
                    lblRecordCount.setText(users.size() + " records found");
                } catch (Exception ex) {
                    logger.error("Error loading users", ex);
                    JOptionPane.showMessageDialog(UserPanel.this, "An error occurred while loading users.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void searchUsers() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) { loadUsers(); return; }

        new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() {
                String lower = keyword.toLowerCase();
                List<User> allUsers = userService.getAllUsers();
                java.util.ArrayList<User> filtered = new java.util.ArrayList<>();
                for (User u : allUsers) {
                    if ((u.getUsername() != null && u.getUsername().toLowerCase().contains(lower))
                            || (u.getFullName() != null && u.getFullName().toLowerCase().contains(lower))) {
                        filtered.add(u);
                    }
                }
                return filtered;
            }

            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    tableModel.setRowCount(0);
                    for (User u : users) {
                        tableModel.addRow(new Object[]{
                                u.getUserId(), u.getUsername(), u.getFullName(), u.getRole()
                        });
                    }
                    lblRecordCount.setText(users.size() + " records found");
                } catch (Exception ex) {
                    logger.error("Error searching users", ex);
                    JOptionPane.showMessageDialog(UserPanel.this, "An error occurred while searching.", "Error", JOptionPane.ERROR_MESSAGE);
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
        JDialog dialog = createDialog("Add New User", 480, 380);
        JPanel form = createFormPanel();

        JTextField txtUsername = new JTextField(20);
        JTextField txtFullName = new JTextField(20);
        JPasswordField txtPassword = new JPasswordField(20);
        JComboBox<String> cmbRole = new JComboBox<>(Constants.ROLE_OPTIONS);

        int row = 0;
        addFormRow(form, row++, "Username *:", txtUsername);
        addFormRow(form, row++, "Full Name *:", txtFullName);
        addFormRow(form, row++, "Password *:", txtPassword);
        addFormRow(form, row++, "Role *:", cmbRole);

        JButton btnSave = new JButton("Save");
        Constants.styleButton(btnSave, Constants.SUCCESS_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));

        btnSave.addActionListener(e -> {
            try {
                String username = txtUsername.getText().trim();
                String fullName = txtFullName.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();
                String role = (String) cmbRole.getSelectedItem();

                if (userService.isUsernameDuplicate(username, -1)) {
                    JOptionPane.showMessageDialog(dialog, "Username already exists.", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                User user = new User();
                user.setUsername(username);
                user.setFullName(fullName);
                user.setRole(role);

                if (userService.addUser(user, password)) {
                    JOptionPane.showMessageDialog(dialog, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DuplicateEntityException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Error adding user", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        addDialogButtons(dialog, form, btnSave, btnCancel);
    }

    private void editSelectedUser() {
        int userId = getSelectedId();
        if (userId == -1) { JOptionPane.showMessageDialog(this, "Please select a user to edit."); return; }
        User user = userService.getUserById(userId);
        if (user == null) return;

        JDialog dialog = createDialog("Edit User - " + user.getFullName(), 480, 320);
        JPanel form = createFormPanel();

        JTextField txtUsername = new JTextField(user.getUsername(), 20);
        JTextField txtFullName = new JTextField(user.getFullName(), 20);
        JComboBox<String> cmbRole = new JComboBox<>(Constants.ROLE_OPTIONS);
        cmbRole.setSelectedItem(user.getRole());

        int row = 0;
        addFormRow(form, row++, "Username *:", txtUsername);
        addFormRow(form, row++, "Full Name *:", txtFullName);
        addFormRow(form, row++, "Role *:", cmbRole);

        JButton btnUpdate = new JButton("Update");
        Constants.styleButton(btnUpdate, Constants.PRIMARY_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));

        btnUpdate.addActionListener(e -> {
            try {
                String username = txtUsername.getText().trim();
                String fullName = txtFullName.getText().trim();
                String role = (String) cmbRole.getSelectedItem();

                if (userService.isUsernameDuplicate(username, user.getUserId())) {
                    JOptionPane.showMessageDialog(dialog, "Username already exists.", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                user.setUsername(username);
                user.setFullName(fullName);
                user.setRole(role);

                if (userService.updateUser(user)) {
                    JOptionPane.showMessageDialog(dialog, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DuplicateEntityException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                logger.error("Error updating user", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        addDialogButtons(dialog, form, btnUpdate, btnCancel);
    }

    private void changePasswordForSelectedUser() {
        int userId = getSelectedId();
        if (userId == -1) { JOptionPane.showMessageDialog(this, "Please select a user to change password."); return; }
        User user = userService.getUserById(userId);
        if (user == null) return;

        JDialog dialog = createDialog("Change Password - " + user.getFullName(), 460, 280);
        JPanel form = createFormPanel();

        JPasswordField txtNewPassword = new JPasswordField(20);
        JPasswordField txtConfirmPassword = new JPasswordField(20);

        int row = 0;
        addFormRow(form, row++, "New Password *:", txtNewPassword);
        addFormRow(form, row++, "Confirm Password *:", txtConfirmPassword);

        JButton btnChange = new JButton("Change Password");
        Constants.styleButton(btnChange, Constants.WARNING_COLOR);
        JButton btnCancel = new JButton("Cancel");
        Constants.styleButton(btnCancel, new Color(108, 117, 125));

        btnChange.addActionListener(e -> {
            try {
                String newPassword = new String(txtNewPassword.getPassword()).trim();
                String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();

                if (newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Password cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Passwords do not match.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (userService.updatePassword(userId, newPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to change password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                logger.error("Error changing password", ex);
                JOptionPane.showMessageDialog(dialog, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        addDialogButtons(dialog, form, btnChange, btnCancel);
    }

    private void deleteSelectedUser() {
        int userId = getSelectedId();
        if (userId == -1) { JOptionPane.showMessageDialog(this, "Please select a user to delete."); return; }
        int row = table.getSelectedRow();
        String name = (String) tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user:\n" + name + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userService.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewSelectedUser() {
        int userId = getSelectedId();
        if (userId == -1) { JOptionPane.showMessageDialog(this, "Please select a user to view."); return; }
        User u = userService.getUserById(userId);
        if (u == null) return;

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        addInfoRow(infoPanel, "User ID:", String.valueOf(u.getUserId()));
        addInfoRow(infoPanel, "Username:", u.getUsername());
        addInfoRow(infoPanel, "Full Name:", u.getFullName());
        addInfoRow(infoPanel, "Role:", u.getRole());
        addInfoRow(infoPanel, "Is Admin:", u.isAdmin() ? "Yes" : "No");
        addInfoRow(infoPanel, "Is Staff:", u.isStaff() ? "Yes" : "No");

        JOptionPane.showMessageDialog(this, infoPanel, "User Details - " + u.getFullName(), JOptionPane.INFORMATION_MESSAGE);
    }

    private JDialog createDialog(String title, int w, int h) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(w, h);
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
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
