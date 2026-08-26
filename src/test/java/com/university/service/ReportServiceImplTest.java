package com.university.service;

import com.university.dao.CourseDao;
import com.university.dao.DepartmentDao;
import com.university.dao.EnrollmentDao;
import com.university.dao.LecturerDao;
import com.university.dao.StudentDao;
import com.university.dto.ReportData;
import com.university.model.Course;
import com.university.model.Department;
import com.university.model.Enrollment;
import com.university.model.Lecturer;
import com.university.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportServiceImpl Tests")
class ReportServiceImplTest {

    @Mock
    private StudentDao studentDao;

    @Mock
    private LecturerDao lecturerDao;

    @Mock
    private CourseDao courseDao;

    @Mock
    private DepartmentDao departmentDao;

    @Mock
    private EnrollmentDao enrollmentDao;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Department buildDepartment(int id, String name, String code) {
        Department d = new Department();
        d.setDepartmentId(id);
        d.setDepartmentName(name);
        d.setDepartmentCode(code);
        d.setLocation("Building A");
        d.setPhone("+1234567890");
        return d;
    }

    private Student buildStudent(int id, String firstName, String lastName, String email) {
        Student s = new Student();
        s.setStudentId(id);
        s.setFirstName(firstName);
        s.setLastName(lastName);
        s.setEmail(email);
        s.setPhone("+1234567890");
        s.setGpa(3.5);
        s.setStatus("Active");
        s.setDepartmentId(1);
        Department dept = buildDepartment(1, "Computer Science", "CS");
        s.setDepartment(dept);
        return s;
    }

    private Lecturer buildLecturer(int id, String firstName, String lastName, String email) {
        Lecturer l = new Lecturer();
        l.setLecturerId(id);
        l.setFirstName(firstName);
        l.setLastName(lastName);
        l.setEmail(email);
        l.setPhone("+1234567890");
        l.setRank("Professor");
        l.setDepartmentId(1);
        Department dept = buildDepartment(1, "Computer Science", "CS");
        l.setDepartment(dept);
        return l;
    }

    private Course buildCourse(int id, String courseName, String courseCode) {
        Course c = new Course();
        c.setCourseId(id);
        c.setCourseName(courseName);
        c.setCourseCode(courseCode);
        c.setCredits(3);
        c.setDepartmentId(1);
        Department dept = buildDepartment(1, "Computer Science", "CS");
        c.setDepartment(dept);
        Lecturer lec = new Lecturer();
        lec.setFirstName("Dr.");
        lec.setLastName("Smith");
        c.setLecturer(lec);
        c.setMaxEnrollment(60);
        return c;
    }

    private Enrollment buildEnrollment(int id, int studentId, int courseId) {
        Enrollment e = new Enrollment();
        e.setEnrollmentId(id);
        e.setStudentId(studentId);
        e.setCourseId(courseId);
        e.setSemester("Fall");
        e.setAcademicYear("2025");
        e.setGrade("A");
        e.setStatus("Enrolled");

        Student student = new Student();
        student.setStudentId(studentId);
        student.setFirstName("John");
        student.setLastName("Doe");
        e.setStudent(student);

        Course course = new Course();
        course.setCourseId(courseId);
        course.setCourseName("Data Structures");
        e.setCourse(course);

        return e;
    }

    @Test
    @DisplayName("generateStudentsReport() returns correct data with matching columns and rows")
    void generateStudentsReport_returnsCorrectData() {
        List<Student> students = Arrays.asList(
                buildStudent(1, "Alice", "Smith", "alice@uni.edu"),
                buildStudent(2, "Bob", "Jones", "bob@uni.edu")
        );
        when(studentDao.getAllStudents()).thenReturn(students);

        ReportData report = reportService.generateStudentsReport();

        assertNotNull(report);
        assertEquals("Student Report", report.getTitle());
        assertEquals(8, report.getColumnHeaders().length);
        assertEquals(2, report.getRowCount());
        assertEquals("ID", report.getColumnHeaders()[0]);
        assertEquals("First Name", report.getColumnHeaders()[1]);
    }

