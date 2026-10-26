package com.university.web;

import com.university.model.User;
import com.university.model.Student;
import com.university.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.sql.Date;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final StudentService studentService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthController(AuthenticationManager authenticationManager, StudentService studentService) {
        this.authenticationManager = authenticationManager;
        this.studentService = studentService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest,
                                   HttpServletResponse httpResponse) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username, request.password));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        return ResponseEntity.ok(userResponse((User) authentication.getPrincipal()));
    }

    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudent(@RequestBody StudentRegistrationRequest request,
                                             HttpServletRequest httpRequest,
                                             HttpServletResponse httpResponse) {
        if (isBlank(request.firstName) || isBlank(request.lastName) || isBlank(request.email)
                || isBlank(request.password) || isBlank(request.gender)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "First name, last name, email, password, and gender are required");
        }
        if (request.password.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password must be at least 8 characters");
        }

        Student student = new Student();
        student.setFirstName(request.firstName);
        student.setLastName(request.lastName);
        student.setEmail(request.email);
        student.setPassword(request.password);
        student.setPhone(request.phone);
        student.setGender(request.gender);
        student.setEnrollmentDate(parseDate(request.enrollmentDate));
        student.setDateOfBirth(parseOptionalDate(request.dateOfBirth));
        student.setDepartmentId(request.departmentId);
        student.setGpa(0);
        student.setStatus("Active");
        studentService.addStudent(student);

        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.email, request.password));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse((User) authentication.getPrincipal()));
    }

    @GetMapping("/me")
    public Map<String, Object> currentUser(Authentication authentication) {
        return userResponse((User) authentication.getPrincipal());
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("token", token.getToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) {
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Map<String, Object> userResponse(User user) {
        return Map.of(
                "id", user.getUserId(),
                "username", user.getUsername(),
                "fullName", user.getFullName(),
                "role", user.getRole(),
                "studentId", user.getStudentId(),
                "lecturerId", user.getLecturerId());
    }

    private Date parseDate(String value) {
        if (isBlank(value)) {
            return Date.valueOf(LocalDate.now());
        }
        try {
            return Date.valueOf(LocalDate.parse(value));
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dates must use yyyy-MM-dd format");
        }
    }

    private Date parseOptionalDate(String value) {
        if (isBlank(value)) {
            return null;
        }
        return parseDate(value);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class StudentRegistrationRequest {
        public String firstName;
        public String lastName;
        public String email;
        public String password;
        public String phone;
        public String dateOfBirth;
        public String gender;
        public String enrollmentDate;
        public int departmentId;
    }
}
