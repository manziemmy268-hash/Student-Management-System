package com.university.service;

import com.university.dao.DepartmentDao;
import com.university.exception.DuplicateEntityException;
import com.university.model.Department;
import com.university.utility.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentDao departmentDao;

    public DepartmentServiceImpl() {
        this.departmentDao = new DepartmentDao();
    }

    public DepartmentServiceImpl(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    @Override
    public boolean addDepartment(Department department) {
        logger.info("Adding department: {}", department.getDepartmentName());
        if (isCodeDuplicate(department.getDepartmentCode(), -1)) {
            throw new DuplicateEntityException("Department", "departmentCode", department.getDepartmentCode());
        }
        department.setDepartmentName(ValidationUtil.sanitizeInput(department.getDepartmentName()));
        department.setDepartmentCode(ValidationUtil.sanitizeInput(department.getDepartmentCode()));
        department.setLocation(ValidationUtil.sanitizeInput(department.getLocation()));
        department.setPhone(ValidationUtil.sanitizeInput(department.getPhone()));
        boolean result = departmentDao.addDepartment(department);
        logger.info("Department added: {}", result);
        return result;
    }

    @Override
    public boolean updateDepartment(Department department) {
        logger.info("Updating department id: {}", department.getDepartmentId());
        if (isCodeDuplicate(department.getDepartmentCode(), department.getDepartmentId())) {
            throw new DuplicateEntityException("Department", "departmentCode", department.getDepartmentCode());
        }
        department.setDepartmentName(ValidationUtil.sanitizeInput(department.getDepartmentName()));
        department.setDepartmentCode(ValidationUtil.sanitizeInput(department.getDepartmentCode()));
        department.setLocation(ValidationUtil.sanitizeInput(department.getLocation()));
        department.setPhone(ValidationUtil.sanitizeInput(department.getPhone()));
        boolean result = departmentDao.updateDepartment(department);
        logger.info("Department updated: {}", result);
        return result;
    }

    @Override
    public boolean isCodeDuplicate(String departmentCode, int excludeId) {
        List<Department> departments = getAllDepartments();
        for (Department d : departments) {
            if (d.getDepartmentCode().equalsIgnoreCase(departmentCode) && d.getDepartmentId() != excludeId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentDao.getAllDepartments();
    }

    @Override
    public Department getDepartmentById(int id) {
        return departmentDao.getDepartmentById(id);
    }

    @Override
    public List<Department> searchDepartments(String keyword) {
        return departmentDao.searchDepartments(keyword);
    }

    @Override
    public int getDepartmentCount() {
        return departmentDao.getDepartmentCount();
    }

    @Override
    public boolean deleteDepartment(int departmentId) {
        logger.info("Deleting department id: {}", departmentId);
        boolean result = departmentDao.deleteDepartment(departmentId);
        logger.info("Department deleted: {}", result);
        return result;
    }
}
