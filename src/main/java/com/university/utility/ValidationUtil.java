package com.university.utility;

import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{10,15}$");

    private ValidationUtil() {
        throw new AssertionError("No instances");
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidId(String id) {
        if (!isNotEmpty(id)) return false;
        try {
            long num = Long.parseLong(id);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidGpa(String gpa) {
        if (!isNotEmpty(gpa)) return false;
        try {
            double val = Double.parseDouble(gpa);
            return val >= 0.0 && val <= 4.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidCredits(String credits) {
        if (!isNotEmpty(credits)) return false;
        try {
            int val = Integer.parseInt(credits);
            return val > 0 && val <= 20;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidName(String name) {
        return isNotEmpty(name) && name.trim().length() >= 2;
    }

    public static boolean isValidCode(String code) {
        return isNotEmpty(code) && code.trim().length() >= 2 && code.trim().length() <= 20;
    }

    public static String sanitizeInput(String input) {
        if (input == null) return "";
        return input.replaceAll("[<>\"';&]", "").trim();
    }
}
