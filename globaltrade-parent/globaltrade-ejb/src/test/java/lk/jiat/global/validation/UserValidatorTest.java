package lk.jiat.global.validation;

import lk.jiat.global.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    // ===== USERNAME VALIDATION =====

    @Test
    @DisplayName("Should accept valid username")
    void validateUsername_ShouldAcceptValidUsername() {
        assertDoesNotThrow(() -> UserValidator.validateUsername("john_doe"));
        assertDoesNotThrow(() -> UserValidator.validateUsername("admin123"));
        assertDoesNotThrow(() -> UserValidator.validateUsername("user_name_1"));
    }

    @ParameterizedTest
    @DisplayName("Should reject invalid username")
    @ValueSource(strings = {"", "  ", "ab", "very_long_username_more_than_20_chars", "user@name", "user name"})
    void validateUsername_ShouldRejectInvalidUsername(String username) {
        assertThrows(ValidationException.class, () -> UserValidator.validateUsername(username));
    }

    @Test
    @DisplayName("Should reject null username")
    void validateUsername_ShouldRejectNull() {
        assertThrows(ValidationException.class, () -> UserValidator.validateUsername(null));
    }

    // ===== PASSWORD VALIDATION =====

    @Test
    @DisplayName("Should accept valid password")
    void validatePassword_ShouldAcceptValidPassword() {
        assertDoesNotThrow(() -> UserValidator.validatePassword("Password@123"));
        assertDoesNotThrow(() -> UserValidator.validatePassword("StrongP@ssw0rd!"));
        assertDoesNotThrow(() -> UserValidator.validatePassword("A1b2C3d4@!"));
    }

    @ParameterizedTest
    @DisplayName("Should reject invalid password")
    @ValueSource(strings = {"", "  ", "short", "Password", "password123", "PASSWORD@123", "Password@", "12345678"})
    void validatePassword_ShouldRejectInvalidPassword(String password) {
        assertThrows(ValidationException.class, () -> UserValidator.validatePassword(password));
    }

    @Test
    @DisplayName("Should reject null password")
    void validatePassword_ShouldRejectNull() {
        assertThrows(ValidationException.class, () -> UserValidator.validatePassword(null));
    }

    // ===== EMAIL VALIDATION =====

    @Test
    @DisplayName("Should accept valid email")
    void validateEmail_ShouldAcceptValidEmail() {
        assertDoesNotThrow(() -> UserValidator.validateEmail("john@example.com"));
        assertDoesNotThrow(() -> UserValidator.validateEmail("john.doe@company.co.uk"));
        assertDoesNotThrow(() -> UserValidator.validateEmail("john_doe123@email.com"));
    }

    @ParameterizedTest
    @DisplayName("Should reject invalid email")
    @ValueSource(strings = {"", "  ", "john", "john@", "@example.com", "john@example", "john@.com"})
    void validateEmail_ShouldRejectInvalidEmail(String email) {
        assertThrows(ValidationException.class, () -> UserValidator.validateEmail(email));
    }

    @Test
    @DisplayName("Should reject null email")
    void validateEmail_ShouldRejectNull() {
        assertThrows(ValidationException.class, () -> UserValidator.validateEmail(null));
    }

    // ===== FULL NAME VALIDATION =====

    @Test
    @DisplayName("Should accept valid full name")
    void validateFullName_ShouldAcceptValidFullName() {
        assertDoesNotThrow(() -> UserValidator.validateFullName("John Doe"));
        assertDoesNotThrow(() -> UserValidator.validateFullName("Jane Smith-Jones"));
        assertDoesNotThrow(() -> UserValidator.validateFullName("O'Malley"));
        assertDoesNotThrow(() -> UserValidator.validateFullName("Mary Anne"));  // Valid: letters and space
        assertDoesNotThrow(() -> UserValidator.validateFullName("Jean-Luc"));   // Valid: letters and hyphen
        assertDoesNotThrow(() -> UserValidator.validateFullName("D'Angelo"));   // Valid: letters and apostrophe
    }

    @ParameterizedTest
    @DisplayName("Should reject invalid full name")
    @ValueSource(strings = {"", "  ", "A", "This name is way too long and exceeds the 100 character limit", "John123", "John@Doe", "Dr. John Doe"})
    void validateFullName_ShouldRejectInvalidFullName(String fullName) {
        assertThrows(ValidationException.class, () -> UserValidator.validateFullName(fullName));
    }

    @Test
    @DisplayName("Should reject null full name")
    void validateFullName_ShouldRejectNull() {
        assertThrows(ValidationException.class, () -> UserValidator.validateFullName(null));
    }

    // ===== NIC VALIDATION =====

    @Test
    @DisplayName("Should accept valid NIC numbers")
    void validateNic_ShouldAcceptValidNic() {
        // Old format (9 digits + V/X)
        assertDoesNotThrow(() -> UserValidator.validateNic("123456789V"));
        assertDoesNotThrow(() -> UserValidator.validateNic("123456789v"));
        assertDoesNotThrow(() -> UserValidator.validateNic("123456789X"));
        // New format (12 digits)
        assertDoesNotThrow(() -> UserValidator.validateNic("123456789012"));
    }

    @ParameterizedTest
    @DisplayName("Should reject invalid NIC numbers")
    @ValueSource(strings = {"", "  ", "12345678V", "1234567890V", "123456789", "1234567890123", "ABCDEFGHIJK"})
    void validateNic_ShouldRejectInvalidNic(String nic) {
        assertThrows(ValidationException.class, () -> UserValidator.validateNic(nic));
    }

    @Test
    @DisplayName("Should reject null NIC")
    void validateNic_ShouldRejectNull() {
        assertThrows(ValidationException.class, () -> UserValidator.validateNic(null));
    }

    // ===== PASSWORD MATCH VALIDATION =====

    @Test
    @DisplayName("Should accept matching passwords")
    void validatePasswordMatch_ShouldAcceptMatchingPasswords() {
        assertDoesNotThrow(() -> UserValidator.validatePasswordMatch("Password@123", "Password@123"));
    }

    @Test
    @DisplayName("Should reject non-matching passwords")
    void validatePasswordMatch_ShouldRejectNonMatchingPasswords() {
        assertThrows(ValidationException.class,
                () -> UserValidator.validatePasswordMatch("Password@123", "Password@456"));
    }

    @Test
    @DisplayName("Should reject null password for match validation")
    void validatePasswordMatch_ShouldRejectNullPassword() {
        assertThrows(ValidationException.class,
                () -> UserValidator.validatePasswordMatch(null, "Password@123"));
    }

    // ===== ROLE VALIDATION =====

    @Test
    @DisplayName("Should accept valid roles")
    void validateRole_ShouldAcceptValidRoles() {
        String[] allowedRoles = {"LOGISTICS_COORDINATOR", "CUSTOMS_AGENT", "WAREHOUSE_MANAGER", "VENDOR_REP", "CUSTOMER"};
        assertDoesNotThrow(() -> UserValidator.validateRole("CUSTOMER", allowedRoles));
        assertDoesNotThrow(() -> UserValidator.validateRole("LOGISTICS_COORDINATOR", allowedRoles));
    }

    @Test
    @DisplayName("Should reject invalid roles")
    void validateRole_ShouldRejectInvalidRoles() {
        String[] allowedRoles = {"CUSTOMER", "VENDOR_REP"};
        assertThrows(ValidationException.class,
                () -> UserValidator.validateRole("ADMIN", allowedRoles));
        assertThrows(ValidationException.class,
                () -> UserValidator.validateRole("LOGISTICS_COORDINATOR", allowedRoles));
    }

    @Test
    @DisplayName("Should reject null role")
    void validateRole_ShouldRejectNull() {
        String[] allowedRoles = {"CUSTOMER"};
        assertThrows(ValidationException.class,
                () -> UserValidator.validateRole(null, allowedRoles));
    }

    // ===== VENDOR ID VALIDATION =====

    @Test
    @DisplayName("Should accept valid vendor ID")
    void validateVendorId_ShouldAcceptValidId() {
        assertDoesNotThrow(() -> UserValidator.validateVendorId(1));
        assertDoesNotThrow(() -> UserValidator.validateVendorId(100));
    }

    @Test
    @DisplayName("Should reject invalid vendor ID")
    void validateVendorId_ShouldRejectInvalidId() {
        assertThrows(ValidationException.class, () -> UserValidator.validateVendorId(null));
        assertThrows(ValidationException.class, () -> UserValidator.validateVendorId(0));
        assertThrows(ValidationException.class, () -> UserValidator.validateVendorId(-1));
    }
}