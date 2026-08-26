package com.university.dao;

import com.university.database.SessionFactoryManager;
import com.university.model.Lecturer;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LecturerDao {

    private static final Logger logger = LoggerFactory.getLogger(LecturerDao.class);

    public List<Lecturer> getAllLecturers() {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Lecturer> query = session.createQuery(
                    "FROM Lecturer l LEFT JOIN FETCH l.department ORDER BY l.lecturerId", Lecturer.class);
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to retrieve all lecturers", e);
            return new ArrayList<>();
        }
    }

    public Lecturer getLecturerById(int id) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Lecturer> query = session.createQuery(
                    "FROM Lecturer l LEFT JOIN FETCH l.department WHERE l.lecturerId = :id", Lecturer.class);
            query.setParameter("id", id);
            return query.uniqueResultOptional().orElse(null);
        } catch (Exception e) {
            logger.error("Failed to retrieve lecturer by id: {}", id, e);
            return null;
        }
    }

    public List<Lecturer> searchLecturers(String keyword) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            String searchPattern = "%" + keyword + "%";
            Query<Lecturer> query = session.createQuery(
                    "FROM Lecturer l LEFT JOIN FETCH l.department " +
                    "WHERE l.firstName LIKE :kw OR l.lastName LIKE :kw OR l.email LIKE :kw " +
                    "OR CAST(l.lecturerId AS string) LIKE :kw ORDER BY l.lecturerId", Lecturer.class);
            query.setParameter("kw", searchPattern);
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to search lecturers with keyword: {}", keyword, e);
            return new ArrayList<>();
        }
    }

    public boolean addLecturer(Lecturer lecturer) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(lecturer);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to add lecturer: {} {}", lecturer.getFirstName(), lecturer.getLastName(), e);
            return false;
        }
    }

    public boolean updateLecturer(Lecturer lecturer) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(lecturer);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to update lecturer id: {}", lecturer.getLecturerId(), e);
            return false;
        }
    }

    public boolean deleteLecturer(int lecturerId) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Lecturer lecturer = session.get(Lecturer.class, lecturerId);
            if (lecturer != null) {
                session.remove(lecturer);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to delete lecturer id: {}", lecturerId, e);
            return false;
        }
    }

    public int getLecturerCount() {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(l) FROM Lecturer l", Long.class);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            logger.error("Failed to get lecturer count", e);
            return 0;
        }
    }
}
