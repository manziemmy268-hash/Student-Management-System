package com.university.dto;

import java.time.LocalDateTime;

public class ReportData {

    private final String title;
    private final String[] columnHeaders;
    private final Object[][] rows;
    private final LocalDateTime generatedAt;

    public ReportData(String title, String[] columnHeaders, Object[][] rows) {
        this.title = title;
        this.columnHeaders = columnHeaders;
        this.rows = rows;
        this.generatedAt = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public String[] getColumnHeaders() {
        return columnHeaders;
    }

    public Object[][] getRows() {
        return rows;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public int getRowCount() {
        return rows.length;
    }
}
