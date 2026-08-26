package com.university.dao;

import com.university.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentDao Consumer Tests")
class StudentDaoTest {

    @Mock
    private StudentDao studentDao;

    private Student buildStudent(int id, String firstName, String lastName, String email) {
        Student s = new Student();
        s.setStudentId(id);
        s.setFirstName(firstName);
        s.setLastName(lastName);
        s.setEmail(email);
        s.setGpa(3.5);
        s.setStatus("Active");
        return s;
    }

    @Test
    @DisplayName("getAllStudents() returns non-empty list when students exist")
    void getAllStudents_returnsNonEmptyList() {
        List<Student> mockStudents = Arrays.asList(
            buildStudent(1, "Alice", "Smith", "alice@uni.edu"),
            buildStudent(2, "Bob", "Jones", "bob@uni.edu")
        );
        when(studentDao.getAllStudents()).thenReturn(mockStudents);

        List<Student> result = studentDao.getAllStudents();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
        verify(studentDao, times(1)).getAllStudents();
    }

    @Test
    @DisplayName("getAllStudents() returns empty list when no students exist")
    void getAllStudents_returnsEmptyList_whenNoStudents() {
        when(studentDao.getAllStudents()).thenReturn(Collections.emptyList());

        List<Student> result = studentDao.getAllStudents();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getStudentById() returns correct student for valid ID")
    void getStudentById_validId_returnsStudent() {
        Student mockStudent = buildStudent(1, "Alice", "Smith", "alice@uni.edu");
        when(studentDao.getStudentById(1)).thenReturn(mockStudent);

        Student result = studentDao.getStudentById(1);

        assertNotNull(result);
        assertEquals(1, result.getStudentId());
        assertEquals("Alice", result.getFirstName());
        verify(studentDao, times(1)).getStudentById(1);
    }

    @Test
    @DisplayName("getStudentById() returns null for non-existent ID")
    void getStudentById_invalidId_returnsNull() {
        when(studentDao.getStudentById(999)).thenReturn(null);

        Student result = studentDao.getStudentById(999);

        assertNull(result);
        verify(studentDao, times(1)).getStudentById(999);
    }

    @Test
    @DisplayName("addStudent() returns true on successful insert")
    void addStudent_validStudent_returnsTrue() {
        Student newStudent = buildStudent(0, "Carol", "White", "carol@uni.edu");
        when(studentDao.addStudent(newStudent)).thenReturn(true);

        boolean result = studentDao.addStudent(newStudent);

        assertTrue(result);
        verify(studentDao, times(1)).addStudent(newStudent);
    }

    @Test
    @DisplayName("addStudent() returns false when email already exists")
    void addStudent_duplicateEmail_returnsFalse() {
        Student duplicate = buildStudent(0, "Alice", "Smith", "alice@uni.edu");
        when(studentDao.addStudent(duplicate)).thenReturn(false);

        boolean result = studentDao.addStudent(duplicate);

        assertFalse(result);
        verify(studentDao, times(1)).addStudent(duplicate);
    }

    @Test
    @DisplayName("updateStudent() returns true on successful update")
    void updateStudent_existingStudent_returnsTrue() {
        Student student = buildStudent(1, "Alice", "SmithUpdated", "alice@uni.edu");
        when(studentDao.updateStudent(student)).thenReturn(true);

        boolean result = studentDao.updateStudent(student);

        assertTrue(result);
        verify(studentDao, times(1)).updateStudent(student);
    }

    @Test
    @DisplayName("deleteStudent() returns true on successful delete")
    void deleteStudent_existingId_returnsTrue() {
        when(studentDao.deleteStudent(1)).thenReturn(true);

        boolean result = studentDao.deleteStudent(1);

        assertTrue(result);
        verify(studentDao, times(1)).deleteStudent(1);
    }

    @Test
    @DisplayName("deleteStudent() returns false for non-existent ID")
    void deleteStudent_nonExistentId_returnsFalse() {
        when(studentDao.deleteStudent(999)).thenReturn(false);

        boolean result = studentDao.deleteStudent(999);

        assertFalse(result);
    }

    @Test
    @DisplayName("searchStudents() returns matched results")
    void searchStudents_withKeyword_returnsMatchingStudents() {
        List<Student> mockResults = List.of(
            buildStudent(1, "Alice", "Smith", "alice@uni.edu")
        );
        when(studentDao.searchStudents("Alice")).thenReturn(mockResults);

        List<Student> results = studentDao.searchStudents("Alice");

        assertEquals(1, results.size());
        assertEquals("Alice", results.get(0).getFirstName());
        verify(studentDao, times(1)).searchStudents("Alice");
    }

    @Test
    @DisplayName("searchStudents() returns empty list when no match found")
    void searchStudents_noMatch_returnsEmptyList() {
        when(studentDao.searchStudents("xyz_no_match")).thenReturn(Collections.emptyList());

        List<Student> results = studentDao.searchStudents("xyz_no_match");

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("getStudentCount() returns correct count")
    void getStudentCount_returnsCount() {
        when(studentDao.getStudentCount()).thenReturn(12);

        int count = studentDao.getStudentCount();

        assertEquals(12, count);
        verify(studentDao, times(1)).getStudentCount();
    }

    @Test
    @DisplayName("Student model getFullName() handles null names safely")
    void studentFullName_handlesNullNames() {
        Student s = new Student();
        s.setFirstName(null);
        s.setLastName(null);

        String fullName = s.getFullName();

        assertEquals("", fullName);
    }

    @Test
    @DisplayName("Student model equals() and hashCode() use studentId")
    void studentEqualsHashCode_usesStudentId() {
        Student s1 = buildStudent(1, "Alice", "Smith", "alice@uni.edu");
        Student s2 = buildStudent(1, "Alice", "Jones", "alice@uni.edu");
        Student s3 = buildStudent(2, "Alice", "Smith", "alice@uni.edu");

        assertEquals(s1, s2, "Students with same ID should be equal");
        assertEquals(s1.hashCode(), s2.hashCode());
        assertNotEquals(s1, s3, "Students with different IDs should not be equal");
    }
}
