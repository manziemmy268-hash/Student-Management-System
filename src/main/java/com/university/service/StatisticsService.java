package com.university.service;

import java.util.List;

public interface StatisticsService {

    int getStudentCount();

    int getLecturerCount();

    int getCourseCount();

    int getDepartmentCount();

    int getEnrollmentCount();

    List<Object[]> getStudentsPerCourse();

    List<Object[]> getStudentsPerDepartment();
}
