package com.university.service;

import com.university.dao.CourseDao;
import com.university.dao.EnrollmentDao;
import com.university.dao.StudentDao;
import com.university.exception.ValidationException;
import com.university.model.Course;
import com.university.model.Enrollment;
import com.university.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class EnrollmentServiceImpl implements EnrollmentService {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    private final EnrollmentDao enrollmentDao;
    private final StudentDao studentDao;
    private final CourseDao courseDao;

    public EnrollmentServiceImpl() {
        this.enrollmentDao = new EnrollmentDao();
        this.studentDao = new StudentDao();
        this.courseDao = new CourseDao();
    }

    public EnrollmentServiceImpl(EnrollmentDao enrollmentDao, StudentDao studentDao, CourseDao courseDao) {
        this.enrollmentDao = enrollmentDao;
        this.studentDao = studentDao;
        this.courseDao = courseDao;
    }

    @Override
    public boolean addEnrollment(Enrollment enrollment) {
        logger.info("Adding enrollment for student {} course {}", enrollment.getStudentId(), enrollment.getCourseId());
        if (isDuplicateEnrollment(enrollment.getStudentId(), enrollment.getCourseId(),
                enrollment.getSemester(), enrollment.getAcademicYear(), -1)) {
            throw new ValidationException("A student is already enrolled in this course for the specified semester and year");
        }
        boolean result = enrollmentDao.addEnrollment(enrollment);
        logger.info("Enrollment added: {}", result);
        return result;
    }

    @Override
    public boolean updateEnrollment(Enrollment enrollment) {
        logger.info("Updating enrollment id: {}", enrollment.getEnrollmentId());
        boolean result = enrollmentDao.updateEnrollment(enrollment);
        logger.info("Enrollment updated: {}", result);
        return result;
    }

    @Override
    public List<Student> getAllStudents() {
        return studentDao.getAllStudents();
    }

    @Override
    public List<Course> getAllCourses() {
        return courseDao.getAllCourses();
    }

    @Override
    public boolean isDuplicateEnrollment(int studentId, int courseId, String semester, String academicYear, int excludeId) {
        return enrollmentDao.isDuplicateEnrollment(studentId, courseId, semester, academicYear, excludeId);
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentDao.getAllEnrollments();
    }

    @Override
    public List<Enrollment> searchEnrollments(String keyword) {
        return enrollmentDao.searchEnrollments(keyword);
    }

    @Override
    public int getEnrollmentCount() {
        return enrollmentDao.getEnrollmentCount();
    }

    @Override
    public boolean deleteEnrollment(int enrollmentId) {
        logger.info("Deleting enrollment id: {}", enrollmentId);
        boolean result = enrollmentDao.deleteEnrollment(enrollmentId);
        logger.info("Enrollment deleted: {}", result);
        return result;
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        return enrollmentDao.getEnrollmentsByStudent(studentId);
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        return enrollmentDao.getEnrollmentsByCourse(courseId);
    }

    @Override
    public int getEnrollmentCountByCourse(int courseId) {
        return enrollmentDao.getEnrollmentCountByCourse(courseId);
    }

    @Override
    public Map<Integer, Integer> getEnrollmentCountsByCourse() {
        return enrollmentDao.getEnrollmentCountsByCourse();
    }
}
