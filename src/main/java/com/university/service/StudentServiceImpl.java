package com.university.service;

import com.university.dao.StudentDao;
import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.Student;
import com.university.utility.ValidationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);
    private final StudentDao studentDao;

    public StudentServiceImpl() {
        this.studentDao = new StudentDao();
    }

    public StudentServiceImpl(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    @Override
    public boolean addStudent(Student student) {
        logger.info("Adding student: {} {}", student.getFirstName(), student.getLastName());

        if (!ValidationUtil.isValidName(student.getFirstName())) {
            throw new ValidationException("firstName", "First name must be at least 2 characters");
        }
        if (!ValidationUtil.isValidName(student.getLastName())) {
            throw new ValidationException("lastName", "Last name must be at least 2 characters");
        }
        if (!ValidationUtil.isValidEmail(student.getEmail())) {
            throw new ValidationException("email", "Invalid email format");
        }
        if (student.getPhone() != null && !student.getPhone().isEmpty()
                && !ValidationUtil.isValidPhone(student.getPhone())) {
            throw new ValidationException("phone", "Invalid phone number format");
        }
        if (isEmailDuplicate(student.getEmail(), 0)) {
            throw new DuplicateEntityException("Student", "email", student.getEmail());
        }

        student.setFirstName(ValidationUtil.sanitizeInput(student.getFirstName()));
        student.setLastName(ValidationUtil.sanitizeInput(student.getLastName()));
        student.setEmail(ValidationUtil.sanitizeInput(student.getEmail()));
        student.setPhone(ValidationUtil.sanitizeInput(student.getPhone()));

        boolean result = studentDao.addStudent(student);
        if (result) {
            logger.info("Student added successfully: {} {}", student.getFirstName(), student.getLastName());
        } else {
            logger.error("Failed to add student: {} {}", student.getFirstName(), student.getLastName());
        }
        return result;
    }

    @Override
    public boolean updateStudent(Student student) {
        logger.info("Updating student id: {}", student.getStudentId());

        if (!ValidationUtil.isValidName(student.getFirstName())) {
            throw new ValidationException("firstName", "First name must be at least 2 characters");
        }
        if (!ValidationUtil.isValidName(student.getLastName())) {
            throw new ValidationException("lastName", "Last name must be at least 2 characters");
        }
        if (!ValidationUtil.isValidEmail(student.getEmail())) {
            throw new ValidationException("email", "Invalid email format");
        }
        if (student.getPhone() != null && !student.getPhone().isEmpty()
                && !ValidationUtil.isValidPhone(student.getPhone())) {
            throw new ValidationException("phone", "Invalid phone number format");
        }
        if (isEmailDuplicate(student.getEmail(), student.getStudentId())) {
            throw new DuplicateEntityException("Student", "email", student.getEmail());
        }

        student.setFirstName(ValidationUtil.sanitizeInput(student.getFirstName()));
        student.setLastName(ValidationUtil.sanitizeInput(student.getLastName()));
        student.setEmail(ValidationUtil.sanitizeInput(student.getEmail()));
        student.setPhone(ValidationUtil.sanitizeInput(student.getPhone()));

        boolean result = studentDao.updateStudent(student);
        if (result) {
            logger.info("Student updated successfully id: {}", student.getStudentId());
        } else {
            logger.error("Failed to update student id: {}", student.getStudentId());
        }
        return result;
    }

    @Override
    public boolean deleteStudent(int studentId) {
        logger.info("Deleting student id: {}", studentId);
        boolean result = studentDao.deleteStudent(studentId);
        if (result) {
            logger.info("Student deleted successfully id: {}", studentId);
        } else {
            logger.error("Failed to delete student id: {}", studentId);
        }
        return result;
    }

    @Override
    public List<Student> getAllStudents() {
        return studentDao.getAllStudents();
    }

    @Override
    public Student getStudentById(int id) {
        return studentDao.getStudentById(id);
    }

    @Override
    public List<Student> searchStudents(String keyword) {
        return studentDao.searchStudents(keyword);
    }

    @Override
    public int getStudentCount() {
        return studentDao.getStudentCount();
    }

    @Override
    public boolean isEmailDuplicate(String email, int excludeId) {
        List<Student> students = getAllStudents();
        return students.stream()
                .anyMatch(s -> s.getEmail().equalsIgnoreCase(email) && s.getStudentId() != excludeId);
    }
}
