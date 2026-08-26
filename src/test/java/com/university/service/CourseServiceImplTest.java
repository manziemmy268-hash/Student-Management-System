package com.university.service;

import com.university.dao.CourseDao;
import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.Course;
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
@DisplayName("CourseServiceImpl Tests")
class CourseServiceImplTest {

    @Mock
    private CourseDao courseDao;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course buildCourse(int id, String courseName, String courseCode) {
        Course c = new Course();
        c.setCourseId(id);
        c.setCourseName(courseName);
        c.setCourseCode(courseCode);
        c.setCredits(3);
        c.setDescription("A sample course");
        c.setMaxEnrollment(60);
        return c;
    }

    @Test
    @DisplayName("getAllCourses() delegates to courseDao")
    void getAllCourses_delegatesToDao() {
        List<Course> mockList = Arrays.asList(
                buildCourse(1, "Data Structures", "CS201"),
                buildCourse(2, "Algorithms", "CS301")
        );
        when(courseDao.getAllCourses()).thenReturn(mockList);

        List<Course> result = courseService.getAllCourses();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Data Structures", result.get(0).getCourseName());
        verify(courseDao, times(1)).getAllCourses();
    }

    @Test
    @DisplayName("addCourse() returns true for a valid course")
    void addCourse_validCourse_returnsTrue() {
        Course course = buildCourse(0, "Linear Algebra", "MATH101");
        when(courseDao.getAllCourses()).thenReturn(Collections.emptyList());
        when(courseDao.addCourse(course)).thenReturn(true);

        boolean result = courseService.addCourse(course);

        assertTrue(result);
        verify(courseDao, times(1)).addCourse(course);
    }

    @Test
    @DisplayName("addCourse() throws DuplicateEntityException for duplicate course code")
    void addCourse_duplicateCode_throwsDuplicateEntityException() {
        Course existing = buildCourse(1, "Data Structures", "CS201");
        when(courseDao.getAllCourses()).thenReturn(Arrays.asList(existing));

        Course duplicate = buildCourse(0, "Advanced Data Structures", "CS201");

        DuplicateEntityException ex = assertThrows(DuplicateEntityException.class,
                () -> courseService.addCourse(duplicate));
        assertEquals("Course", ex.getEntityName());
        assertEquals("courseCode", ex.getFieldName());
    }

    @Test
    @DisplayName("addCourse() throws ValidationException for empty course name")
    void addCourse_invalidName_throwsValidationException() {
        Course course = new Course();
        course.setCourseName("");
        course.setCourseCode("CS101");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> courseService.addCourse(course));
        assertEquals("courseName", ex.getField());
    }

    @Test
    @DisplayName("addCourse() throws ValidationException for invalid course code")
    void addCourse_invalidCode_throwsValidationException() {
        Course course = new Course();
        course.setCourseName("Physics");
        course.setCourseCode("X");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> courseService.addCourse(course));
        assertEquals("courseCode", ex.getField());
    }

    @Test
    @DisplayName("searchCourses() delegates to courseDao")
    void searchCourses_delegatesToDao() {
        List<Course> mockResults = List.of(
                buildCourse(1, "Data Structures", "CS201")
        );
        when(courseDao.searchCourses("Data")).thenReturn(mockResults);

        List<Course> result = courseService.searchCourses("Data");

        assertEquals(1, result.size());
        assertEquals("Data Structures", result.get(0).getCourseName());
        verify(courseDao, times(1)).searchCourses("Data");
    }

    @Test
    @DisplayName("deleteCourse() delegates to courseDao")
    void deleteCourse_delegatesToDao() {
        when(courseDao.deleteCourse(1)).thenReturn(true);

        boolean result = courseService.deleteCourse(1);

        assertTrue(result);
        verify(courseDao, times(1)).deleteCourse(1);
    }

    @Test
    @DisplayName("getCourseCount() delegates to courseDao")
    void getCourseCount_delegatesToDao() {
        when(courseDao.getCourseCount()).thenReturn(30);

        int count = courseService.getCourseCount();

        assertEquals(30, count);
        verify(courseDao, times(1)).getCourseCount();
    }

    @Test
    @DisplayName("isCodeDuplicate() returns true when code exists with different id")
    void isCodeDuplicate_existingCode_returnsTrue() {
        Course existing = buildCourse(1, "Data Structures", "CS201");
        when(courseDao.getAllCourses()).thenReturn(Arrays.asList(existing));

        boolean result = courseService.isCodeDuplicate("CS201", 0);

        assertTrue(result);
    }

    @Test
    @DisplayName("isCodeDuplicate() returns true for different id with same code")
    void isCodeDuplicate_differentId_returnsTrue() {
        Course existing = buildCourse(5, "Data Structures", "CS201");
        when(courseDao.getAllCourses()).thenReturn(Arrays.asList(existing));

        boolean result = courseService.isCodeDuplicate("CS201", 10);

        assertTrue(result);
    }

    @Test
    @DisplayName("isCodeDuplicate() returns false when no duplicate exists")
    void isCodeDuplicate_noDuplicate_returnsFalse() {
        Course existing = buildCourse(1, "Data Structures", "CS201");
        when(courseDao.getAllCourses()).thenReturn(Arrays.asList(existing));

        boolean result = courseService.isCodeDuplicate("MATH101", 0);

        assertFalse(result);
    }
}
