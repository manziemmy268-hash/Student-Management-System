package com.university.controller;

import com.university.model.User;
import com.university.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginController Tests")
class LoginControllerTest {

    @Mock
    private AuthService authService;

    private LoginController loginController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        loginController = new LoginController(authService);
        mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUsername("admin");
        mockUser.setFullName("System Admin");
        mockUser.setRole("Admin");
    }

    @Test
    @DisplayName("authenticate() returns true when credentials are valid")
    void authenticate_validCredentials_returnsTrue() {
        when(authService.authenticate("admin", "password123")).thenReturn(mockUser);

        boolean result = loginController.authenticate("admin", "password123");

        assertTrue(result);
        verify(authService, times(1)).authenticate("admin", "password123");
    }

    @Test
    @DisplayName("authenticate() returns false when credentials are invalid")
    void authenticate_invalidCredentials_returnsFalse() {
        when(authService.authenticate("admin", "wrongpass")).thenReturn(null);

        boolean result = loginController.authenticate("admin", "wrongpass");

        assertFalse(result);
        verify(authService, times(1)).authenticate("admin", "wrongpass");
    }

    @Test
    @DisplayName("getCurrentUser() returns the authenticated user after successful login")
    void getCurrentUser_afterSuccessfulLogin_returnsUser() {
        when(authService.authenticate("admin", "password123")).thenReturn(mockUser);
        loginController.authenticate("admin", "password123");

        User currentUser = loginController.getCurrentUser();

        assertNotNull(currentUser);
        assertEquals("admin", currentUser.getUsername());
        assertEquals("Admin", currentUser.getRole());
    }

    @Test
    @DisplayName("getCurrentUser() returns null before any login attempt")
    void getCurrentUser_beforeLogin_returnsNull() {
        assertNull(loginController.getCurrentUser());
    }

    @Test
    @DisplayName("logout() clears the current user session")
    void logout_clearsCurrentUser() {
        when(authService.authenticate("admin", "password123")).thenReturn(mockUser);
        loginController.authenticate("admin", "password123");

        loginController.logout();

        assertNull(loginController.getCurrentUser());
    }

    @Test
    @DisplayName("authenticate() handles null username gracefully")
    void authenticate_nullUsername_returnsFalse() {
        when(authService.authenticate(null, "pass")).thenReturn(null);

        boolean result = loginController.authenticate(null, "pass");

        assertFalse(result);
        verify(authService, times(1)).authenticate(null, "pass");
    }

    @Test
    @DisplayName("authenticate() handles empty password gracefully")
    void authenticate_emptyPassword_returnsFalse() {
        when(authService.authenticate("admin", "")).thenReturn(null);

        boolean result = loginController.authenticate("admin", "");

        assertFalse(result);
        verify(authService, times(1)).authenticate("admin", "");
    }

    @Test
    @DisplayName("authenticate() propagates exceptions instead of hiding them")
    void authenticate_serviceThrowsException_propagates() {
        when(authService.authenticate("admin", "pass")).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> loginController.authenticate("admin", "pass"));
        assertNull(loginController.getCurrentUser());
    }

    @Test
    @DisplayName("getRoleName() returns role of current user")
    void getRoleName_afterLogin_returnsRole() {
        when(authService.authenticate("admin", "password123")).thenReturn(mockUser);
        when(authService.getRoleName(mockUser)).thenReturn("Admin");
        loginController.authenticate("admin", "password123");

        String role = loginController.getRoleName();

        assertEquals("Admin", role);
    }
}
