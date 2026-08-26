package com.university.controller;

import com.university.model.User;
import com.university.service.AuthService;
import com.university.service.AuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final AuthService authService;
    private User currentUser;

    public LoginController() {
        authService = new AuthServiceImpl();
    }

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    public boolean authenticate(String username, String password) {
        try {
            currentUser = authService.authenticate(username, password);
        } catch (Exception e) {
            logger.error("Authentication error for '{}'", username, e);
            currentUser = null;
        }
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getRoleName() {
        return authService.getRoleName(currentUser);
    }

    public void logout() {
        currentUser = null;
    }
}
