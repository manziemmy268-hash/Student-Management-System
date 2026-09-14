package com.university.utility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidationUtil Tests")
class ValidationUtilTest {

    // ===== isValidName Tests =====

    @Test
    @DisplayName("isValidName() returns false for null")
    void isValidName_null_returnsFalse() {
        assertFalse(ValidationUtil.isValidName(null));
    }

    @Test
    @DisplayName("isValidName() returns false for empty string")
    void isValidName_empty_returnsFalse() {
        assertFalse(ValidationUtil.isValidName(""));
    }

    @Test
    @DisplayName("isValidName() returns false for blank string")
    void isValidName_blank_returnsFalse() {
        assertFalse(ValidationUtil.isValidName("   "));
    }

    @Test
    @DisplayName("isValidName() returns false for single character")
    void isValidName_singleChar_returnsFalse() {
        assertFalse(ValidationUtil.isValidName("A"));
    }

    @Test
    @DisplayName("isValidName() returns true for valid name")
    void isValidName_validName_returnsTrue() {
        assertTrue(ValidationUtil.isValidName("Alice"));
    }

    @Test
    @DisplayName("isValidName() returns true for two character name")
    void isValidName_twoChars_returnsTrue() {
        assertTrue(ValidationUtil.isValidName("Al"));
    }

    @Test
    @DisplayName("isValidName() returns true for name with leading spaces")
    void isValidName_withLeadingSpaces_returnsTrue() {
        assertTrue(ValidationUtil.isValidName("  Alice"));
    }

    // ===== isValidEmail Tests =====

