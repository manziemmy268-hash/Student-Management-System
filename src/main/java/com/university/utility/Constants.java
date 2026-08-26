package com.university.utility;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;

public class Constants {

    public static final String APP_NAME = "University Management System";

    public static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    public static final Color PRIMARY_DARK = new Color(21, 101, 192);
    public static final Color PRIMARY_LIGHT = new Color(66, 165, 245);
    public static final Color SECONDARY_COLOR = new Color(71, 143, 255);
    public static final Color DARK_COLOR = new Color(30, 36, 50);
    public static final Color DARK_SIDEBAR = new Color(33, 40, 56);
    public static final Color LIGHT_COLOR = new Color(245, 247, 250);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color SUCCESS_COLOR = new Color(46, 160, 67);
    public static final Color DANGER_COLOR = new Color(211, 47, 47);
    public static final Color WARNING_COLOR = new Color(245, 166, 35);
    public static final Color INFO_COLOR = new Color(41, 182, 246);
    public static final Color TABLE_HEADER_BG = new Color(25, 118, 210);
    public static final Color TABLE_HEADER_FG = Color.WHITE;
    public static final Color TABLE_ALT_ROW = new Color(232, 244, 253);
    public static final Color TABLE_HOVER = new Color(207, 228, 250);
    public static final Color TABLE_SELECTED = new Color(25, 118, 210);
    public static final Color BORDER_COLOR = new Color(200, 212, 226);
    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    public static final Color PLACEHOLDER_COLOR = new Color(170, 170, 170);
    public static final Color SIDEBAR_ACTIVE = new Color(25, 118, 210);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font LABEL_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font MENU_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font STAT_NUMBER = new Font("Segoe UI", Font.BOLD, 36);
    public static final Font STAT_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font LOGIN_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font SIDEBAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SIDEBAR_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    public static final String[] ROLE_OPTIONS = {"Admin", "Staff", "Lecturer"};

    public static void styleButton(JButton btn, Color bgColor) {
        btn.setFont(BUTTON_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setOpaque(true);
    }

    public static void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(35);
        table.setSelectionBackground(TABLE_SELECTED);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(Color.WHITE);

        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_FG);
        header.setPreferredSize(new java.awt.Dimension(0, 40));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            }
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, r, c);
                setBackground(TABLE_HEADER_BG);
                setForeground(TABLE_HEADER_FG);
                setFont(TABLE_HEADER_FONT);
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
    }

    public static void styleField(JComponent field) {
        field.setFont(FIELD_FONT);
        field.setPreferredSize(new java.awt.Dimension(250, 36));
        field.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(BORDER_COLOR, 1),
                javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }
}
