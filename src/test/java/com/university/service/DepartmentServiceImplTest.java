package com.university.service;

import com.university.dao.DepartmentDao;
import com.university.exception.DuplicateEntityException;
import com.university.model.Department;
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
@DisplayName("DepartmentServiceImpl Tests")
class DepartmentServiceImplTest {

    @Mock
    private DepartmentDao departmentDao;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department buildDepartment(int id, String name, String code) {
        Department d = new Department();
        d.setDepartmentId(id);
        d.setDepartmentName(name);
        d.setDepartmentCode(code);
        d.setLocation("Building A");
        d.setPhone("+1234567890");
        return d;
    }

    @Test
    @DisplayName("getAllDepartments() delegates to departmentDao")
    void getAllDepartments_delegatesToDao() {
        List<Department> mockList = Arrays.asList(
                buildDepartment(1, "Computer Science", "CS"),
                buildDepartment(2, "Mathematics", "MATH")
        );
        when(departmentDao.getAllDepartments()).thenReturn(mockList);

        List<Department> result = departmentService.getAllDepartments();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Computer Science", result.get(0).getDepartmentName());
        verify(departmentDao, times(1)).getAllDepartments();
    }

    @Test
    @DisplayName("addDepartment() returns true for a valid department")
    void addDepartment_validDepartment_returnsTrue() {
        Department department = buildDepartment(0, "Physics", "PHYS");
        when(departmentDao.isCodeDuplicate("PHYS", -1)).thenReturn(false);
        when(departmentDao.addDepartment(department)).thenReturn(true);

        boolean result = departmentService.addDepartment(department);

        assertTrue(result);
        verify(departmentDao, times(1)).addDepartment(department);
    }

    @Test
    @DisplayName("addDepartment() throws DuplicateEntityException for duplicate code")
    void addDepartment_duplicateCode_throwsDuplicateEntityException() {
        when(departmentDao.isCodeDuplicate("CS", -1)).thenReturn(true);

        Department duplicate = buildDepartment(0, "Cyber Security", "CS");

        DuplicateEntityException ex = assertThrows(DuplicateEntityException.class,
                () -> departmentService.addDepartment(duplicate));
        assertEquals("Department", ex.getEntityName());
        assertEquals("departmentCode", ex.getFieldName());
    }

    @Test
    @DisplayName("addDepartment() sanitizes input fields")
    void addDepartment_withHtmlInputs_sanitizesFields() {
        Department department = buildDepartment(0, "Physics", "PHYS");
        department.setLocation("Building <script>alert('xss')</script>");
        department.setPhone("+1234567890");
        when(departmentDao.isCodeDuplicate("PHYS", -1)).thenReturn(false);
        when(departmentDao.addDepartment(any(Department.class))).thenReturn(true);

        departmentService.addDepartment(department);

        verify(departmentDao, times(1)).addDepartment(department);
        assertFalse(department.getLocation().contains("<script>"));
    }

    @Test
    @DisplayName("updateDepartment() returns true for a valid update")
    void updateDepartment_validDepartment_returnsTrue() {
        Department department = buildDepartment(1, "Computer Science", "CS");
        when(departmentDao.isCodeDuplicate("CS", 1)).thenReturn(false);
        when(departmentDao.updateDepartment(department)).thenReturn(true);

        boolean result = departmentService.updateDepartment(department);

        assertTrue(result);
        verify(departmentDao, times(1)).updateDepartment(department);
    }

    @Test
    @DisplayName("updateDepartment() throws DuplicateEntityException for duplicate code with different id")
    void updateDepartment_duplicateCode_throwsDuplicateEntityException() {
        when(departmentDao.isCodeDuplicate("CS", 2)).thenReturn(true);

        Department duplicate = buildDepartment(2, "Cyber Security", "CS");

        DuplicateEntityException ex = assertThrows(DuplicateEntityException.class,
                () -> departmentService.updateDepartment(duplicate));
        assertEquals("Department", ex.getEntityName());
        assertEquals("departmentCode", ex.getFieldName());
    }

    @Test
    @DisplayName("updateDepartment() allows same code for same id")
    void updateDepartment_sameIdSameCode_doesNotThrow() {
        Department existing = buildDepartment(1, "Computer Science", "CS");
        when(departmentDao.isCodeDuplicate("CS", 1)).thenReturn(false);
        when(departmentDao.updateDepartment(existing)).thenReturn(true);

        boolean result = departmentService.updateDepartment(existing);

        assertTrue(result);
        verify(departmentDao, times(1)).updateDepartment(existing);
    }

    @Test
    @DisplayName("deleteDepartment() delegates to departmentDao")
    void deleteDepartment_delegatesToDao() {
        when(departmentDao.deleteDepartment(1)).thenReturn(true);

        boolean result = departmentService.deleteDepartment(1);

        assertTrue(result);
        verify(departmentDao, times(1)).deleteDepartment(1);
    }

    @Test
    @DisplayName("getDepartmentById() delegates to departmentDao")
    void getDepartmentById_delegatesToDao() {
        Department department = buildDepartment(1, "Computer Science", "CS");
        when(departmentDao.getDepartmentById(1)).thenReturn(department);

        Department result = departmentService.getDepartmentById(1);

        assertNotNull(result);
        assertEquals("Computer Science", result.getDepartmentName());
        verify(departmentDao, times(1)).getDepartmentById(1);
    }

    @Test
    @DisplayName("searchDepartments() delegates to departmentDao")
    void searchDepartments_delegatesToDao() {
        List<Department> mockResults = List.of(
                buildDepartment(1, "Computer Science", "CS")
        );
        when(departmentDao.searchDepartments("Computer")).thenReturn(mockResults);

        List<Department> result = departmentService.searchDepartments("Computer");

        assertEquals(1, result.size());
        assertEquals("Computer Science", result.get(0).getDepartmentName());
        verify(departmentDao, times(1)).searchDepartments("Computer");
    }

    @Test
    @DisplayName("getDepartmentCount() delegates to departmentDao")
    void getDepartmentCount_delegatesToDao() {
        when(departmentDao.getDepartmentCount()).thenReturn(10);

        int count = departmentService.getDepartmentCount();

        assertEquals(10, count);
        verify(departmentDao, times(1)).getDepartmentCount();
    }

    @Test
    @DisplayName("isCodeDuplicate() returns true when code exists with different id")
    void isCodeDuplicate_existingCode_returnsTrue() {
        when(departmentDao.isCodeDuplicate("CS", 0)).thenReturn(true);

        boolean result = departmentService.isCodeDuplicate("CS", 0);

        assertTrue(result);
    }

    @Test
    @DisplayName("isCodeDuplicate() returns false for same id with same code")
    void isCodeDuplicate_sameId_returnsFalse() {
        when(departmentDao.isCodeDuplicate("CS", 1)).thenReturn(false);

        boolean result = departmentService.isCodeDuplicate("CS", 1);

        assertFalse(result);
    }

    @Test
    @DisplayName("isCodeDuplicate() returns false when no duplicate exists")
    void isCodeDuplicate_noDuplicate_returnsFalse() {
        when(departmentDao.isCodeDuplicate("MATH", 0)).thenReturn(false);

        boolean result = departmentService.isCodeDuplicate("MATH", 0);

        assertFalse(result);
    }
}
