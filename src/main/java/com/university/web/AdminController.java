package com.university.web;

import com.university.model.Course;
import com.university.model.Department;
import com.university.model.Lecturer;
import com.university.model.Student;
import com.university.model.User;
import com.university.service.CourseService;
import com.university.service.DepartmentService;
import com.university.service.LecturerService;
import com.university.service.StudentService;
import com.university.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final StudentService studentService;
    private final LecturerService lecturerService;
    private final DepartmentService departmentService;
    private final CourseService courseService;
    private final UserService userService;

    public AdminController(StudentService studentService, LecturerService lecturerService,
                           DepartmentService departmentService, CourseService courseService,
                           UserService userService) {
        this.studentService = studentService;
        this.lecturerService = lecturerService;
        this.departmentService = departmentService;
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/departments")
    public List<WebDtos.DepartmentResponse> getDepartments(Authentication authentication) {
        requireAcademicRole(authentication);
        return departmentService.getAllDepartments().stream()
                .map(WebDtos::department)
                .collect(Collectors.toList());
    }

    @PostMapping("/departments")
    public ResponseEntity<WebDtos.DepartmentResponse> addDepartment(@RequestBody DepartmentRequest request,
                                                                    Authentication authentication) {
        requireAcademicRole(authentication);
        Department department = new Department();
        apply(request, department);
        departmentService.addDepartment(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(WebDtos.department(
                departmentService.getDepartmentById(department.getDepartmentId())));
    }

    @PutMapping("/departments/{id}")
    public WebDtos.DepartmentResponse updateDepartment(@PathVariable int id,
                                                       @RequestBody DepartmentRequest request,
                                                       Authentication authentication) {
        requireAcademicRole(authentication);
        Department department = departmentService.getDepartmentById(id);
        requireFound(department, "Department");
        apply(request, department);
        departmentService.updateDepartment(department);
        return WebDtos.department(departmentService.getDepartmentById(id));
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable int id, Authentication authentication) {
        requireAcademicRole(authentication);
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/students")
    public ResponseEntity<WebDtos.StudentResponse> addStudent(@RequestBody StudentRequest request,
                                                              Authentication authentication) {
        requireAcademicRole(authentication);
        Student student = new Student();
        apply(request, student);
        studentService.addStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(WebDtos.student(
                studentService.getStudentById(student.getStudentId())));
    }

    @PutMapping("/students/{id}")
    public WebDtos.StudentResponse updateStudent(@PathVariable int id, @RequestBody StudentRequest request,
                                                 Authentication authentication) {
        requireAcademicRole(authentication);
        Student student = studentService.getStudentById(id);
        requireFound(student, "Student");
        apply(request, student);
        studentService.updateStudent(student);
        return WebDtos.student(studentService.getStudentById(id));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable int id, Authentication authentication) {
        requireAcademicRole(authentication);
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lecturers")
    public List<WebDtos.LecturerResponse> getLecturers(Authentication authentication) {
        requireAcademicRole(authentication);
        return lecturerService.getAllLecturers().stream()
                .map(WebDtos::lecturer)
                .collect(Collectors.toList());
    }

    @PostMapping("/lecturers")
    public ResponseEntity<WebDtos.LecturerResponse> addLecturer(@RequestBody LecturerRequest request,
                                                                Authentication authentication) {
        requireAcademicRole(authentication);
        Lecturer lecturer = new Lecturer();
        apply(request, lecturer);
        lecturerService.addLecturer(lecturer);
        return ResponseEntity.status(HttpStatus.CREATED).body(WebDtos.lecturer(
                lecturerService.getLecturerById(lecturer.getLecturerId())));
    }

    @PutMapping("/lecturers/{id}")
    public WebDtos.LecturerResponse updateLecturer(@PathVariable int id, @RequestBody LecturerRequest request,
                                                   Authentication authentication) {
        requireAcademicRole(authentication);
        Lecturer lecturer = lecturerService.getLecturerById(id);
        requireFound(lecturer, "Lecturer");
        apply(request, lecturer);
        lecturerService.updateLecturer(lecturer);
        return WebDtos.lecturer(lecturerService.getLecturerById(id));
    }

    @DeleteMapping("/lecturers/{id}")
    public ResponseEntity<Void> deleteLecturer(@PathVariable int id, Authentication authentication) {
        requireAcademicRole(authentication);
        lecturerService.deleteLecturer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/courses")
    public ResponseEntity<WebDtos.CourseResponse> addCourse(@RequestBody CourseRequest request,
                                                            Authentication authentication) {
        requireAcademicRole(authentication);
        Course course = new Course();
        apply(request, course);
        courseService.addCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(WebDtos.course(
                courseService.getCourseById(course.getCourseId())));
    }

    @PutMapping("/courses/{id}")
    public WebDtos.CourseResponse updateCourse(@PathVariable int id, @RequestBody CourseRequest request,
                                               Authentication authentication) {
        requireAcademicRole(authentication);
        Course course = courseService.getCourseById(id);
        requireFound(course, "Course");
        apply(request, course);
        courseService.updateCourse(course);
        return WebDtos.course(courseService.getCourseById(id));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable int id, Authentication authentication) {
        requireAcademicRole(authentication);
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public List<WebDtos.UserResponse> getUsers(Authentication authentication) {
        requireAdmin(authentication);
        return userService.getAllUsers().stream()
                .map(WebDtos::user)
                .collect(Collectors.toList());
    }

    @PostMapping("/users")
    public ResponseEntity<WebDtos.UserResponse> addUser(@RequestBody UserRequest request,
                                                         Authentication authentication) {
        requireAdmin(authentication);
        User user = new User();
        user.setUsername(request.username);
        user.setFullName(request.fullName);
        user.setRole(request.role);
        userService.addUser(user, request.password);
        return ResponseEntity.status(HttpStatus.CREATED).body(WebDtos.user(userService.getUserById(user.getUserId())));
    }

    @PutMapping("/users/{id}")
    public WebDtos.UserResponse updateUser(@PathVariable int id, @RequestBody UserRequest request,
                                           Authentication authentication) {
        requireAdmin(authentication);
        User user = userService.getUserById(id);
        requireFound(user, "User");
        user.setUsername(request.username);
        user.setFullName(request.fullName);
        user.setRole(request.role);
        userService.updateUser(user);
        return WebDtos.user(userService.getUserById(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id, Authentication authentication) {
        User currentUser = currentUser(authentication);
        requireAdmin(currentUser);
        if (currentUser.getUserId() == id) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You cannot delete your own account");
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private void apply(DepartmentRequest request, Department department) {
        department.setDepartmentName(request.name);
        department.setDepartmentCode(request.code);
        department.setLocation(request.location);
        department.setPhone(request.phone);
    }

    private void apply(StudentRequest request, Student student) {
        student.setFirstName(request.firstName);
        student.setLastName(request.lastName);
        student.setEmail(request.email);
        student.setPhone(request.phone);
        student.setDateOfBirth(parseDate(request.dateOfBirth, "dateOfBirth"));
        student.setGender(request.gender);
        student.setEnrollmentDate(parseDate(request.enrollmentDate, "enrollmentDate"));
        student.setDepartmentId(request.departmentId);
        student.setGpa(request.gpa);
        student.setStatus(request.status);
        if (request.password != null && !request.password.isBlank()) {
            student.setPassword(request.password);
        }
    }

    private void apply(LecturerRequest request, Lecturer lecturer) {
        lecturer.setFirstName(request.firstName);
        lecturer.setLastName(request.lastName);
        lecturer.setEmail(request.email);
        lecturer.setPhone(request.phone);
        lecturer.setHireDate(parseDate(request.hireDate, "hireDate"));
        lecturer.setDepartmentId(request.departmentId);
        lecturer.setRank(request.rank);
        if (request.password != null && !request.password.isBlank()) {
            lecturer.setPassword(request.password);
        }
    }

    private void apply(CourseRequest request, Course course) {
        course.setCourseName(request.name);
        course.setCourseCode(request.code);
        course.setCredits(request.credits);
        course.setDepartmentId(request.departmentId);
        course.setLecturerId(request.lecturerId);
        course.setDescription(request.description);
        course.setMaxEnrollment(request.maxEnrollment);
    }

    private Date parseDate(String value, String field) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Date.valueOf(LocalDate.parse(value));
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    field + " must use yyyy-MM-dd format");
        }
    }

    private void requireAcademicRole(Authentication authentication) {
        User user = currentUser(authentication);
        if (Arrays.stream(new String[]{"Admin", "Staff"}).noneMatch(role -> role.equals(user.getRole()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions");
        }
    }

    private void requireAdmin(Authentication authentication) {
        requireAdmin(currentUser(authentication));
    }

    private void requireAdmin(User user) {
        if (!user.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator role required");
        }
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return (User) authentication.getPrincipal();
    }

    private void requireFound(Object value, String entity) {
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, entity + " not found");
        }
    }

    public static class DepartmentRequest {
        public String name;
        public String code;
        public String location;
        public String phone;
    }

    public static class StudentRequest {
        public String firstName;
        public String lastName;
        public String email;
        public String password;
        public String phone;
        public String dateOfBirth;
        public String gender;
        public String enrollmentDate;
        public int departmentId;
        public double gpa;
        public String status;
    }

    public static class LecturerRequest {
        public String firstName;
        public String lastName;
        public String email;
        public String password;
        public String phone;
        public String hireDate;
        public int departmentId;
        public String rank;
    }

    public static class CourseRequest {
        public String name;
        public String code;
        public int credits;
        public int departmentId;
        public int lecturerId;
        public String description;
        public int maxEnrollment;
    }

    public static class UserRequest {
        public String username;
        public String fullName;
        public String role;
        public String password;
    }
}
