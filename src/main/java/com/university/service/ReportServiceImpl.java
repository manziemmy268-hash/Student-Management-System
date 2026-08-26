package com.university.service;

import com.university.dao.*;
import com.university.dto.ReportData;
import com.university.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportServiceImpl implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final StudentDao studentDao;
    private final LecturerDao lecturerDao;
    private final CourseDao courseDao;
    private final DepartmentDao departmentDao;
    private final EnrollmentDao enrollmentDao;

    public ReportServiceImpl() {
        this.studentDao = new StudentDao();
        this.lecturerDao = new LecturerDao();
        this.courseDao = new CourseDao();
        this.departmentDao = new DepartmentDao();
        this.enrollmentDao = new EnrollmentDao();
    }

    public ReportServiceImpl(StudentDao studentDao, LecturerDao lecturerDao, CourseDao courseDao,
                             DepartmentDao departmentDao, EnrollmentDao enrollmentDao) {
        this.studentDao = studentDao;
        this.lecturerDao = lecturerDao;
        this.courseDao = courseDao;
        this.departmentDao = departmentDao;
        this.enrollmentDao = enrollmentDao;
    }

    @Override
    public ReportData generateStudentsReport() {
        logger.info("Generating students report...");
        String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "GPA", "Status", "Department"};
        List<Student> students = studentDao.getAllStudents();
        Object[][] rows = new Object[students.size()][];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            rows[i] = new Object[]{
                    s.getStudentId(),
                    s.getFirstName() != null ? s.getFirstName() : "-",
                    s.getLastName() != null ? s.getLastName() : "-",
                    s.getEmail() != null ? s.getEmail() : "-",
                    s.getPhone() != null ? s.getPhone() : "-",
                    String.format("%.2f", s.getGpa()),
                    s.getStatus() != null ? s.getStatus() : "-",
                    s.getDepartmentName() != null ? s.getDepartmentName() : "-"
            };
        }
        logger.info("Students report generated with {} records.", students.size());
        return new ReportData("Student Report", columns, rows);
    }

    @Override
    public ReportData generateLecturersReport() {
        logger.info("Generating lecturers report...");
        String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "Rank", "Department"};
        List<Lecturer> lecturers = lecturerDao.getAllLecturers();
        Object[][] rows = new Object[lecturers.size()][];
        for (int i = 0; i < lecturers.size(); i++) {
            Lecturer l = lecturers.get(i);
            rows[i] = new Object[]{
                    l.getLecturerId(),
                    l.getFirstName() != null ? l.getFirstName() : "-",
                    l.getLastName() != null ? l.getLastName() : "-",
                    l.getEmail() != null ? l.getEmail() : "-",
                    l.getPhone() != null ? l.getPhone() : "-",
                    l.getRank() != null ? l.getRank() : "-",
                    l.getDepartmentName() != null ? l.getDepartmentName() : "-"
            };
        }
        logger.info("Lecturers report generated with {} records.", lecturers.size());
        return new ReportData("Lecturer Report", columns, rows);
    }

    @Override
    public ReportData generateCoursesReport() {
        logger.info("Generating courses report...");
        String[] columns = {"ID", "Code", "Name", "Credits", "Department", "Lecturer", "Max Enrollment"};
        List<Course> courses = courseDao.getAllCourses();
        Object[][] rows = new Object[courses.size()][];
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            rows[i] = new Object[]{
                    c.getCourseId(),
                    c.getCourseCode() != null ? c.getCourseCode() : "-",
                    c.getCourseName() != null ? c.getCourseName() : "-",
                    c.getCredits(),
                    c.getDepartmentName() != null ? c.getDepartmentName() : "-",
                    c.getLecturerName() != null ? c.getLecturerName() : "-",
                    c.getMaxEnrollment()
            };
        }
        logger.info("Courses report generated with {} records.", courses.size());
        return new ReportData("Course Report", columns, rows);
    }

    @Override
    public ReportData generateDepartmentsReport() {
        logger.info("Generating departments report...");
        String[] columns = {"ID", "Name", "Code", "Location", "Phone"};
        List<Department> departments = departmentDao.getAllDepartments();
        Object[][] rows = new Object[departments.size()][];
        for (int i = 0; i < departments.size(); i++) {
            Department d = departments.get(i);
            rows[i] = new Object[]{
                    d.getDepartmentId(),
                    d.getDepartmentName() != null ? d.getDepartmentName() : "-",
                    d.getDepartmentCode() != null ? d.getDepartmentCode() : "-",
                    d.getLocation() != null ? d.getLocation() : "-",
                    d.getPhone() != null ? d.getPhone() : "-"
            };
        }
        logger.info("Departments report generated with {} records.", departments.size());
        return new ReportData("Department Report", columns, rows);
    }

    @Override
    public ReportData generateEnrollmentsReport() {
        logger.info("Generating enrollments report...");
        String[] columns = {"ID", "Student", "Course", "Semester", "Year", "Grade", "Status"};
        List<Enrollment> enrollments = enrollmentDao.getAllEnrollments();
        Object[][] rows = new Object[enrollments.size()][];
        for (int i = 0; i < enrollments.size(); i++) {
            Enrollment e = enrollments.get(i);
            rows[i] = new Object[]{
                    e.getEnrollmentId(),
                    e.getStudentName() != null ? e.getStudentName() : "-",
                    e.getCourseName() != null ? e.getCourseName() : "-",
                    e.getSemester() != null ? e.getSemester() : "-",
                    e.getAcademicYear() != null ? e.getAcademicYear() : "-",
                    e.getGrade() != null ? e.getGrade() : "-",
                    e.getStatus() != null ? e.getStatus() : "-"
            };
        }
        logger.info("Enrollments report generated with {} records.", enrollments.size());
        return new ReportData("Enrollment Report", columns, rows);
    }

    @Override
    public String buildPrintPreview(ReportData data) {
        StringBuilder sb = new StringBuilder();
        String divider = "=".repeat(90);
        String generatedAt = data.getGeneratedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        sb.append(divider).append("\n");
        sb.append("  UNIVERSITY MANAGEMENT SYSTEM\n");
        sb.append("  Report: ").append(data.getTitle() != null ? data.getTitle() : "N/A").append("\n");
        sb.append("  Generated: ").append(generatedAt).append("\n");
        sb.append(divider).append("\n\n");

        String[] columns = data.getColumnHeaders();
        for (int j = 0; j < columns.length; j++) {
            sb.append(String.format("%-18s", columns[j]));
        }
        sb.append("\n").append("-".repeat(90)).append("\n");

        Object[][] rows = data.getRows();
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length; j++) {
                Object val = rows[i][j];
                sb.append(String.format("%-18s", val != null ? val : ""));
            }
            sb.append("\n");
        }

        sb.append("\n").append(divider).append("\n");
        sb.append("  Total Records: ").append(data.getRowCount()).append("\n");
        sb.append(divider);

        logger.info("Print preview built for '{}' with {} records.", data.getTitle(), data.getRowCount());
        return sb.toString();
    }

    @Override
    public String buildCsvExport(ReportData data) {
        StringBuilder sb = new StringBuilder();
        String[] columns = data.getColumnHeaders();

        for (int j = 0; j < columns.length; j++) {
            sb.append(csvEscape(columns[j]));
            if (j < columns.length - 1) sb.append(",");
        }
        sb.append("\n");

        Object[][] rows = data.getRows();
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length; j++) {
                Object val = rows[i][j];
                sb.append(csvEscape(val != null ? String.valueOf(val) : ""));
                if (j < rows[i].length - 1) sb.append(",");
            }
            sb.append("\n");
        }

        logger.info("CSV export built for '{}' with {} records.", data.getTitle(), data.getRowCount());
        return sb.toString();
    }

    private String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
