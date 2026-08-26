package com.university.service;

import com.university.dao.CourseDao;
import com.university.dao.DepartmentDao;
import com.university.dao.EnrollmentDao;
import com.university.dao.LecturerDao;
import com.university.dao.StudentDao;
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
@DisplayName("StatisticsServiceImpl Tests")
class StatisticsServiceImplTest {

    @Mock
    private StudentDao studentDao;

    @Mock
    private LecturerDao lecturerDao;

    @Mock
    private CourseDao courseDao;

    @Mock
    private DepartmentDao departmentDao;

    @Mock
    private EnrollmentDao enrollmentDao;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Test
    @DisplayName("getStudentCount() delegates to studentDao")
    void getStudentCount_delegatesToDao() {
        when(studentDao.getStudentCount()).thenReturn(150);

        int count = statisticsService.getStudentCount();

        assertEquals(150, count);
        verify(studentDao, times(1)).getStudentCount();
    }

    @Test
    @DisplayName("getLecturerCount() delegates to lecturerDao")
    void getLecturerCount_delegatesToDao() {
        when(lecturerDao.getLecturerCount()).thenReturn(45);

        int count = statisticsService.getLecturerCount();

        assertEquals(45, count);
        verify(lecturerDao, times(1)).getLecturerCount();
    }

    @Test
    @DisplayName("getCourseCount() delegates to courseDao")
    void getCourseCount_delegatesToDao() {
        when(courseDao.getCourseCount()).thenReturn(60);

        int count = statisticsService.getCourseCount();

        assertEquals(60, count);
        verify(courseDao, times(1)).getCourseCount();
    }

    @Test
    @DisplayName("getDepartmentCount() delegates to departmentDao")
    void getDepartmentCount_delegatesToDao() {
        when(departmentDao.getDepartmentCount()).thenReturn(8);

        int count = statisticsService.getDepartmentCount();

        assertEquals(8, count);
        verify(departmentDao, times(1)).getDepartmentCount();
    }

    @Test
    @DisplayName("getEnrollmentCount() delegates to enrollmentDao")
    void getEnrollmentCount_delegatesToDao() {
        when(enrollmentDao.getEnrollmentCount()).thenReturn(300);

        int count = statisticsService.getEnrollmentCount();

        assertEquals(300, count);
        verify(enrollmentDao, times(1)).getEnrollmentCount();
    }

    @Test
    @DisplayName("getStudentsPerCourse() delegates to enrollmentDao")
    void getStudentsPerCourse_delegatesToDao() {
        List<Object[]> mockData = Arrays.asList(
                new Object[]{"CS201", "Data Structures", 45},
                new Object[]{"CS301", "Algorithms", 30}
        );
        when(enrollmentDao.getStudentsPerCourse()).thenReturn(mockData);

        List<Object[]> result = statisticsService.getStudentsPerCourse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("CS201", result.get(0)[0]);
        verify(enrollmentDao, times(1)).getStudentsPerCourse();
    }

    @Test
    @DisplayName("getStudentsPerDepartment() delegates to enrollmentDao")
    void getStudentsPerDepartment_delegatesToDao() {
        List<Object[]> mockData = Arrays.asList(
                new Object[]{"CS", "Computer Science", 120},
                new Object[]{"MATH", "Mathematics", 80}
        );
        when(enrollmentDao.getStudentsPerDepartment()).thenReturn(mockData);

        List<Object[]> result = statisticsService.getStudentsPerDepartment();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("CS", result.get(0)[0]);
        verify(enrollmentDao, times(1)).getStudentsPerDepartment();
    }

    @Test
    @DisplayName("getStudentsPerCourse() returns empty list when no data")
    void getStudentsPerCourse_empty_returnsEmptyList() {
        when(enrollmentDao.getStudentsPerCourse()).thenReturn(Collections.emptyList());

        List<Object[]> result = statisticsService.getStudentsPerCourse();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
