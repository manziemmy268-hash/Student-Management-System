package com.university.service;

import com.university.dto.ReportData;

public interface ReportService {

    ReportData generateStudentsReport();

    ReportData generateLecturersReport();

    ReportData generateCoursesReport();

    ReportData generateDepartmentsReport();

    ReportData generateEnrollmentsReport();

    String buildPrintPreview(ReportData data);

    String buildCsvExport(ReportData data);
}
