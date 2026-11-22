package lk.jiat.global.service;

import jakarta.ejb.Local;
import lk.jiat.global.entity.Customer;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@Local
public interface CustomerServiceLocal {

    // CRUD Operations
    Customer createCustomer(Integer userId, String companyName, String contactPhone, String country)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException;

    Customer updateCustomer(Integer customerId, String companyName, String contactPhone, String country)
            throws ResourceNotFoundException, ValidationException;

    Optional<Customer> findCustomerById(Integer customerId);

    Optional<Customer> findCustomerByUserId(Integer userId);

    List<Customer> findAllCustomers();

    List<Customer> findCustomersByCountry(String country);

    void deleteCustomer(Integer customerId) throws ResourceNotFoundException;

    void deleteCustomerWithUser(Integer customerId) throws ResourceNotFoundException;

    // Validation
    boolean userHasCustomer(Integer userId);
}