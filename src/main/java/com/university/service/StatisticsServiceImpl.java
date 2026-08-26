package com.university.service;

import com.university.dao.CourseDao;
import com.university.dao.DepartmentDao;
import com.university.dao.EnrollmentDao;
import com.university.dao.LecturerDao;
import com.university.dao.StudentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StatisticsServiceImpl implements StatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    private final StudentDao studentDao;
    private final LecturerDao lecturerDao;
    private final CourseDao courseDao;
    private final DepartmentDao departmentDao;
    private final EnrollmentDao enrollmentDao;

    public StatisticsServiceImpl() {
        this.studentDao = new StudentDao();
        this.lecturerDao = new LecturerDao();
        this.courseDao = new CourseDao();
        this.departmentDao = new DepartmentDao();
        this.enrollmentDao = new EnrollmentDao();
    }

    public StatisticsServiceImpl(StudentDao studentDao, LecturerDao lecturerDao, CourseDao courseDao,
                                 DepartmentDao departmentDao, EnrollmentDao enrollmentDao) {
        this.studentDao = studentDao;
        this.lecturerDao = lecturerDao;
        this.courseDao = courseDao;
        this.departmentDao = departmentDao;
        this.enrollmentDao = enrollmentDao;
    }

    @Override
    public int getStudentCount() {
        logger.info("Fetching student count");
        return studentDao.getStudentCount();
    }

    @Override
    public int getLecturerCount() {
        logger.info("Fetching lecturer count");
        return lecturerDao.getLecturerCount();
    }

    @Override
    public int getCourseCount() {
        logger.info("Fetching course count");
        return courseDao.getCourseCount();
    }

    @Override
    public int getDepartmentCount() {
        logger.info("Fetching department count");
        return departmentDao.getDepartmentCount();
    }

    @Override
    public int getEnrollmentCount() {
        logger.info("Fetching enrollment count");
        return enrollmentDao.getEnrollmentCount();
    }

    @Override
    public List<Object[]> getStudentsPerCourse() {
        logger.info("Fetching students per course");
        return enrollmentDao.getStudentsPerCourse();
    }

    @Override
    public List<Object[]> getStudentsPerDepartment() {
        logger.info("Fetching students per department");
        return enrollmentDao.getStudentsPerDepartment();
    }
}
