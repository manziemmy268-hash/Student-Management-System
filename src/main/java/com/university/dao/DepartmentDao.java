package com.university.dao;

import com.university.database.SessionFactoryManager;
import com.university.model.Department;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DepartmentDao {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentDao.class);

    public List<Department> getAllDepartments() {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Department> query = session.createQuery("FROM Department ORDER BY departmentId", Department.class);
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to retrieve all departments", e);
            return new ArrayList<>();
        }
    }

    public Department getDepartmentById(int id) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            return session.get(Department.class, id);
        } catch (Exception e) {
            logger.error("Failed to retrieve department by id: {}", id, e);
            return null;
        }
    }

    public boolean addDepartment(Department dept) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(dept);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to add department: {}", dept.getDepartmentName(), e);
            return false;
        }
    }

    public boolean updateDepartment(Department dept) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(dept);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to update department id: {}", dept.getDepartmentId(), e);
            return false;
        }
    }

    public boolean deleteDepartment(int departmentId) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Department dept = session.get(Department.class, departmentId);
            if (dept != null) {
                session.remove(dept);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to delete department id: {}", departmentId, e);
            return false;
        }
    }

    public int getDepartmentCount() {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(d) FROM Department d", Long.class);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            logger.error("Failed to get department count", e);
            return 0;
        }
    }

    public List<Department> searchDepartments(String keyword) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Department> query = session.createQuery(
                    "FROM Department d WHERE d.departmentName LIKE :kw OR d.departmentCode LIKE :kw " +
                    "OR CAST(d.departmentId AS string) LIKE :kw ORDER BY d.departmentId",
                    Department.class);
            query.setParameter("kw", "%" + keyword + "%");
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to search departments with keyword: {}", keyword, e);
            return new ArrayList<>();
        }
    }
}
