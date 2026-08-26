package com.university.dao;

import com.university.database.SessionFactoryManager;
import com.university.model.User;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public User authenticate(String username, String password) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Query<User> userQuery = session.createQuery(
                        "FROM User u WHERE u.username = :uname", User.class);
                userQuery.setParameter("uname", username);
                User user = userQuery.uniqueResultOptional().orElse(null);

                if (user != null) {
                    NativeQuery<?> pwdQuery = session.createNativeQuery(
                            "SELECT password FROM users WHERE user_id = :id");
                    pwdQuery.setParameter("id", user.getUserId());
                    String storedHash = (String) pwdQuery.uniqueResult();
                    if (storedHash != null && checkPassword(password, storedHash)) {
                        session.createNativeQuery("UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = :id")
                                .setParameter("id", user.getUserId())
                                .executeUpdate();
                        tx.commit();
                        return user;
                    }
                }

                NativeQuery<?> studentQuery = session.createNativeQuery(
                        "SELECT student_id, email, first_name, last_name, password FROM students WHERE email = :email");
                studentQuery.setParameter("email", username);
                Object[] studentRow = (Object[]) studentQuery.uniqueResult();

                if (studentRow != null) {
                    String storedHash = (String) studentRow[4];
                    if (storedHash != null && checkPassword(password, storedHash)) {
                        User studentUser = new User();
                        studentUser.setUserId(((Number) studentRow[0]).intValue());
                        studentUser.setUsername((String) studentRow[1]);
                        studentUser.setFullName(((String) studentRow[2]) + " " + studentRow[3]);
                        studentUser.setRole("Student");
                        studentUser.setStudentId(((Number) studentRow[0]).intValue());
                        tx.commit();
                        return studentUser;
                    }
                }

                NativeQuery<?> lecturerQuery = session.createNativeQuery(
                        "SELECT lecturer_id, email, first_name, last_name, password FROM lecturers WHERE email = :email");
                lecturerQuery.setParameter("email", username);
                Object[] lecturerRow = (Object[]) lecturerQuery.uniqueResult();

                if (lecturerRow != null) {
                    String storedHash = (String) lecturerRow[4];
                    if (storedHash != null && checkPassword(password, storedHash)) {
                        User lecturerUser = new User();
                        lecturerUser.setUserId(((Number) lecturerRow[0]).intValue());
                        lecturerUser.setUsername((String) lecturerRow[1]);
                        lecturerUser.setFullName(((String) lecturerRow[2]) + " " + lecturerRow[3]);
                        lecturerUser.setRole("Lecturer");
                        lecturerUser.setLecturerId(((Number) lecturerRow[0]).intValue());
                        tx.commit();
                        return lecturerUser;
                    }
                }

                tx.commit();
                return null;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        } catch (Exception e) {
            logger.error("Authentication query failed for user: {}", username, e);
            return null;
        }
    }

    public List<User> getAllUsers() {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User ORDER BY userId", User.class);
            return new ArrayList<>(query.list());
        } catch (Exception e) {
            logger.error("Failed to retrieve all users", e);
            return new ArrayList<>();
        }
    }

    public User getUserById(int id) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        } catch (Exception e) {
            logger.error("Failed to retrieve user by id: {}", id, e);
            return null;
        }
    }

    public boolean addUser(User user, String plainPassword) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
            NativeQuery<?> query = session.createNativeQuery(
                    "INSERT INTO users (username, password, full_name, role) VALUES (:uname, :pwd, :fname, :role)");
            query.setParameter("uname", user.getUsername())
                 .setParameter("pwd", hash)
                 .setParameter("fname", user.getFullName())
                 .setParameter("role", user.getRole());
            query.executeUpdate();
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to add user: {}", user.getUsername(), e);
            return false;
        }
    }

    public boolean updateUser(User user) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to update user id: {}", user.getUserId(), e);
            return false;
        }
    }

    public boolean updatePassword(int userId, String newPlainPassword) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            String hash = BCrypt.hashpw(newPlainPassword, BCrypt.gensalt());
            session.createNativeQuery("UPDATE users SET password=:pwd WHERE user_id=:id")
                    .setParameter("pwd", hash)
                    .setParameter("id", userId)
                    .executeUpdate();
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to update password for user id: {}", userId, e);
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                session.remove(user);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Failed to delete user id: {}", userId, e);
            return false;
        }
    }

    public int getUserCount() {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(u) FROM User u", Long.class);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            logger.error("Failed to get user count", e);
            return 0;
        }
    }

    public boolean isUsernameDuplicate(String username, int excludeId) {
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.username = :uname AND u.userId != :id", Long.class);
            query.setParameter("uname", username);
            query.setParameter("id", excludeId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            logger.error("Failed to check username duplicate", e);
            return false;
        }
    }

    private boolean checkPassword(String plain, String hash) {
        try {
            return BCrypt.checkpw(plain, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
