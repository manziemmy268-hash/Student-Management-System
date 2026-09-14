package com.university.service;

import com.university.dao.UserDao;
import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.User;
import com.university.utility.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDao();
    }

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Override
    public boolean addUser(User user, String plainPassword) {
        logger.info("Adding user: {}", user.getUsername());

        if (!ValidationUtil.isValidName(user.getUsername())) {
            throw new ValidationException("username", "Username must be at least 2 characters");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new ValidationException("fullName", "Full name is required");
        }
        if (plainPassword == null || plainPassword.length() < 4) {
            throw new ValidationException("password", "Password must be at least 4 characters");
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new ValidationException("role", "Role is required");
        }
        if (isUsernameDuplicate(user.getUsername(), 0)) {
            throw new DuplicateEntityException("User", "username", user.getUsername());
        }

        boolean result = userDao.addUser(user, plainPassword);
        if (result) {
            logger.info("User added successfully: {}", user.getUsername());
        } else {
            logger.error("Failed to add user: {}", user.getUsername());
        }
        return result;
    }

    @Override
    public boolean updateUser(User user) {
        logger.info("Updating user id: {}", user.getUserId());

        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new ValidationException("fullName", "Full name is required");
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new ValidationException("role", "Role is required");
        }
        if (isUsernameDuplicate(user.getUsername(), user.getUserId())) {
            throw new DuplicateEntityException("User", "username", user.getUsername());
        }

        boolean result = userDao.updateUser(user);
        if (result) {
            logger.info("User updated successfully id: {}", user.getUserId());
        } else {
            logger.error("Failed to update user id: {}", user.getUserId());
        }
        return result;
    }

    @Override
    public boolean updatePassword(int userId, String newPlainPassword) {
        logger.info("Updating password for user id: {}", userId);
        if (newPlainPassword == null || newPlainPassword.length() < 4) {
            throw new ValidationException("password", "Password must be at least 4 characters");
        }
        boolean result = userDao.updatePassword(userId, newPlainPassword);
        if (result) {
            logger.info("Password updated successfully for user id: {}", userId);
        } else {
            logger.error("Failed to update password for user id: {}", userId);
        }
        return result;
    }

    @Override
    public boolean deleteUser(int userId) {
        User user = userDao.getUserById(userId);
        if (user != null && "Admin".equals(user.getRole())) {
            int adminCount = userDao.getRoleCount("Admin");
            if (adminCount <= 1) {
                logger.warn("Blocked deletion of the last admin account id: {}", userId);
                throw new ValidationException("Cannot delete the last admin account. Promote another user to Admin first.");
            }
        }
        logger.info("Deleting user id: {}", userId);
        boolean result = userDao.deleteUser(userId);
        if (result) {
            logger.info("User deleted successfully id: {}", userId);
        } else {
            logger.error("Failed to delete user id: {}", userId);
        }
        return result;
    }

    @Override
    public int getUserCount() {
        return userDao.getUserCount();
    }

    @Override
    public boolean isUsernameDuplicate(String username, int excludeId) {
        return userDao.isUsernameDuplicate(username, excludeId);
    }
}
