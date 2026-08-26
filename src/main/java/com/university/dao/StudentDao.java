package com.university.dao;

import com.university.database.SessionFactoryManager;
import com.university.model.Student;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StudentDao {

    private static final Logger logger = LoggerFactory.getLogger(StudentDao.class);

    public List<Student> getAllStudents() {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Student> query = session.createQuery(
                    "FROM Student s LEFT JOIN FETCH s.department ORDER BY s.studentId", Student.class);
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to retrieve all students", e);
            return new ArrayList<>();
        }
    }

    public Student getStudentById(int id) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Student> query = session.createQuery(
                    "FROM Student s LEFT JOIN FETCH s.department WHERE s.studentId = :id", Student.class);
            query.setParameter("id", id);
            return query.uniqueResultOptional().orElse(null);
        } catch (Exception e) {
            logger.error("Failed to retrieve student by id: {}", id, e);
            return null;
        }
    }

    public List<Student> searchStudents(String keyword) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            String searchPattern = "%" + keyword + "%";
            Query<Student> query = session.createQuery(
                    "FROM Student s LEFT JOIN FETCH s.department " +
                    "WHERE s.firstName LIKE :kw OR s.lastName LIKE :kw OR s.email LIKE :kw " +
                    "OR CAST(s.studentId AS string) LIKE :kw ORDER BY s.studentId", Student.class);
            query.setParameter("kw", searchPattern);
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to search students with keyword: {}", keyword, e);
            return new ArrayList<>();
        }
    }

    public boolean addStudent(Student student) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(student);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to add student: {} {}", student.getFirstName(), student.getLastName(), e);
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(student);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to update student id: {}", student.getStudentId(), e);
            return false;
        }
    }

    public boolean deleteStudent(int studentId) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Student student = session.get(Student.class, studentId);
            if (student != null) {
                session.remove(student);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to delete student id: {}", studentId, e);
            return false;
        }
    }

    public int getStudentCount() {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(s) FROM Student s", Long.class);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            logger.error("Failed to get student count", e);
            return 0;
        }
    }
}
