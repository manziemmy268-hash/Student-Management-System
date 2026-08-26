package com.university.service;

import com.university.dao.CourseDao;
import com.university.dao.EnrollmentDao;
import com.university.dao.StudentDao;
import com.university.exception.ValidationException;
import com.university.model.Course;
import com.university.model.Enrollment;
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
@DisplayName("EnrollmentServiceImpl Tests")
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentDao enrollmentDao;

    @Mock
    private StudentDao studentDao;

    @Mock
    private CourseDao courseDao;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private Enrollment buildEnrollment(int id, int studentId, int courseId, String semester, String academicYear) {
        Enrollment e = new Enrollment();
        e.setEnrollmentId(id);
        e.setStudentId(studentId);
        e.setCourseId(courseId);
        e.setSemester(semester);
        e.setAcademicYear(academicYear);
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

    private Student buildStudent(int id, String firstName, String lastName, String email) {
        Student s = new Student();
        s.setStudentId(id);
        s.setFirstName(firstName);
        s.setLastName(lastName);
        s.setEmail(email);
        return s;
    }

    private Course buildCourse(int id, String courseName, String courseCode) {
        Course c = new Course();
        c.setCourseId(id);
        c.setCourseName(courseName);
        c.setCourseCode(courseCode);
        return c;
    }

    @Test
    @DisplayName("getAllEnrollments() delegates to enrollmentDao")
    void getAllEnrollments_delegatesToDao() {
        List<Enrollment> mockList = Arrays.asList(
                buildEnrollment(1, 1, 1, "Fall", "2025"),
                buildEnrollment(2, 2, 2, "Spring", "2026")
        );
        when(enrollmentDao.getAllEnrollments()).thenReturn(mockList);

        List<Enrollment> result = enrollmentService.getAllEnrollments();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(enrollmentDao, times(1)).getAllEnrollments();
    }

    @Test
    @DisplayName("addEnrollment() returns true for a valid enrollment")
    void addEnrollment_validEnrollment_returnsTrue() {
        Enrollment enrollment = buildEnrollment(0, 1, 1, "Fall", "2025");
        when(enrollmentDao.getAllEnrollments()).thenReturn(Collections.emptyList());
        when(enrollmentDao.addEnrollment(enrollment)).thenReturn(true);

        boolean result = enrollmentService.addEnrollment(enrollment);

        assertTrue(result);
        verify(enrollmentDao, times(1)).addEnrollment(enrollment);
    }

    @Test
    @DisplayName("addEnrollment() throws ValidationException for duplicate enrollment")
    void addEnrollment_duplicateEnrollment_throwsValidationException() {
        Enrollment existing = buildEnrollment(1, 1, 1, "Fall", "2025");
        when(enrollmentDao.getAllEnrollments()).thenReturn(Arrays.asList(existing));

        Enrollment duplicate = buildEnrollment(0, 1, 1, "Fall", "2025");

        assertThrows(ValidationException.class,
                () -> enrollmentService.addEnrollment(duplicate));
    }

    @Test
    @DisplayName("searchEnrollments() delegates to enrollmentDao")
    void searchEnrollments_delegatesToDao() {
        List<Enrollment> mockResults = List.of(
                buildEnrollment(1, 1, 1, "Fall", "2025")
        );
        when(enrollmentDao.searchEnrollments("John")).thenReturn(mockResults);

        List<Enrollment> result = enrollmentService.searchEnrollments("John");

        assertEquals(1, result.size());
        verify(enrollmentDao, times(1)).searchEnrollments("John");
    }

    @Test
    @DisplayName("deleteEnrollment() delegates to enrollmentDao")
    void deleteEnrollment_delegatesToDao() {
        when(enrollmentDao.deleteEnrollment(1)).thenReturn(true);

        boolean result = enrollmentService.deleteEnrollment(1);

        assertTrue(result);
        verify(enrollmentDao, times(1)).deleteEnrollment(1);
    }

    @Test
    @DisplayName("getEnrollmentCount() delegates to enrollmentDao")
    void getEnrollmentCount_delegatesToDao() {
        when(enrollmentDao.getEnrollmentCount()).thenReturn(42);

        int count = enrollmentService.getEnrollmentCount();

        assertEquals(42, count);
        verify(enrollmentDao, times(1)).getEnrollmentCount();
    }

    @Test
    @DisplayName("getAllStudents() delegates to studentDao")
    void getAllStudents_delegatesToStudentDao() {
        List<Student> mockStudents = Arrays.asList(
                buildStudent(1, "Alice", "Smith", "alice@uni.edu")
        );
        when(studentDao.getAllStudents()).thenReturn(mockStudents);

        List<Student> result = enrollmentService.getAllStudents();

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
        verify(studentDao, times(1)).getAllStudents();
    }

    @Test
    @DisplayName("getAllCourses() delegates to courseDao")
    void getAllCourses_delegatesToCourseDao() {
        List<Course> mockCourses = Arrays.asList(
                buildCourse(1, "Data Structures", "CS201")
        );
        when(courseDao.getAllCourses()).thenReturn(mockCourses);

        List<Course> result = enrollmentService.getAllCourses();

        assertEquals(1, result.size());
        assertEquals("Data Structures", result.get(0).getCourseName());
        verify(courseDao, times(1)).getAllCourses();
    }
}
