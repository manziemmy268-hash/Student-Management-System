package com.university.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private int courseId;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "course_code", nullable = false, unique = true)
    private String courseCode;

    @Column(name = "credits", nullable = false)
    private int credits;

    @Column(name = "department_id")
    private int departmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    private Department department;

    @Column(name = "lecturer_id")
    private int lecturerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", insertable = false, updatable = false)
    private Lecturer lecturer;

    @Column(name = "description")
    private String description;

    @Column(name = "max_enrollment")
    private int maxEnrollment;

    public Course() {}

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public String getDepartmentName() {
        return department != null ? department.getDepartmentName() : null;
    }
    public int getLecturerId() { return lecturerId; }
    public void setLecturerId(int lecturerId) { this.lecturerId = lecturerId; }
    public Lecturer getLecturer() { return lecturer; }
    public void setLecturer(Lecturer lecturer) { this.lecturer = lecturer; }
    public String getLecturerName() {
        return lecturer != null ? lecturer.getFullName() : null;
    }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getMaxEnrollment() { return maxEnrollment; }
    public void setMaxEnrollment(int maxEnrollment) { this.maxEnrollment = maxEnrollment; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return courseId == course.courseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }

    @Override
    public String toString() {
        return (courseName != null ? courseName : "") + " (" + (courseCode != null ? courseCode : "") + ")";
    }
}