    @Test
    @DisplayName("isValidEmail() returns false for null")
    void isValidEmail_null_returnsFalse() {
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    @DisplayName("isValidEmail() returns false for empty string")
    void isValidEmail_empty_returnsFalse() {
        assertFalse(ValidationUtil.isValidEmail(""));
    }

    @Test
    @DisplayName("isValidEmail() returns false for invalid email without @")
    void isValidEmail_noAt_returnsFalse() {
        assertFalse(ValidationUtil.isValidEmail("aliceuniversity.edu"));
    }

    @Test
    @DisplayName("isValidEmail() returns false for invalid email without domain")
    void isValidEmail_noDomain_returnsFalse() {
        assertFalse(ValidationUtil.isValidEmail("alice@"));
    }

    @Test
    @DisplayName("isValidEmail() returns true for valid email")
    void isValidEmail_validEmail_returnsTrue() {
        assertTrue(ValidationUtil.isValidEmail("alice@uni.edu"));
    }

    @Test
    @DisplayName("isValidEmail() returns true for email with subdomains")
    void isValidEmail_subdomain_returnsTrue() {
        assertTrue(ValidationUtil.isValidEmail("alice@mail.cs.uni.edu"));
    }

    // ===== isValidPhone Tests =====

    @Test
    @DisplayName("isValidPhone() returns false for null")
    void isValidPhone_null_returnsFalse() {
        assertFalse(ValidationUtil.isValidPhone(null));
    }

    @Test
    @DisplayName("isValidPhone() returns false for empty string")
    void isValidPhone_empty_returnsFalse() {
        assertFalse(ValidationUtil.isValidPhone(""));
    }

    @Test
    @DisplayName("isValidPhone() returns false for too short number")
    void isValidPhone_tooShort_returnsFalse() {
        assertFalse(ValidationUtil.isValidPhone("+1234"));
    }

    @Test
    @DisplayName("isValidPhone() returns false for letters in phone")
    void isValidPhone_letters_returnsFalse() {
        assertFalse(ValidationUtil.isValidPhone("+1234567890abc"));
    }

    @Test
    @DisplayName("isValidPhone() returns true for valid phone with plus")
    void isValidPhone_validWithPlus_returnsTrue() {
        assertTrue(ValidationUtil.isValidPhone("+1234567890"));
    }

    @Test
    @DisplayName("isValidPhone() returns true for valid phone without plus")
    void isValidPhone_validWithoutPlus_returnsTrue() {
        assertTrue(ValidationUtil.isValidPhone("1234567890"));
    }

    @Test
    @DisplayName("isValidPhone() returns true for 15 digit phone")
    void isValidPhone_15Digits_returnsTrue() {
        assertTrue(ValidationUtil.isValidPhone("123456789012345"));
    }

    // ===== isValidCode Tests =====

    @Test
    @DisplayName("isValidCode() returns false for null")
    void isValidCode_null_returnsFalse() {
        assertFalse(ValidationUtil.isValidCode(null));
    }

    @Test
    @DisplayName("isValidCode() returns false for empty string")
    void isValidCode_empty_returnsFalse() {
        assertFalse(ValidationUtil.isValidCode(""));
    }

    @Test
    @DisplayName("isValidCode() returns false for single character")
    void isValidCode_singleChar_returnsFalse() {
        assertFalse(ValidationUtil.isValidCode("A"));
    }

    @Test
    @DisplayName("isValidCode() returns true for two character code")
    void isValidCode_twoChars_returnsTrue() {
        assertTrue(ValidationUtil.isValidCode("CS"));
    }

    @Test
    @DisplayName("isValidCode() returns true for valid code")
    void isValidCode_validCode_returnsTrue() {
        assertTrue(ValidationUtil.isValidCode("CS201"));
    }

    @Test
    @DisplayName("isValidCode() returns true for 20 character code")
    void isValidCode_20Chars_returnsTrue() {
        assertTrue(ValidationUtil.isValidCode("ABCDEF12345678901234"));
    }

    @Test
    @DisplayName("isValidCode() returns false for code longer than 20 characters")
    void isValidCode_tooLong_returnsFalse() {
        assertFalse(ValidationUtil.isValidCode("ABCDEF123456789012345"));
    }

    // ===== sanitizeInput Tests =====

    @Test
    @DisplayName("sanitizeInput() returns empty string for null")
    void sanitizeInput_null_returnsEmpty() {
        assertEquals("", ValidationUtil.sanitizeInput(null));
    }

    @Test
    @DisplayName("sanitizeInput() trims whitespace")
    void sanitizeInput_whitespace_trims() {
        assertEquals("Hello", ValidationUtil.sanitizeInput("  Hello  "));
    }

    @Test
    @DisplayName("sanitizeInput() removes angle brackets and quotes")
    void sanitizeInput_html_removesTags() {
        assertEquals("scriptalert(XSS)/script", ValidationUtil.sanitizeInput("<script>alert('XSS')</script>"));
    }

    @Test
    @DisplayName("sanitizeInput() removes semicolons and quotes")
    void sanitizeInput_specialChars_removesThem() {
        assertEquals("HelloWorld", ValidationUtil.sanitizeInput("Hello\"World;"));
    }

    @Test
    @DisplayName("sanitizeInput() returns same string when no special characters")
    void sanitizeInput_cleanString_returnsSame() {
        assertEquals("CS201", ValidationUtil.sanitizeInput("CS201"));
    }

    @Test
    @DisplayName("sanitizeInput() removes ampersand")
    void sanitizeInput_ampersand_removesIt() {
        assertEquals("ATT", ValidationUtil.sanitizeInput("AT&T"));
    }

    // ===== isNotEmpty Tests =====

    @Test
    @DisplayName("isNotEmpty() returns false for null")
    void isNotEmpty_null_returnsFalse() {
        assertFalse(ValidationUtil.isNotEmpty(null));
    }

    @Test
    @DisplayName("isNotEmpty() returns false for empty string")
    void isNotEmpty_empty_returnsFalse() {
        assertFalse(ValidationUtil.isNotEmpty(""));
    }

    @Test
    @DisplayName("isNotEmpty() returns false for blank string")
    void isNotEmpty_blank_returnsFalse() {
        assertFalse(ValidationUtil.isNotEmpty("   "));
    }

    @Test
    @DisplayName("isNotEmpty() returns true for valid string")
    void isNotEmpty_valid_returnsTrue() {
        assertTrue(ValidationUtil.isNotEmpty("Hello"));
    }

    @Test
    @DisplayName("isNotEmpty() returns true for string with leading spaces")
    void isNotEmpty_withSpaces_returnsTrue() {
        assertTrue(ValidationUtil.isNotEmpty("  Hello"));
    }

    // ===== isValidId Tests =====

    @Test
    @DisplayName("isValidId() returns false for null")
    void isValidId_null_returnsFalse() {
        assertFalse(ValidationUtil.isValidId(null));
    }

    @Test
    @DisplayName("isValidId() returns false for empty string")
    void isValidId_empty_returnsFalse() {
        assertFalse(ValidationUtil.isValidId(""));
    }

    @Test
    @DisplayName("isValidId() returns false for non-numeric string")
    void isValidId_nonNumeric_returnsFalse() {
        assertFalse(ValidationUtil.isValidId("abc"));
    }

    @Test
    @DisplayName("isValidId() returns false for zero")
    void isValidId_zero_returnsFalse() {
        assertFalse(ValidationUtil.isValidId("0"));
    }

    @Test
    @DisplayName("isValidId() returns false for negative number")
    void isValidId_negative_returnsFalse() {
        assertFalse(ValidationUtil.isValidId("-1"));
    }

    @Test
    @DisplayName("isValidId() returns true for valid positive id")
    void isValidId_valid_returnsTrue() {
        assertTrue(ValidationUtil.isValidId("1"));
    }

    // ===== isValidGpa Tests =====

    @Test
    @DisplayName("isValidGpa() returns false for null")
    void isValidGpa_null_returnsFalse() {
        assertFalse(ValidationUtil.isValidGpa(null));
    }

    @Test
    @DisplayName("isValidGpa() returns false for empty string")
    void isValidGpa_empty_returnsFalse() {
        assertFalse(ValidationUtil.isValidGpa(""));
    }

    @Test
    @DisplayName("isValidGpa() returns false for non-numeric string")
    void isValidGpa_nonNumeric_returnsFalse() {
        assertFalse(ValidationUtil.isValidGpa("abc"));
    }

    @Test
    @DisplayName("isValidGpa() returns false for value above 4.0")
    void isValidGpa_aboveMax_returnsFalse() {
        assertFalse(ValidationUtil.isValidGpa("4.5"));
    }

    @Test
    @DisplayName("isValidGpa() returns false for negative value")
    void isValidGpa_negative_returnsFalse() {
        assertFalse(ValidationUtil.isValidGpa("-0.5"));
    }

    @Test
    @DisplayName("isValidGpa() returns true for valid gpa")
    void isValidGpa_valid_returnsTrue() {
        assertTrue(ValidationUtil.isValidGpa("3.5"));
    }

    @Test
    @DisplayName("isValidGpa() returns true for boundary values")
    void isValidGpa_boundaries_returnsTrue() {
        assertTrue(ValidationUtil.isValidGpa("0.0"));
        assertTrue(ValidationUtil.isValidGpa("4.0"));
    }

    // ===== isValidCredits Tests =====

    @Test
    @DisplayName("isValidCredits() returns false for null")
    void isValidCredits_null_returnsFalse() {
        assertFalse(ValidationUtil.isValidCredits(null));
    }

    @Test
    @DisplayName("isValidCredits() returns false for empty string")
    void isValidCredits_empty_returnsFalse() {
        assertFalse(ValidationUtil.isValidCredits(""));
    }

    @Test
    @DisplayName("isValidCredits() returns false for non-numeric string")
    void isValidCredits_nonNumeric_returnsFalse() {
        assertFalse(ValidationUtil.isValidCredits("abc"));
    }

    @Test
    @DisplayName("isValidCredits() returns false for zero credits")
    void isValidCredits_zero_returnsFalse() {
        assertFalse(ValidationUtil.isValidCredits("0"));
    }

    @Test
    @DisplayName("isValidCredits() returns false for credits above 20")
    void isValidCredits_aboveMax_returnsFalse() {
        assertFalse(ValidationUtil.isValidCredits("21"));
    }

    @Test
    @DisplayName("isValidCredits() returns true for valid credits")
    void isValidCredits_valid_returnsTrue() {
        assertTrue(ValidationUtil.isValidCredits("3"));
    }

    @Test
    @DisplayName("isValidCredits() returns true for boundary values")
    void isValidCredits_boundaries_returnsTrue() {
        assertTrue(ValidationUtil.isValidCredits("1"));
        assertTrue(ValidationUtil.isValidCredits("20"));
    }

    // ===== isValidPositiveInt Tests =====

    @Test
    @DisplayName("isValidPositiveInt() returns false for null")
    void isValidPositiveInt_null_returnsFalse() {
        assertFalse(ValidationUtil.isValidPositiveInt(null));
    }

    @Test
    @DisplayName("isValidPositiveInt() returns false for empty string")
    void isValidPositiveInt_empty_returnsFalse() {
        assertFalse(ValidationUtil.isValidPositiveInt(""));
    }

    @Test
    @DisplayName("isValidPositiveInt() returns false for non-numeric string")
    void isValidPositiveInt_nonNumeric_returnsFalse() {
        assertFalse(ValidationUtil.isValidPositiveInt("abc"));
    }

    @Test
    @DisplayName("isValidPositiveInt() returns false for zero")
    void isValidPositiveInt_zero_returnsFalse() {
        assertFalse(ValidationUtil.isValidPositiveInt("0"));
    }

    @Test
    @DisplayName("isValidPositiveInt() returns false for negative number")
    void isValidPositiveInt_negative_returnsFalse() {
        assertFalse(ValidationUtil.isValidPositiveInt("-5"));
    }

    @Test
    @DisplayName("isValidPositiveInt() returns true for valid positive number")
    void isValidPositiveInt_valid_returnsTrue() {
        assertTrue(ValidationUtil.isValidPositiveInt("50"));
    }

    // ===== isValidDate Tests =====

    @Test
    @DisplayName("isValidDate() returns false for null")
    void isValidDate_null_returnsFalse() {
        assertFalse(ValidationUtil.isValidDate(null));
    }

    @Test
    @DisplayName("isValidDate() returns false for empty string")
    void isValidDate_empty_returnsFalse() {
        assertFalse(ValidationUtil.isValidDate(""));
    }

    @Test
    @DisplayName("isValidDate() returns false for non-date string")
    void isValidDate_nonDate_returnsFalse() {
        assertFalse(ValidationUtil.isValidDate("not-a-date"));
    }

    @Test
    @DisplayName("isValidDate() returns false for malformed date")
    void isValidDate_malformed_returnsFalse() {
        assertFalse(ValidationUtil.isValidDate("2026/01/01"));
    }

    @Test
    @DisplayName("isValidDate() returns true for valid date")
    void isValidDate_valid_returnsTrue() {
        assertTrue(ValidationUtil.isValidDate("2026-09-01"));
    }
}
