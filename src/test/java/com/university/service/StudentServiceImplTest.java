package com.university.service;

import com.university.dao.StudentDao;
import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentServiceImpl Tests")
class StudentServiceImplTest {

    @Mock
    private StudentDao studentDao;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student buildStudent(int id, String firstName, String lastName, String email) {
        Student s = new Student();
        s.setStudentId(id);
        s.setFirstName(firstName);
        s.setLastName(lastName);
        s.setEmail(email);
        s.setPhone("+1234567890");
        s.setGpa(3.5);
        s.setStatus("Active");
        return s;
    }

    @Test
    @DisplayName("getAllStudents() delegates to studentDao")
    void getAllStudents_delegatesToDao() {
        List<Student> mockList = Arrays.asList(
                buildStudent(1, "Alice", "Smith", "alice@uni.edu"),
                buildStudent(2, "Bob", "Jones", "bob@uni.edu")
        );
        when(studentDao.getAllStudents()).thenReturn(mockList);

        List<Student> result = studentService.getAllStudents();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
        verify(studentDao, times(1)).getAllStudents();
    }

    @Test
    @DisplayName("addStudent() returns true for a valid student")
    void addStudent_validStudent_returnsTrue() {
        Student student = buildStudent(0, "Carol", "White", "carol@uni.edu");
        when(studentDao.getAllStudents()).thenReturn(Collections.emptyList());
        when(studentDao.addStudent(student)).thenReturn(true);

        boolean result = studentService.addStudent(student);

        assertTrue(result);
        verify(studentDao, times(1)).addStudent(student);
    }

    @Test
    @DisplayName("addStudent() throws DuplicateEntityException for duplicate email")
    void addStudent_duplicateEmail_throwsDuplicateEntityException() {
        Student existing = buildStudent(1, "Alice", "Smith", "alice@uni.edu");
        when(studentDao.getAllStudents()).thenReturn(Arrays.asList(existing));

        Student duplicate = buildStudent(0, "Bob", "Jones", "alice@uni.edu");

        DuplicateEntityException ex = assertThrows(DuplicateEntityException.class,
                () -> studentService.addStudent(duplicate));
        assertEquals("Student", ex.getEntityName());
        assertEquals("email", ex.getFieldName());
    }

    @Test
    @DisplayName("addStudent() throws ValidationException for empty first name")
    void addStudent_invalidName_throwsValidationException() {
        Student student = new Student();
        student.setFirstName("");
        student.setLastName("Doe");
        student.setEmail("test@uni.edu");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> studentService.addStudent(student));
        assertEquals("firstName", ex.getField());
    }

    @Test
    @DisplayName("searchStudents() delegates to studentDao")
    void searchStudents_delegatesToDao() {
        List<Student> mockResults = List.of(
                buildStudent(1, "Alice", "Smith", "alice@uni.edu")
        );
        when(studentDao.searchStudents("Alice")).thenReturn(mockResults);

        List<Student> result = studentService.searchStudents("Alice");

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
        verify(studentDao, times(1)).searchStudents("Alice");
    }

    @Test
    @DisplayName("deleteStudent() delegates to studentDao")
    void deleteStudent_delegatesToDao() {
        when(studentDao.deleteStudent(1)).thenReturn(true);

        boolean result = studentService.deleteStudent(1);

        assertTrue(result);
        verify(studentDao, times(1)).deleteStudent(1);
    }

    @Test
    @DisplayName("getStudentCount() delegates to studentDao")
    void getStudentCount_delegatesToDao() {
        when(studentDao.getStudentCount()).thenReturn(25);

        int count = studentService.getStudentCount();

        assertEquals(25, count);
        verify(studentDao, times(1)).getStudentCount();
    }

    @Test
    @DisplayName("isEmailDuplicate() returns true when email exists with different id")
    void isEmailDuplicate_existingEmail_returnsTrue() {
        Student existing = buildStudent(1, "Alice", "Smith", "alice@uni.edu");
        when(studentDao.getAllStudents()).thenReturn(Arrays.asList(existing));

        boolean result = studentService.isEmailDuplicate("alice@uni.edu", 0);

        assertTrue(result);
    }

    @Test
    @DisplayName("isEmailDuplicate() returns true for different id with same email")
    void isEmailDuplicate_differentId_returnsTrue() {
        Student existing = buildStudent(5, "Alice", "Smith", "alice@uni.edu");
        when(studentDao.getAllStudents()).thenReturn(Arrays.asList(existing));

        boolean result = studentService.isEmailDuplicate("alice@uni.edu", 10);

        assertTrue(result);
    }

    @Test
    @DisplayName("isEmailDuplicate() returns false when no duplicate exists")
    void isEmailDuplicate_noDuplicate_returnsFalse() {
        Student existing = buildStudent(1, "Alice", "Smith", "alice@uni.edu");
        when(studentDao.getAllStudents()).thenReturn(Arrays.asList(existing));

        boolean result = studentService.isEmailDuplicate("bob@uni.edu", 0);

        assertFalse(result);
    }
}
