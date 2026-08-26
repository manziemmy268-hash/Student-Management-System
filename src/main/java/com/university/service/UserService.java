package com.university.service;

import com.university.model.User;
import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(int id);

    boolean addUser(User user, String plainPassword);

    boolean updateUser(User user);

    boolean updatePassword(int userId, String newPlainPassword);

    boolean deleteUser(int userId);

    int getUserCount();

    boolean isUsernameDuplicate(String username, int excludeId);
}
