package com.university.service;

import com.university.dao.CourseDao;
import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.Course;
import com.university.utility.ValidationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CourseServiceImpl implements CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
    private final CourseDao courseDao;

    public CourseServiceImpl() {
        this.courseDao = new CourseDao();
    }

    public CourseServiceImpl(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    @Override
    public boolean addCourse(Course course) {
        logger.info("Adding course: {}", course.getCourseName());

        if (!ValidationUtil.isValidName(course.getCourseName())) {
            throw new ValidationException("courseName", "Course name must be at least 2 characters");
        }
        if (!ValidationUtil.isValidCode(course.getCourseCode())) {
            throw new ValidationException("courseCode", "Course code must be between 2 and 20 characters");
        }
        if (isCodeDuplicate(course.getCourseCode(), 0)) {
            throw new DuplicateEntityException("Course", "courseCode", course.getCourseCode());
        }

        course.setCourseName(ValidationUtil.sanitizeInput(course.getCourseName()));
        course.setCourseCode(ValidationUtil.sanitizeInput(course.getCourseCode()));
        course.setDescription(ValidationUtil.sanitizeInput(course.getDescription()));

        boolean result = courseDao.addCourse(course);
        if (result) {
            logger.info("Course added successfully: {}", course.getCourseName());
        } else {
            logger.error("Failed to add course: {}", course.getCourseName());
        }
        return result;
    }

    @Override
    public boolean updateCourse(Course course) {
        logger.info("Updating course id: {}", course.getCourseId());

        if (!ValidationUtil.isValidName(course.getCourseName())) {
            throw new ValidationException("courseName", "Course name must be at least 2 characters");
        }
        if (!ValidationUtil.isValidCode(course.getCourseCode())) {
            throw new ValidationException("courseCode", "Course code must be between 2 and 20 characters");
        }
        if (isCodeDuplicate(course.getCourseCode(), course.getCourseId())) {
            throw new DuplicateEntityException("Course", "courseCode", course.getCourseCode());
        }

        course.setCourseName(ValidationUtil.sanitizeInput(course.getCourseName()));
        course.setCourseCode(ValidationUtil.sanitizeInput(course.getCourseCode()));
        course.setDescription(ValidationUtil.sanitizeInput(course.getDescription()));

        boolean result = courseDao.updateCourse(course);
        if (result) {
            logger.info("Course updated successfully id: {}", course.getCourseId());
        } else {
            logger.error("Failed to update course id: {}", course.getCourseId());
        }
        return result;
    }

    @Override
    public boolean deleteCourse(int courseId) {
        logger.info("Deleting course id: {}", courseId);
        boolean result = courseDao.deleteCourse(courseId);
        if (result) {
            logger.info("Course deleted successfully id: {}", courseId);
        } else {
            logger.error("Failed to delete course id: {}", courseId);
        }
        return result;
    }

    @Override
    public List<Course> getAllCourses() {
        return courseDao.getAllCourses();
    }

    @Override
    public Course getCourseById(int id) {
        return courseDao.getCourseById(id);
    }

    @Override
    public List<Course> searchCourses(String keyword) {
        return courseDao.searchCourses(keyword);
    }

    @Override
    public int getCourseCount() {
        return courseDao.getCourseCount();
    }

    @Override
    public List<Course> getCoursesByLecturer(int lecturerId) {
        return courseDao.getCoursesByLecturer(lecturerId);
    }

    @Override
    public List<Course> getAvailableCourses() {
        return courseDao.getAvailableCourses();
    }

    @Override
    public boolean isCodeDuplicate(String courseCode, int excludeId) {
        List<Course> courses = getAllCourses();
        return courses.stream()
                .anyMatch(c -> c.getCourseCode().equalsIgnoreCase(courseCode) && c.getCourseId() != excludeId);
    }
}
