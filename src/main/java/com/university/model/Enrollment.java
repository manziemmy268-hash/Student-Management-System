package com.university.model;

import jakarta.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private int enrollmentId;

    @Column(name = "student_id", nullable = false)
    private int studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    @Column(name = "course_id", nullable = false)
    private int courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    @Column(name = "enrollment_date", nullable = false)
    private Date enrollmentDate;

    @Column(name = "semester", nullable = false)
    private String semester;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "grade")
    private String grade;

    @Column(name = "status")
    private String status;

    public Enrollment() {}

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public String getStudentName() {
        return student != null ? student.getFullName() : null;
    }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public String getCourseName() {
        return course != null ? course.getCourseName() : null;
    }
    public String getCourseCode() {
        return course != null ? course.getCourseCode() : null;
    }
    public Date getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Date enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        return enrollmentId == that.enrollmentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enrollmentId);
    }
}
