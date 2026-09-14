package com.university.integration;

import com.university.dao.CourseDao;
import com.university.dao.EnrollmentDao;
import com.university.dao.UserDao;
import com.university.exception.DataAccessException;
import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.Course;
import com.university.model.Enrollment;
import com.university.model.Student;
import com.university.model.User;
import com.university.service.AuthService;
import com.university.service.AuthServiceImpl;
import com.university.service.CourseService;
import com.university.service.CourseServiceImpl;
import com.university.service.EnrollmentService;
import com.university.service.EnrollmentServiceImpl;
import com.university.service.StudentService;
import com.university.service.StudentServiceImpl;
import com.university.service.UserService;
import com.university.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("End-to-end integration tests against an embedded H2 database")
class DatabaseIntegrationTest {

    private static final String H2_URL = "jdbc:h2:mem:university_it;MODE=MySQL;DB_CLOSE_DELAY=-1";

    static {
        System.setProperty("db.url", H2_URL);
        System.setProperty("db.username", "sa");
        System.setProperty("db.password", "");
    }

    private static UserService userService;
    private static AuthService authService;
    private static StudentService studentService;
    private static CourseService courseService;
    private static EnrollmentService enrollmentService;
    private static UserDao userDao;
    private static CourseDao courseDao;
    private static EnrollmentDao enrollmentDao;

    @BeforeAll
    static void setUp() {
        userService = new UserServiceImpl();
        authService = new AuthServiceImpl();
        studentService = new StudentServiceImpl();
        courseService = new CourseServiceImpl();
        enrollmentService = new EnrollmentServiceImpl();

        userDao = new UserDao();
        courseDao = new CourseDao();
        enrollmentDao = new EnrollmentDao();
    }

    @Test
    @DisplayName("Register a user, then log in with the correct and wrong passwords")
    void registerUser_thenAuthenticate() {
        User user = new User();
        user.setUsername("itstaff");
        user.setFullName("IT Staff");
        user.setRole("Staff");

        assertTrue(userService.addUser(user, "secret123"), "Registration should succeed");

        User loggedIn = authService.authenticate("itstaff", "secret123");
        assertNotNull(loggedIn);
        assertEquals("itstaff", loggedIn.getUsername());
        assertEquals("Staff", loggedIn.getRole());

        assertNull(authService.authenticate("itstaff", "wrong-password"),
                "Wrong password should be rejected");
    }

    @Test
    @DisplayName("Service layer rejects duplicate usernames, course codes, emails, and enrollments")
    void serviceLayer_rejectsDuplicates() {
        User duplicateAdmin = new User();
        duplicateAdmin.setUsername("admin");
        duplicateAdmin.setFullName("Another Admin");
        duplicateAdmin.setRole("Admin");
        assertThrows(DuplicateEntityException.class,
                () -> userService.addUser(duplicateAdmin, "secret123"));

        Course duplicateCourse = new Course();
        duplicateCourse.setCourseName("Dup Programming");
        duplicateCourse.setCourseCode("CS101");
        duplicateCourse.setCredits(3);
        duplicateCourse.setDepartmentId(1);
        duplicateCourse.setMaxEnrollment(30);
        assertThrows(DuplicateEntityException.class,
                () -> courseService.addCourse(duplicateCourse));

        Student duplicateStudent = new Student();
        duplicateStudent.setFirstName("David");
        duplicateStudent.setLastName("Nsengimana");
        duplicateStudent.setEmail("david.nsengimana@student.rw");
        duplicateStudent.setGender("Male");
        duplicateStudent.setEnrollmentDate(Date.valueOf("2022-09-01"));
        duplicateStudent.setDepartmentId(1);
        duplicateStudent.setStatus("Active");
        assertThrows(DuplicateEntityException.class,
                () -> studentService.addStudent(duplicateStudent));

        Enrollment duplicateEnrollment = new Enrollment();
        duplicateEnrollment.setStudentId(1);
        duplicateEnrollment.setCourseId(1);
        duplicateEnrollment.setEnrollmentDate(Date.valueOf("2022-09-05"));
        duplicateEnrollment.setSemester("September");
        duplicateEnrollment.setAcademicYear("2022");
        duplicateEnrollment.setStatus("Enrolled");
        assertThrows(ValidationException.class,
                () -> enrollmentService.addEnrollment(duplicateEnrollment));
    }

    @Test
    @DisplayName("Register student, enroll them, and grade the enrollment end-to-end")
    void studentEnrollAndGrade_flow() {
        Student student = new Student();
        student.setFirstName("Integration");
        student.setLastName("Student");
        student.setEmail("integration.student@uni.test");
        student.setPassword("not-used-in-test");
        student.setGender("Female");
        student.setDateOfBirth(Date.valueOf("2001-03-10"));
        student.setEnrollmentDate(Date.valueOf("2026-09-01"));
        student.setDepartmentId(1);
        student.setGpa(0.0);
        student.setStatus("Active");
        assertTrue(studentService.addStudent(student));

        Course course = new Course();
        course.setCourseName("Integration Testing");
        course.setCourseCode("IT999");
        course.setCredits(3);
        course.setDepartmentId(1);
        course.setLecturerId(1);
        course.setDescription("Seed course for integration test");
        course.setMaxEnrollment(30);
        assertTrue(courseService.addCourse(course));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(student.getStudentId());
        enrollment.setCourseId(course.getCourseId());
        enrollment.setEnrollmentDate(Date.valueOf("2026-09-05"));
        enrollment.setSemester("September");
        enrollment.setAcademicYear("2026");
        enrollment.setStatus("Enrolled");
        assertTrue(enrollmentService.addEnrollment(enrollment));

        enrollment.setGrade("A");
        enrollment.setStatus("Completed");
        assertTrue(enrollmentService.updateEnrollment(enrollment));

        List<Enrollment> byStudent = enrollmentDao.getEnrollmentsByStudent(student.getStudentId());
        assertEquals(1, byStudent.size());
        assertEquals("A", byStudent.get(0).getGrade());
        assertEquals("Completed", byStudent.get(0).getStatus());
    }

    @Test
    @DisplayName("DAO layer propagates database failures as DataAccessException")
    void dao_propagatesDatabaseFailures() {
        User duplicateAdmin = new User();
        duplicateAdmin.setUsername("admin");
        duplicateAdmin.setFullName("Raw Admin");
        duplicateAdmin.setRole("Admin");

        assertThrows(DataAccessException.class,
                () -> userDao.addUser(duplicateAdmin, "secret123"),
                "Unique-constraint violation should surface as DataAccessException");
    }
}