package com.university.web;

import com.university.exception.ValidationException;
import com.university.model.Course;
import com.university.model.Enrollment;
import com.university.model.Student;
import com.university.model.User;
import com.university.service.CourseService;
import com.university.service.EnrollmentService;
import com.university.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class WebDataController {

    private static final Set<String> VALID_GRADES = Set.of(
            "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F");
    private static final Set<String> VALID_ENROLLMENT_STATUSES = Set.of(
            "Enrolled", "Completed", "Dropped", "Failed");

    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public WebDataController(StudentService studentService, CourseService courseService,
                             EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/students")
    public List<WebDtos.StudentResponse> students(Authentication authentication) {
        requireRole(authentication, "Admin", "Staff");
        return studentService.getAllStudents().stream()
                .map(WebDtos::student)
                .collect(Collectors.toList());
    }

    @GetMapping("/courses")
    public List<WebDtos.CourseResponse> courses(Authentication authentication) {
        User user = currentUser(authentication);
        List<Course> courses = user.isStudent()
                ? courseService.getAvailableCourses()
                : courseService.getAllCourses();
        return courses.stream()
                .map(WebDtos::course)
                .collect(Collectors.toList());
    }

    @GetMapping("/me/enrollments")
    public List<WebDtos.EnrollmentResponse> myEnrollments(Authentication authentication) {
        User user = currentUser(authentication);
        requireRole(user, "Student");
        return enrollmentService.getEnrollmentsByStudent(user.getStudentId()).stream()
                .map(WebDtos::enrollment)
                .collect(Collectors.toList());
    }

    @PostMapping("/me/enrollments")
    public ResponseEntity<WebDtos.EnrollmentResponse> enroll(@RequestBody EnrollmentRequest request,
                                                              Authentication authentication) {
        User user = currentUser(authentication);
        requireRole(user, "Student");
        Course course = courseService.getCourseById(request.courseId);
        if (course == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
        if (enrollmentService.getEnrollmentCountByCourse(course.getCourseId()) >= course.getMaxEnrollment()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Course has reached its enrollment capacity");
        }
        if (enrollmentService.isDuplicateEnrollment(user.getStudentId(), request.courseId,
                request.semester, request.academicYear, -1)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student is already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(user.getStudentId());
        enrollment.setCourseId(request.courseId);
        enrollment.setEnrollmentDate(Date.valueOf(LocalDate.now()));
        enrollment.setSemester(request.semester);
        enrollment.setAcademicYear(request.academicYear);
        enrollment.setStatus("Enrolled");
        enrollmentService.addEnrollment(enrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(WebDtos.enrollment(
                enrollmentService.getEnrollmentById(enrollment.getEnrollmentId())));
    }

    @GetMapping("/lecturer/courses")
    public List<WebDtos.CourseResponse> lecturerCourses(Authentication authentication) {
        User user = currentUser(authentication);
        requireRole(user, "Lecturer");
        return courseService.getCoursesByLecturer(user.getLecturerId()).stream()
                .map(WebDtos::course)
                .collect(Collectors.toList());
    }

    @GetMapping("/lecturer/courses/{courseId}/enrollments")
    public List<WebDtos.EnrollmentResponse> courseEnrollments(@PathVariable int courseId,
                                                              Authentication authentication) {
        User user = currentUser(authentication);
        requireRole(user, "Lecturer");
        verifyLecturerOwnsCourse(user, courseId);
        return enrollmentService.getEnrollmentsByCourse(courseId).stream()
                .map(WebDtos::enrollment)
                .collect(Collectors.toList());
    }

    @PatchMapping("/lecturer/enrollments/{enrollmentId}/grade")
    public WebDtos.EnrollmentResponse updateGrade(@PathVariable int enrollmentId,
                                                  @RequestBody GradeRequest request,
                                                  Authentication authentication) {
        User user = currentUser(authentication);
        requireRole(user, "Lecturer");
        Enrollment enrollment = enrollmentService.getEnrollmentById(enrollmentId);
        if (enrollment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found");
        }
        verifyLecturerOwnsCourse(user, enrollment.getCourseId());
        if (request.grade == null || !VALID_GRADES.contains(request.grade)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid grade");
        }
        String status = request.status == null ? "Completed" : request.status;
        if (!VALID_ENROLLMENT_STATUSES.contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid enrollment status");
        }
        enrollment.setGrade(request.grade);
        enrollment.setStatus(status);
        enrollmentService.updateEnrollment(enrollment);
        return WebDtos.enrollment(enrollmentService.getEnrollmentById(enrollmentId));
    }

    private void verifyLecturerOwnsCourse(User user, int courseId) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
        if (course.getLecturerId() != user.getLecturerId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Lecturer is not assigned to this course");
        }
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return (User) authentication.getPrincipal();
    }

    private void requireRole(Authentication authentication, String... roles) {
        requireRole(currentUser(authentication), roles);
    }

    private void requireRole(User user, String... roles) {
        if (Arrays.stream(roles).noneMatch(role -> role.equals(user.getRole()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions");
        }
    }

    public static class EnrollmentRequest {
        public int courseId;
        public String semester;
        public String academicYear;
    }

    public static class GradeRequest {
        public String grade;
        public String status;
    }
}
