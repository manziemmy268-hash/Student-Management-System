package com.university.utility;

import com.university.exception.ValidationException;

public final class InitialPasswordProvider {

    private InitialPasswordProvider() {
    }

    public static String getRequired() {
        String password = System.getProperty("initial.account.password");
        if (password == null || password.isBlank()) {
            password = System.getenv("INITIAL_ACCOUNT_PASSWORD");
        }
        if (password == null || password.isBlank()) {
            throw new ValidationException("password",
                    "Set INITIAL_ACCOUNT_PASSWORD before creating an account without a password");
        }
        return password;
    }
}
