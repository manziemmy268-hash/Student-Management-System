package com.university.service;

import com.university.model.Course;
import java.util.List;

public interface CourseService {

    List<Course> getAllCourses();

    Course getCourseById(int id);

    List<Course> searchCourses(String keyword);

    List<Course> getCoursesByLecturer(int lecturerId);

    List<Course> getAvailableCourses();

    boolean addCourse(Course course);

    boolean updateCourse(Course course);

    boolean deleteCourse(int courseId);

    int getCourseCount();

    boolean isCodeDuplicate(String courseCode, int excludeId);
}
