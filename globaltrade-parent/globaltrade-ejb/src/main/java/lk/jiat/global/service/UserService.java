package lk.jiat.global.service;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lk.jiat.global.dao.UserDAO;
import lk.jiat.global.dao.RoleDAO;
import lk.jiat.global.dao.VendorDAO;
import lk.jiat.global.dao.CustomerDAO;
import lk.jiat.global.dao.EmployeeDAO;
import lk.jiat.global.entity.User;
import lk.jiat.global.entity.Role;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.entity.Customer;
import lk.jiat.global.entity.Employee;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.InvalidCredentialsException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.interceptor.ExceptionLogged;
import lk.jiat.global.validation.UserValidator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Stateless
@ExceptionLogged
public class UserService implements UserServiceLocal {

    @Inject
    private UserDAO userDAO;

    @Inject
    private RoleDAO roleDAO;

    @Inject
    private VendorDAO vendorDAO;

    @Inject
    private CustomerDAO customerDAO;

    @Inject
    private EmployeeDAO employeeDAO;

    // ===== SHA-256 HELPER METHODS =====

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private boolean verifyPassword(String plainPassword, String storedHash) {
        String hashed = hashPassword(plainPassword);
        return hashed.equals(storedHash);
    }

    @Override
    @PermitAll
    public List<Vendor> findAllVendors() {
        return vendorDAO.findAll();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PermitAll
    public User register(String username, String password, String email,
                         String fullName, String roleName, Integer vendorId)
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException {

        UserValidator.validateUsername(username);
        UserValidator.validatePassword(password);
        UserValidator.validateEmail(email);
        UserValidator.validateFullName(fullName);
        UserValidator.validateRole(roleName, new String[]{"CUSTOMER", "VENDOR_REP"});

        validateUniqueUser(username, email);

        Role role = getRole(roleName);

        User user = createBaseUser(username, password, email, fullName, role, "ACTIVE");

        if ("VENDOR_REP".equals(roleName)) {
            if (vendorId == null) {
                throw new ValidationException("Vendor selection is required for Vendor Representative");
            }
            Vendor vendor = vendorDAO.findById(vendorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor with ID " + vendorId + " not found"));
            user.setVendor(vendor);
        }

        userDAO.create(user);

        if ("CUSTOMER".equals(roleName)) {
            Customer customer = new Customer(user, "Sri Lanka");
            customerDAO.create(customer);
            user.setCustomer(customer);
        }

        return user;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PermitAll
    public User registerStaffWithNic(String username, String password, String email,
                                     String fullName, String roleName, String nicNumber)
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException {

        UserValidator.validateUsername(username);
        UserValidator.validatePassword(password);
        UserValidator.validateEmail(email);
        UserValidator.validateFullName(fullName);
        UserValidator.validateNic(nicNumber);
        UserValidator.validateRole(roleName, new String[]{"LOGISTICS_COORDINATOR", "WAREHOUSE_MANAGER"});

        validateUniqueUser(username, email);

        Optional<Employee> alreadyRegistered = employeeDAO.findRegisteredByNic(nicNumber.trim());
        if (alreadyRegistered.isPresent()) {
            throw new ValidationException("This NIC has already been used to register. Please contact support.");
        }

        Optional<Employee> empOpt = employeeDAO.findActiveUnregisteredByNicAndPosition(nicNumber.trim(), roleName);
        if (empOpt.isEmpty()) {
            throw new ValidationException("Invalid NIC or you are not authorized for this role");
        }

        Employee employee = empOpt.get();
        Role role = getRole(roleName);

        User user = createBaseUser(username, password, email, fullName, role, "ACTIVE");

        userDAO.create(user);
        employeeDAO.markAsRegistered(employee.getEmployeeId());

        return user;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PermitAll
    public User registerCustomsAgent(String username, String password, String email,
                                     String fullName, String roleName)
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException {

        UserValidator.validateUsername(username);
        UserValidator.validatePassword(password);
        UserValidator.validateEmail(email);
        UserValidator.validateFullName(fullName);
        UserValidator.validateRole(roleName, new String[]{"CUSTOMS_AGENT"});

        validateUniqueUser(username, email);

        Role role = getRole(roleName);

        User user = createBaseUser(username, password, email, fullName, role, "PENDING_APPROVAL");

        userDAO.create(user);
        return user;
    }

    @Override
    @PermitAll
    public User authenticate(String username, String password) throws InvalidCredentialsException {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        User user = userOpt.get();

        if (!verifyPassword(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        if ("PENDING_APPROVAL".equals(user.getStatus())) {
            throw new InvalidCredentialsException("Your account is pending approval. Please wait for confirmation.");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new InvalidCredentialsException("Your account is " + user.getStatus().toLowerCase() + ". Please contact support.");
        }

        return user;
    }

    @Override
    @PermitAll
    public Optional<User> findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    @PermitAll
    public Optional<User> findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @Override
    @PermitAll
    public String getUserRole(User user) {
        return user.getRole().getRoleName();
    }

    @Override
    @PermitAll
    public boolean hasRole(User user, String roleName) {
        return user.getRole().getRoleName().equals(roleName);
    }

    @Override
    public List<User> findUsersByRole(String roleName) {
        return userDAO.findByRole(roleName);
    }

    @Override
    public List<User> findUsersByRoleAndStatus(String roleName, String status) {
        return userDAO.findByRoleAndStatus(roleName, status);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User updateUser(User user) throws ResourceNotFoundException, ValidationException {
        if (user == null || user.getUserId() == null) {
            throw new ValidationException("User is required");
        }
        if (!userDAO.findById(user.getUserId()).isPresent()) {
            throw new ResourceNotFoundException("User not found with ID: " + user.getUserId());
        }
        return userDAO.update(user);
    }

    private void validateUniqueUser(String username, String email) throws DuplicateResourceException {
        if (userDAO.findByUsername(username).isPresent()) {
            throw new DuplicateResourceException("Username '" + username + "' already exists");
        }
        if (userDAO.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Email '" + email + "' already exists");
        }
    }

    private Role getRole(String roleName) throws ResourceNotFoundException {
        Role role = roleDAO.findByRoleName(roleName);
        if (role == null) {
            throw new ResourceNotFoundException("Role '" + roleName + "' not found");
        }
        return role;
    }

    private User createBaseUser(String username, String password, String email,
                                String fullName, Role role, String status) {
        String hashedPassword = hashPassword(password);
        User user = new User(username, hashedPassword, email, fullName, role);
        user.setStatus(status);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}