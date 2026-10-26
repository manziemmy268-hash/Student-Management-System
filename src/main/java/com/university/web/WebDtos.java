package com.university.web;

import com.university.model.Course;
import com.university.model.Department;
import com.university.model.Enrollment;
import com.university.model.Lecturer;
import com.university.model.Student;
import com.university.model.User;

public final class WebDtos {

    private WebDtos() {
    }

    public static StudentResponse student(Student student) {
        return new StudentResponse(
                student.getStudentId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getPhone(),
                student.getDateOfBirth() == null ? null : student.getDateOfBirth().toString(),
                student.getGender(),
                student.getEnrollmentDate() == null ? null : student.getEnrollmentDate().toString(),
                student.getDepartmentId(),
                student.getDepartmentName(),
                student.getGpa(),
                student.getStatus());
    }

    public static CourseResponse course(Course course) {
        return new CourseResponse(
                course.getCourseId(),
                course.getCourseCode(),
                course.getCourseName(),
                course.getCredits(),
                course.getDepartmentName(),
                course.getLecturerId(),
                course.getLecturerName(),
                course.getDescription(),
                course.getMaxEnrollment());
    }

    public static EnrollmentResponse enrollment(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getEnrollmentId(),
                enrollment.getStudentId(),
                enrollment.getStudentName(),
                enrollment.getCourseId(),
                enrollment.getCourseName(),
                enrollment.getCourseCode(),
                enrollment.getEnrollmentDate() == null ? null : enrollment.getEnrollmentDate().toString(),
                enrollment.getSemester(),
                enrollment.getAcademicYear(),
                enrollment.getGrade(),
                enrollment.getStatus());
    }

    public static DepartmentResponse department(Department department) {
        return new DepartmentResponse(department.getDepartmentId(), department.getDepartmentName(),
                department.getDepartmentCode(), department.getLocation(), department.getPhone());
    }

    public static LecturerResponse lecturer(Lecturer lecturer) {
        return new LecturerResponse(lecturer.getLecturerId(), lecturer.getFirstName(), lecturer.getLastName(),
                lecturer.getEmail(), lecturer.getPhone(), lecturer.getHireDate() == null ? null : lecturer.getHireDate().toString(),
                lecturer.getDepartmentId(), lecturer.getDepartmentName(), lecturer.getRank());
    }

    public static UserResponse user(User user) {
        return new UserResponse(user.getUserId(), user.getUsername(), user.getFullName(), user.getRole());
    }

    public static final class StudentResponse {
        public final int id;
        public final String firstName;
        public final String lastName;
        public final String email;
        public final String phone;
        public final String dateOfBirth;
        public final String gender;
        public final String enrollmentDate;
        public final int departmentId;
        public final String department;
        public final double gpa;
        public final String status;

        private StudentResponse(int id, String firstName, String lastName, String email, String phone,
                    String dateOfBirth, String gender, String enrollmentDate, int departmentId,
                    String department, double gpa, String status) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phone = phone;
            this.dateOfBirth = dateOfBirth;
            this.gender = gender;
            this.enrollmentDate = enrollmentDate;
            this.departmentId = departmentId;
            this.department = department;
            this.gpa = gpa;
            this.status = status;
        }
    }

    public static final class CourseResponse {
        public final int id;
        public final String code;
        public final String name;
        public final int credits;
        public final String department;
        public final int lecturerId;
        public final String lecturer;
        public final String description;
        public final int maxEnrollment;

        private CourseResponse(int id, String code, String name, int credits, String department,
                               int lecturerId, String lecturer, String description, int maxEnrollment) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.credits = credits;
            this.department = department;
            this.lecturerId = lecturerId;
            this.lecturer = lecturer;
            this.description = description;
            this.maxEnrollment = maxEnrollment;
        }
    }

    public static final class EnrollmentResponse {
        public final int id;
        public final int studentId;
        public final String student;
        public final int courseId;
        public final String course;
        public final String courseCode;
        public final String enrollmentDate;
        public final String semester;
        public final String academicYear;
        public final String grade;
        public final String status;

        private EnrollmentResponse(int id, int studentId, String student, int courseId, String course,
                                   String courseCode, String enrollmentDate, String semester,
                                   String academicYear, String grade, String status) {
            this.id = id;
            this.studentId = studentId;
            this.student = student;
            this.courseId = courseId;
            this.course = course;
            this.courseCode = courseCode;
            this.enrollmentDate = enrollmentDate;
            this.semester = semester;
            this.academicYear = academicYear;
            this.grade = grade;
            this.status = status;
        }
    }

    public static final class DepartmentResponse {
        public final int id;
        public final String name;
        public final String code;
        public final String location;
        public final String phone;

        private DepartmentResponse(int id, String name, String code, String location, String phone) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.location = location;
            this.phone = phone;
        }
    }

    public static final class LecturerResponse {
        public final int id;
        public final String firstName;
        public final String lastName;
        public final String email;
        public final String phone;
        public final String hireDate;
        public final int departmentId;
        public final String department;
        public final String rank;

        private LecturerResponse(int id, String firstName, String lastName, String email, String phone,
                                 String hireDate, int departmentId, String department, String rank) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phone = phone;
            this.hireDate = hireDate;
            this.departmentId = departmentId;
            this.department = department;
            this.rank = rank;
        }
    }

    public static final class UserResponse {
        public final int id;
        public final String username;
        public final String fullName;
        public final String role;

        private UserResponse(int id, String username, String fullName, String role) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
            this.role = role;
        }
    }
}
