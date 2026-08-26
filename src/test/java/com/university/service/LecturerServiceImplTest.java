package com.university.service;

import com.university.dao.LecturerDao;
import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.Lecturer;
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
@DisplayName("LecturerServiceImpl Tests")
class LecturerServiceImplTest {

    @Mock
    private LecturerDao lecturerDao;

    @InjectMocks
    private LecturerServiceImpl lecturerService;

    private Lecturer buildLecturer(int id, String firstName, String lastName, String email) {
        Lecturer l = new Lecturer();
        l.setLecturerId(id);
        l.setFirstName(firstName);
        l.setLastName(lastName);
        l.setEmail(email);
        l.setPhone("+1234567890");
        l.setRank("Professor");
        return l;
    }

    @Test
    @DisplayName("getAllLecturers() delegates to lecturerDao")
    void getAllLecturers_delegatesToDao() {
        List<Lecturer> mockList = Arrays.asList(
                buildLecturer(1, "Alice", "Smith", "alice@uni.edu"),
                buildLecturer(2, "Bob", "Jones", "bob@uni.edu")
        );
        when(lecturerDao.getAllLecturers()).thenReturn(mockList);

        List<Lecturer> result = lecturerService.getAllLecturers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
        verify(lecturerDao, times(1)).getAllLecturers();
    }

    @Test
    @DisplayName("addLecturer() returns true for a valid lecturer")
    void addLecturer_validLecturer_returnsTrue() {
        Lecturer lecturer = buildLecturer(0, "Carol", "White", "carol@uni.edu");
        when(lecturerDao.getAllLecturers()).thenReturn(Collections.emptyList());
        when(lecturerDao.addLecturer(lecturer)).thenReturn(true);

        boolean result = lecturerService.addLecturer(lecturer);

        assertTrue(result);
        verify(lecturerDao, times(1)).addLecturer(lecturer);
    }

    @Test
    @DisplayName("addLecturer() throws DuplicateEntityException for duplicate email")
    void addLecturer_duplicateEmail_throwsDuplicateEntityException() {
        Lecturer existing = buildLecturer(1, "Alice", "Smith", "alice@uni.edu");
        when(lecturerDao.getAllLecturers()).thenReturn(Arrays.asList(existing));

        Lecturer duplicate = buildLecturer(0, "Bob", "Jones", "alice@uni.edu");

        DuplicateEntityException ex = assertThrows(DuplicateEntityException.class,
                () -> lecturerService.addLecturer(duplicate));
        assertEquals("Lecturer", ex.getEntityName());
        assertEquals("email", ex.getFieldName());
    }

    @Test
    @DisplayName("addLecturer() throws ValidationException for empty first name")
    void addLecturer_invalidName_throwsValidationException() {
        Lecturer lecturer = new Lecturer();
        lecturer.setFirstName("");
        lecturer.setLastName("Doe");
        lecturer.setEmail("test@uni.edu");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> lecturerService.addLecturer(lecturer));
        assertEquals("firstName", ex.getField());
    }

    @Test
    @DisplayName("searchLecturers() delegates to lecturerDao")
    void searchLecturers_delegatesToDao() {
        List<Lecturer> mockResults = List.of(
                buildLecturer(1, "Alice", "Smith", "alice@uni.edu")
        );
        when(lecturerDao.searchLecturers("Alice")).thenReturn(mockResults);

        List<Lecturer> result = lecturerService.searchLecturers("Alice");

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getFirstName());
        verify(lecturerDao, times(1)).searchLecturers("Alice");
    }

    @Test
    @DisplayName("deleteLecturer() delegates to lecturerDao")
    void deleteLecturer_delegatesToDao() {
        when(lecturerDao.deleteLecturer(1)).thenReturn(true);

        boolean result = lecturerService.deleteLecturer(1);

        assertTrue(result);
        verify(lecturerDao, times(1)).deleteLecturer(1);
    }

    @Test
    @DisplayName("getLecturerCount() delegates to lecturerDao")
    void getLecturerCount_delegatesToDao() {
        when(lecturerDao.getLecturerCount()).thenReturn(15);

        int count = lecturerService.getLecturerCount();

        assertEquals(15, count);
        verify(lecturerDao, times(1)).getLecturerCount();
    }

    @Test
    @DisplayName("isEmailDuplicate() returns true when email exists with different id")
    void isEmailDuplicate_existingEmail_returnsTrue() {
        Lecturer existing = buildLecturer(1, "Alice", "Smith", "alice@uni.edu");
        when(lecturerDao.getAllLecturers()).thenReturn(Arrays.asList(existing));

        boolean result = lecturerService.isEmailDuplicate("alice@uni.edu", 0);

        assertTrue(result);
    }

    @Test
    @DisplayName("isEmailDuplicate() returns true for different id with same email")
    void isEmailDuplicate_differentId_returnsTrue() {
        Lecturer existing = buildLecturer(5, "Alice", "Smith", "alice@uni.edu");
        when(lecturerDao.getAllLecturers()).thenReturn(Arrays.asList(existing));

        boolean result = lecturerService.isEmailDuplicate("alice@uni.edu", 10);

        assertTrue(result);
    }

    @Test
    @DisplayName("isEmailDuplicate() returns false when no duplicate exists")
    void isEmailDuplicate_noDuplicate_returnsFalse() {
        Lecturer existing = buildLecturer(1, "Alice", "Smith", "alice@uni.edu");
        when(lecturerDao.getAllLecturers()).thenReturn(Arrays.asList(existing));

        boolean result = lecturerService.isEmailDuplicate("bob@uni.edu", 0);

        assertFalse(result);
    }
}
