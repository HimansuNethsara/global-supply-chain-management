package lk.jiat.global.service;

import jakarta.ejb.Local;
import lk.jiat.global.entity.User;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.InvalidCredentialsException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@Local
public interface UserServiceLocal {

    List<Vendor> findAllVendors();

    User register(String username, String password, String email,
                  String fullName, String roleName, Integer vendorId)
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException;

    User registerStaffWithNic(String username, String password, String email,
                              String fullName, String roleName, String nicNumber)
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException;

    User registerCustomsAgent(String username, String password, String email,
                              String fullName, String roleName)
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException;

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User authenticate(String username, String password) throws InvalidCredentialsException;

    String getUserRole(User user);

    boolean hasRole(User user, String roleName);

    List<User> findUsersByRole(String roleName);

    List<User> findUsersByRoleAndStatus(String roleName, String status);

    User updateUser(User user) throws ResourceNotFoundException, ValidationException;
}