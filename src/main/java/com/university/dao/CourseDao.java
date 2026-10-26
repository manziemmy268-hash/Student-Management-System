package com.university.dao;

import com.university.database.SessionFactoryManager;
import com.university.exception.DataAccessException;
import com.university.model.Course;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CourseDao {

    private static final Logger logger = LoggerFactory.getLogger(CourseDao.class);

    public List<Course> getAllCourses() {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Course> query = session.createQuery(
                    "FROM Course c LEFT JOIN FETCH c.department LEFT JOIN FETCH c.lecturer ORDER BY c.courseId",
                    Course.class);
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to retrieve all courses", e);
            throw new DataAccessException("Failed to retrieve all courses", e);
        }
    }

    public Course getCourseById(int id) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Course> query = session.createQuery(
                    "FROM Course c LEFT JOIN FETCH c.department LEFT JOIN FETCH c.lecturer WHERE c.courseId = :id",
                    Course.class);
            query.setParameter("id", id);
            return query.uniqueResultOptional().orElse(null);
        } catch (Exception e) {
            logger.error("Failed to retrieve course by id: {}", id, e);
            throw new DataAccessException("Failed to retrieve course by id: " + id, e);
        }
    }

    public List<Course> searchCourses(String keyword) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            String searchPattern = "%" + keyword + "%";
            Query<Course> query = session.createQuery(
                    "FROM Course c LEFT JOIN FETCH c.department LEFT JOIN FETCH c.lecturer " +
                    "WHERE c.courseName LIKE :kw OR c.courseCode LIKE :kw " +
                    "OR CAST(c.courseId AS string) LIKE :kw ORDER BY c.courseId", Course.class);
            query.setParameter("kw", searchPattern);
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to search courses with keyword: {}", keyword, e);
            throw new DataAccessException("Failed to search courses", e);
        }
    }

    public boolean addCourse(Course course) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(course);
            tx.commit();
            return true;
        } catch (Exception e) {
            try {
                if (tx != null) tx.rollback();
            } catch (Exception rollbackEx) {
                logger.warn("Transaction rollback failed", rollbackEx);
            }
            logger.error("Failed to add course: {}", course.getCourseName(), e);
            throw new DataAccessException("Failed to add course: " + course.getCourseName(), e);
        }
    }

    public boolean updateCourse(Course course) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(course);
            tx.commit();
            return true;
        } catch (Exception e) {
            try {
                if (tx != null) tx.rollback();
            } catch (Exception rollbackEx) {
                logger.warn("Transaction rollback failed", rollbackEx);
            }
            logger.error("Failed to update course id: {}", course.getCourseId(), e);
            throw new DataAccessException("Failed to update course id: " + course.getCourseId(), e);
        }
    }

    public boolean deleteCourse(int courseId) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Course course = session.get(Course.class, courseId);
            if (course != null) {
                session.remove(course);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } catch (Exception e) {
            try {
                if (tx != null) tx.rollback();
            } catch (Exception rollbackEx) {
                logger.warn("Transaction rollback failed", rollbackEx);
            }
            logger.error("Failed to delete course id: {}", courseId, e);
            throw new DataAccessException("Failed to delete course id: " + courseId, e);
        }
    }

    public int getCourseCount() {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(c) FROM Course c", Long.class);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            logger.error("Failed to get course count", e);
            throw new DataAccessException("Failed to get course count", e);
        }
    }

    public boolean isCodeDuplicate(String courseCode, int excludeId) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(c) FROM Course c WHERE LOWER(c.courseCode) = LOWER(:code) AND c.courseId != :id",
                    Long.class);
            query.setParameter("code", courseCode);
            query.setParameter("id", excludeId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            logger.error("Failed to check course code duplicate: {}", courseCode, e);
            throw new DataAccessException("Failed to check course code duplicate", e);
        }
    }

    public List<Course> getCoursesByLecturer(int lecturerId) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Course> query = session.createQuery(
                    "FROM Course c LEFT JOIN FETCH c.department LEFT JOIN FETCH c.lecturer " +
                    "WHERE c.lecturerId = :lid ORDER BY c.courseId", Course.class);
            query.setParameter("lid", lecturerId);
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to retrieve courses for lecturer: {}", lecturerId, e);
            throw new DataAccessException("Failed to retrieve courses for lecturer: " + lecturerId, e);
        }
    }

    public List<Course> getAvailableCourses() {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Course> query = session.createQuery(
                "FROM Course c LEFT JOIN FETCH c.department LEFT JOIN FETCH c.lecturer " +
                "WHERE (SELECT COUNT(e) FROM Enrollment e WHERE e.courseId = c.courseId " +
                "AND e.status = 'Enrolled') < c.maxEnrollment ORDER BY c.courseId", Course.class);
            List<Course> courses = new ArrayList<>(query.getResultList());
            return courses;
        } catch (Exception e) {
            logger.error("Failed to retrieve available courses", e);
            throw new DataAccessException("Failed to retrieve available courses", e);
        }
    }
}