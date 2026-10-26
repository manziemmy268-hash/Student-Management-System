package com.university.service;

import com.university.model.Course;
import com.university.model.Enrollment;
import com.university.model.Student;
import java.util.List;
import java.util.Map;

public interface EnrollmentService {

    List<Enrollment> getAllEnrollments();

    Enrollment getEnrollmentById(int enrollmentId);

    List<Student> getAllStudents();

    List<Course> getAllCourses();

    List<Enrollment> getEnrollmentsByStudent(int studentId);

    List<Enrollment> getEnrollmentsByCourse(int courseId);

    int getEnrollmentCountByCourse(int courseId);

    Map<Integer, Integer> getEnrollmentCountsByCourse();

    boolean addEnrollment(Enrollment enrollment);

    boolean updateEnrollment(Enrollment enrollment);

    boolean deleteEnrollment(int enrollmentId);

    List<Enrollment> searchEnrollments(String keyword);

    int getEnrollmentCount();

    boolean isDuplicateEnrollment(int studentId, int courseId, String semester, String academicYear, int excludeId);
}
