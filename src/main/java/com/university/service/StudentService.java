package com.university.service;

import com.university.model.Student;
import java.util.List;

public interface StudentService {

    List<Student> getAllStudents();

    Student getStudentById(int id);

    List<Student> searchStudents(String keyword);

    boolean addStudent(Student student);

    boolean updateStudent(Student student);

    boolean deleteStudent(int studentId);

    int getStudentCount();

    boolean isEmailDuplicate(String email, int excludeId);
}
