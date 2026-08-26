package com.university.service;

import com.university.model.Department;
import java.util.List;

public interface DepartmentService {

    List<Department> getAllDepartments();

    Department getDepartmentById(int id);

    List<Department> searchDepartments(String keyword);

    boolean addDepartment(Department department);

    boolean updateDepartment(Department department);

    boolean deleteDepartment(int departmentId);

    int getDepartmentCount();

    boolean isCodeDuplicate(String departmentCode, int excludeId);
}
