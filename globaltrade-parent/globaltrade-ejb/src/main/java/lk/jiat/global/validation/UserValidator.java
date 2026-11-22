package lk.jiat.global.validation;

import lk.jiat.global.exception.ValidationException;

import java.util.regex.Pattern;

public class UserValidator {

    // Username: alphanumeric, 3-20 chars
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    // Email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    // Password: min 8 chars, at least 1 uppercase, 1 lowercase, 1 number, 1 special
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    // Full name: 2-100 chars, letters, spaces, hyphens, apostrophes
    private static final Pattern FULL_NAME_PATTERN = Pattern.compile(
            "^[a-zA-Z\\s\\-']{2,100}$"
    );

    public static void validateUsername(String username) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new ValidationException(
                    "Username must be 3-20 characters (letters, numbers, underscore only)"
            );
        }
    }

    public static void validatePassword(String password) throws ValidationException {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new ValidationException(
                    "Password must be at least 8 characters with uppercase, lowercase, number, and special character"
            );
        }
    }

    public static void validateEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format");
        }
    }

    public static void validateFullName(String fullName) throws ValidationException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Full name is required");
        }
        if (!FULL_NAME_PATTERN.matcher(fullName).matches()) {
            throw new ValidationException("Full name must be 2-100 characters (letters, spaces, hyphens, apostrophes only)");
        }
    }

    public static void validatePasswordMatch(String password, String confirmPassword) throws ValidationException {
        if (password == null || !password.equals(confirmPassword)) {
            throw new ValidationException("Passwords do not match");
        }
    }

    public static void validateRole(String role, String[] allowedRoles) throws ValidationException {
        if (role == null || role.trim().isEmpty()) {
            throw new ValidationException("Role is required");
        }
        for (String allowed : allowedRoles) {
            if (allowed.equals(role)) {
                return;
            }
        }
        throw new ValidationException("Invalid role: " + role);
    }

    public static void validateVendorId(Integer vendorId) throws ValidationException {
        if (vendorId == null || vendorId <= 0) {
            throw new ValidationException("Valid vendor selection is required");
        }
    }

    private static final Pattern NIC_PATTERN = Pattern.compile("^[0-9]{9}[VXvx]$|^[0-9]{12}$");

    public static void validateNic(String nic) throws ValidationException {
        if (nic == null || nic.trim().isEmpty()) {
            throw new ValidationException("NIC number is required");
        }
        if (!NIC_PATTERN.matcher(nic.trim()).matches()) {
            throw new ValidationException("Invalid NIC format. Use 9 digits + V/X or 12 digits");
        }
    }
}