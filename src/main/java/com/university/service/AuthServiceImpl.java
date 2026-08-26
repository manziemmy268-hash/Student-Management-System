package com.university.service;

import com.university.dao.UserDao;
import com.university.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserDao userDao = new UserDao();

    @Override
    public User authenticate(String username, String password) {
        try {
            User user = userDao.authenticate(username, password);
            if (user != null) {
                logger.info("User '{}' logged in with role '{}'.", username, user.getRole());
            } else {
                logger.warn("Failed login attempt for '{}'.", username);
            }
            return user;
        } catch (Exception e) {
            logger.error("Authentication error for '{}'", username, e);
            return null;
        }
    }

    @Override
    public String getRoleName(User user) {
        if (user == null) return "Unknown";
        return user.getRole();
    }
}
