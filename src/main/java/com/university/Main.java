package com.university;

import com.university.database.DBConnection;
import com.university.view.LoginFrame;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(DBConnection::shutdown));

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf theme");
        }

        SwingUtilities.invokeLater(() -> {
            if (DBConnection.testConnection()) {
                String dbType = DBConnection.isH2() ? "H2 (Demo Mode)" : "MySQL";
                System.out.println("Connected to " + dbType + " database.");
                new LoginFrame().setVisible(true);
            } else {
                String message;
                if (DBConnection.isH2()) {
                    message = "Cannot connect to H2 embedded database.\n\n" +
                            "This should not happen. Please report this issue.";
                } else {
                    message = "Cannot connect to MySQL database!\n\n" +
                            "Please ensure:\n" +
                            "1. MySQL is running\n" +
                            "2. Database 'university_db' exists (run sql/university_db.sql)\n" +
                            "3. Credentials are correct in db.properties or environment variables\n\n" +
                            "Or run in Demo Mode: mvn clean package -Pdemo && java -jar target/*.jar";
                }
                JOptionPane.showMessageDialog(null, message,
                        "Database Connection Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
