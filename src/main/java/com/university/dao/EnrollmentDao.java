package com.university.dao;

import com.university.database.SessionFactoryManager;
import com.university.model.Enrollment;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentDao {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentDao.class);

    public List<Enrollment> getAllEnrollments() {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Enrollment> query = session.createQuery(
                    "FROM Enrollment e LEFT JOIN FETCH e.student LEFT JOIN FETCH e.course ORDER BY e.enrollmentId",
                    Enrollment.class);
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to retrieve all enrollments", e);
            return new ArrayList<>();
        }
    }

    public boolean addEnrollment(Enrollment enrollment) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(enrollment);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to add enrollment for student {} course {}",
                    enrollment.getStudentId(), enrollment.getCourseId(), e);
            return false;
        }
    }

    public boolean updateEnrollment(Enrollment enrollment) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(enrollment);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to update enrollment id: {}", enrollment.getEnrollmentId(), e);
            return false;
        }
    }

    public boolean deleteEnrollment(int enrollmentId) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Enrollment enrollment = session.get(Enrollment.class, enrollmentId);
            if (enrollment != null) {
                session.remove(enrollment);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to delete enrollment id: {}", enrollmentId, e);
            return false;
        }
    }

    public List<Enrollment> searchEnrollments(String keyword) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            String pattern = "%" + keyword + "%";
            Query<Enrollment> query = session.createQuery(
                    "FROM Enrollment e LEFT JOIN FETCH e.student LEFT JOIN FETCH e.course " +
                    "WHERE e.student.firstName LIKE :kw OR e.student.lastName LIKE :kw " +
                    "OR e.course.courseName LIKE :kw OR e.course.courseCode LIKE :kw " +
                    "ORDER BY e.enrollmentId", Enrollment.class);
            query.setParameter("kw", pattern);
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to search enrollments with keyword: {}", keyword, e);
            return new ArrayList<>();
        }
    }

    public int getEnrollmentCount() {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(e) FROM Enrollment e", Long.class);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            logger.error("Failed to get enrollment count", e);
            return 0;
        }
    }

    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Enrollment> query = session.createQuery(
                    "FROM Enrollment e LEFT JOIN FETCH e.student LEFT JOIN FETCH e.course " +
                    "WHERE e.studentId = :sid ORDER BY e.enrollmentId", Enrollment.class);
            query.setParameter("sid", studentId);
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to retrieve enrollments for student: {}", studentId, e);
            return new ArrayList<>();
        }
    }

    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Enrollment> query = session.createQuery(
                    "FROM Enrollment e LEFT JOIN FETCH e.student LEFT JOIN FETCH e.course " +
                    "WHERE e.courseId = :cid ORDER BY e.enrollmentId", Enrollment.class);
            query.setParameter("cid", courseId);
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to retrieve enrollments for course: {}", courseId, e);
            return new ArrayList<>();
        }
    }

    public int getEnrollmentCountByCourse(int courseId) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(e) FROM Enrollment e WHERE e.courseId = :cid AND e.status = 'Enrolled'", Long.class);
            query.setParameter("cid", courseId);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            logger.error("Failed to get enrollment count for course: {}", courseId, e);
            return 0;
        }
    }

    public List<Object[]> getStudentsPerCourse() {
        String sql = "SELECT c.course_code, c.course_name, COUNT(e.student_id) AS student_count " +
                "FROM courses c LEFT JOIN enrollments e ON c.course_id = e.course_id " +
                "GROUP BY c.course_id, c.course_code, c.course_name ORDER BY student_count DESC";
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
            return new ArrayList<>(query.getResultList());
        } catch (Exception e) {
            logger.error("Failed to get students per course", e);
            return new ArrayList<>();
        }
    }

    public List<Object[]> getStudentsPerDepartment() {
        String sql = "SELECT d.department_code, d.department_name, COUNT(s.student_id) AS student_count " +
                "FROM departments d LEFT JOIN students s ON d.department_id = s.department_id " +
                "GROUP BY d.department_id, d.department_code, d.department_name ORDER BY student_count DESC";
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            NativeQuery<Object[]> query = session.createNativeQuery(sql, Object[].class);
            return new ArrayList<>(query.getResultList());
        } catch (Exception e) {
            logger.error("Failed to get students per department", e);
            return new ArrayList<>();
        }
    }
}