    @Test
    @DisplayName("generateStudentsReport() handles empty student list")
    void generateStudentsReport_emptyList_returnsEmptyRows() {
        when(studentDao.getAllStudents()).thenReturn(Collections.emptyList());

        ReportData report = reportService.generateStudentsReport();

        assertNotNull(report);
        assertEquals(0, report.getRowCount());
        assertEquals("Student Report", report.getTitle());
    }

    @Test
    @DisplayName("generateLecturersReport() returns correct data with matching columns and rows")
    void generateLecturersReport_returnsCorrectData() {
        List<Lecturer> lecturers = Arrays.asList(
                buildLecturer(1, "Alice", "Smith", "alice@uni.edu"),
                buildLecturer(2, "Bob", "Jones", "bob@uni.edu")
        );
        when(lecturerDao.getAllLecturers()).thenReturn(lecturers);

        ReportData report = reportService.generateLecturersReport();

        assertNotNull(report);
        assertEquals("Lecturer Report", report.getTitle());
        assertEquals(7, report.getColumnHeaders().length);
        assertEquals(2, report.getRowCount());
        assertEquals("Rank", report.getColumnHeaders()[5]);
    }

    @Test
    @DisplayName("generateCoursesReport() returns correct data with matching columns and rows")
    void generateCoursesReport_returnsCorrectData() {
        List<Course> courses = Arrays.asList(
                buildCourse(1, "Data Structures", "CS201"),
                buildCourse(2, "Algorithms", "CS301"),
                buildCourse(3, "Linear Algebra", "MATH101")
        );
        when(courseDao.getAllCourses()).thenReturn(courses);

        ReportData report = reportService.generateCoursesReport();

        assertNotNull(report);
        assertEquals("Course Report", report.getTitle());
        assertEquals(7, report.getColumnHeaders().length);
        assertEquals(3, report.getRowCount());
        assertEquals("Credits", report.getColumnHeaders()[3]);
    }

    @Test
    @DisplayName("generateDepartmentsReport() returns correct data with matching columns and rows")
    void generateDepartmentsReport_returnsCorrectData() {
        List<Department> departments = Arrays.asList(
                buildDepartment(1, "Computer Science", "CS"),
                buildDepartment(2, "Mathematics", "MATH")
        );
        when(departmentDao.getAllDepartments()).thenReturn(departments);

        ReportData report = reportService.generateDepartmentsReport();

        assertNotNull(report);
        assertEquals("Department Report", report.getTitle());
        assertEquals(5, report.getColumnHeaders().length);
        assertEquals(2, report.getRowCount());
        assertEquals("Location", report.getColumnHeaders()[3]);
    }

    @Test
    @DisplayName("generateEnrollmentsReport() returns correct data with matching columns and rows")
    void generateEnrollmentsReport_returnsCorrectData() {
        List<Enrollment> enrollments = Arrays.asList(
                buildEnrollment(1, 1, 1),
                buildEnrollment(2, 2, 2)
        );
        when(enrollmentDao.getAllEnrollments()).thenReturn(enrollments);

        ReportData report = reportService.generateEnrollmentsReport();

        assertNotNull(report);
        assertEquals("Enrollment Report", report.getTitle());
        assertEquals(7, report.getColumnHeaders().length);
        assertEquals(2, report.getRowCount());
        assertEquals("Semester", report.getColumnHeaders()[3]);
    }

    @Test
    @DisplayName("buildPrintPreview() contains the report title")
    void buildPrintPreview_containsTitle() {
        String[] columns = {"ID", "Name"};
        Object[][] rows = {{1, "Alice"}};
        ReportData data = new ReportData("Student Report", columns, rows);

        String preview = reportService.buildPrintPreview(data);

        assertNotNull(preview);
        assertTrue(preview.contains("Student Report"));
        assertTrue(preview.contains("UNIVERSITY MANAGEMENT SYSTEM"));
        assertTrue(preview.contains("Total Records: 1"));
    }

    @Test
    @DisplayName("buildCsvExport() contains correct headers")
    void buildCsvExport_containsHeaders() {
        String[] columns = {"ID", "First Name", "Last Name"};
        Object[][] rows = {{1, "Alice", "Smith"}};
        ReportData data = new ReportData("Student Report", columns, rows);

        String csv = reportService.buildCsvExport(data);

        assertNotNull(csv);
        assertTrue(csv.contains("ID,First Name,Last Name"));
        assertTrue(csv.contains("1,Alice,Smith"));
    }
}
