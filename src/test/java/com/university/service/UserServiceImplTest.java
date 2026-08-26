package com.university.service;

import com.university.dao.UserDao;
import com.university.exception.DuplicateEntityException;
import com.university.exception.ValidationException;
import com.university.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private User buildUser(int id, String username, String fullName, String role) {
        User u = new User();
        u.setUserId(id);
        u.setUsername(username);
        u.setFullName(fullName);
        u.setRole(role);
        return u;
    }

    @Test
    @DisplayName("getAllUsers() delegates to userDao")
    void getAllUsers_delegatesToDao() {
        List<User> mockList = Arrays.asList(
                buildUser(1, "admin", "System Admin", "Admin"),
                buildUser(2, "staff1", "John Staff", "Staff")
        );
        when(userDao.getAllUsers()).thenReturn(mockList);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userDao, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("addUser() returns true for valid user")
    void addUser_validUser_returnsTrue() {
        User user = buildUser(0, "newstaff", "New Staff Member", "Staff");
        when(userDao.isUsernameDuplicate("newstaff", 0)).thenReturn(false);
        when(userDao.addUser(user, "password123")).thenReturn(true);

        boolean result = userService.addUser(user, "password123");

        assertTrue(result);
        verify(userDao, times(1)).addUser(user, "password123");
    }

    @Test
    @DisplayName("addUser() throws DuplicateEntityException for duplicate username")
    void addUser_duplicateUsername_throwsDuplicateEntityException() {
        User user = buildUser(0, "admin", "Another Admin", "Admin");
        when(userDao.isUsernameDuplicate("admin", 0)).thenReturn(true);

        DuplicateEntityException ex = assertThrows(DuplicateEntityException.class,
                () -> userService.addUser(user, "password123"));
        assertEquals("User", ex.getEntityName());
        assertEquals("username", ex.getFieldName());
    }

    @Test
    @DisplayName("addUser() throws ValidationException for short password")
    void addUser_shortPassword_throwsValidationException() {
        User user = buildUser(0, "newstaff", "New Staff", "Staff");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> userService.addUser(user, "ab"));
        assertEquals("password", ex.getField());
    }

    @Test
    @DisplayName("addUser() throws ValidationException for missing role")
    void addUser_missingRole_throwsValidationException() {
        User user = buildUser(0, "newstaff", "New Staff", "");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> userService.addUser(user, "password123"));
        assertEquals("role", ex.getField());
    }

    @Test
    @DisplayName("updateUser() returns true for valid update")
    void updateUser_validUser_returnsTrue() {
        User user = buildUser(1, "admin", "Updated Admin", "Admin");
        when(userDao.isUsernameDuplicate("admin", 1)).thenReturn(false);
        when(userDao.updateUser(user)).thenReturn(true);

        boolean result = userService.updateUser(user);

        assertTrue(result);
        verify(userDao, times(1)).updateUser(user);
    }

    @Test
    @DisplayName("deleteUser() delegates to userDao")
    void deleteUser_delegatesToDao() {
        when(userDao.deleteUser(1)).thenReturn(true);

        boolean result = userService.deleteUser(1);

        assertTrue(result);
        verify(userDao, times(1)).deleteUser(1);
    }

    @Test
    @DisplayName("getUserCount() delegates to userDao")
    void getUserCount_delegatesToDao() {
        when(userDao.getUserCount()).thenReturn(5);

        int count = userService.getUserCount();

        assertEquals(5, count);
        verify(userDao, times(1)).getUserCount();
    }

    @Test
    @DisplayName("updatePassword() returns true for valid password")
    void updatePassword_validPassword_returnsTrue() {
        when(userDao.updatePassword(1, "newpass123")).thenReturn(true);

        boolean result = userService.updatePassword(1, "newpass123");

        assertTrue(result);
        verify(userDao, times(1)).updatePassword(1, "newpass123");
    }

    @Test
    @DisplayName("updatePassword() throws ValidationException for short password")
    void updatePassword_shortPassword_throwsValidationException() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> userService.updatePassword(1, "ab"));
        assertEquals("password", ex.getField());
    }

    @Test
    @DisplayName("isUsernameDuplicate() delegates to userDao")
    void isUsernameDuplicate_delegatesToDao() {
        when(userDao.isUsernameDuplicate("admin", 0)).thenReturn(true);

        boolean result = userService.isUsernameDuplicate("admin", 0);

        assertTrue(result);
        verify(userDao, times(1)).isUsernameDuplicate("admin", 0);
    }
}
