package com.university.view;

import com.university.dto.ReportData;
import com.university.service.ReportService;
import com.university.service.ReportServiceImpl;
import com.university.utility.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class ReportPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(ReportPanel.class);

    private final ReportService reportService = new ReportServiceImpl();
    private ReportData currentReportData;
    private JComboBox<String> cmbReportType;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblRecordCount;

    public ReportPanel() {
        initUI();
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
        JLabel lblTitle = new JLabel("Report Generation");
        lblTitle.setFont(Constants.TITLE_FONT);
        lblTitle.setForeground(Constants.TEXT_PRIMARY);
        titleSection.add(lblTitle);
        lblRecordCount = new JLabel("Select a report type and click Generate");
        lblRecordCount.setFont(Constants.STAT_LABEL);
        lblRecordCount.setForeground(Constants.TEXT_SECONDARY);
        titleSection.add(lblRecordCount);
        headerPanel.add(titleSection, BorderLayout.WEST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controlPanel.setBackground(Constants.LIGHT_COLOR);

        cmbReportType = new JComboBox<>(new String[]{
                "Students Report", "Lecturers Report", "Courses Report",
                "Departments Report", "Enrollments Report"
        });
        cmbReportType.setFont(Constants.FIELD_FONT);
        cmbReportType.setPreferredSize(new Dimension(200, 36));

        JButton btnGenerate = new JButton("Generate Report");
        Constants.styleButton(btnGenerate, Constants.PRIMARY_COLOR);
        JButton btnPrint = new JButton("Print Preview");
        Constants.styleButton(btnPrint, new Color(108, 117, 125));
        btnGenerate.addActionListener(e -> generateReport());
        btnPrint.addActionListener(e -> printReport());

        controlPanel.add(cmbReportType);
        controlPanel.add(btnGenerate);
        controlPanel.add(btnPrint);
        headerPanel.add(controlPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createLineBorder(Constants.BORDER_COLOR, 1));
        tableModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        Constants.styleTable(table);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Constants.BORDER_COLOR));

        JButton btnExport = new JButton("Export as Text");
        Constants.styleButton(btnExport, Constants.INFO_COLOR);
        btnExport.addActionListener(e -> exportReport());
        bottomPanel.add(btnExport);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshData() {
        generateReport();
    }

    private void generateReport() {
        String reportType = (String) cmbReportType.getSelectedItem();
        if (reportType == null) return;
        tableModel.setRowCount(0);

        new SwingWorker<ReportData, Void>() {
            @Override
            protected ReportData doInBackground() {
                switch (reportType) {
                    case "Students Report": return reportService.generateStudentsReport();
                    case "Lecturers Report": return reportService.generateLecturersReport();
                    case "Courses Report": return reportService.generateCoursesReport();
                    case "Departments Report": return reportService.generateDepartmentsReport();
                    case "Enrollments Report": return reportService.generateEnrollmentsReport();
                    default: return null;
                }
            }

            @Override
            protected void done() {
                try {
                    ReportData data = get();
                    if (data == null) return;
                    currentReportData = data;
                    tableModel.setColumnIdentifiers(data.getColumnHeaders());
                    tableModel.setRowCount(0);
                    for (Object[] row : data.getRows()) {
                        tableModel.addRow(row);
                    }
                    lblRecordCount.setText(data.getTitle() + " - " + data.getRowCount()
                            + " records | Generated: " + data.getGeneratedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                } catch (Exception ex) {
                    logger.error("Failed to generate report", ex);
                    JOptionPane.showMessageDialog(ReportPanel.this, "Failed to generate report.");
                }
            }
        }.execute();
    }

    private void printReport() {
        if (currentReportData == null) {
            JOptionPane.showMessageDialog(this, "Please generate a report first.");
            return;
        }

        String preview = reportService.buildPrintPreview(currentReportData);

        JTextArea textArea = new JTextArea(preview);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBackground(new Color(250, 250, 250));
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(750, 500));
        JOptionPane.showMessageDialog(this, scrollPane,
                "Report Preview - " + currentReportData.getTitle(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportReport() {
        if (currentReportData == null) {
            JOptionPane.showMessageDialog(this, "Please generate a report first.");
            return;
        }

        String csv = reportService.buildCsvExport(currentReportData);

        JTextArea ta = new JTextArea(csv);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta),
                "CSV Format - Copy and paste into a .csv file", JOptionPane.INFORMATION_MESSAGE);
    }
}
