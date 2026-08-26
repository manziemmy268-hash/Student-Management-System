package com.university.service;

import com.university.model.User;

public interface AuthService {

    User authenticate(String username, String password);

    String getRoleName(User user);
}
