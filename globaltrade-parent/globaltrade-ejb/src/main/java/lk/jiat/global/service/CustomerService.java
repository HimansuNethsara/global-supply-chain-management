package lk.jiat.global.service;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lk.jiat.global.dao.CustomerDAO;
import lk.jiat.global.dao.UserDAO;
import lk.jiat.global.entity.Customer;
import lk.jiat.global.entity.User;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.interceptor.ExceptionLogged;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
@ExceptionLogged
@PermitAll
public class CustomerService implements CustomerServiceLocal {

    @Inject
    private CustomerDAO customerDAO;

    @Inject
    private UserDAO userDAO;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Customer createCustomer(Integer userId, String companyName, String contactPhone, String country)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException {

        if (userId == null) {
            throw new ValidationException("User ID is required");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new ValidationException("Country is required");
        }

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (customerDAO.findByUserId(userId).isPresent()) {
            throw new DuplicateResourceException("User already has a customer profile");
        }

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setCompanyName(companyName != null ? companyName.trim() : null);
        customer.setContactPhone(contactPhone != null ? contactPhone.trim() : null);
        customer.setCountry(country.trim());
        customer.setCreatedAt(LocalDateTime.now());

        customerDAO.create(customer);
        return customer;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Customer updateCustomer(Integer customerId, String companyName, String contactPhone, String country)
            throws ResourceNotFoundException, ValidationException {

        Customer customer = customerDAO.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        if (companyName != null) {
            customer.setCompanyName(companyName.trim());
        }
        if (contactPhone != null) {
            customer.setContactPhone(contactPhone.trim());
        }
        if (country != null && !country.trim().isEmpty()) {
            customer.setCountry(country.trim());
        }

        return customerDAO.update(customer);
    }

    @Override
    public Optional<Customer> findCustomerById(Integer customerId) {
        return customerDAO.findById(customerId);
    }

    @Override
    public Optional<Customer> findCustomerByUserId(Integer userId) {
        return customerDAO.findByUserId(userId);
    }

    @Override
    public List<Customer> findAllCustomers() {
        return customerDAO.findAll();
    }

    @Override
    public List<Customer> findCustomersByCountry(String country) {
        return customerDAO.findByCountry(country);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteCustomer(Integer customerId) throws ResourceNotFoundException {
        Customer customer = customerDAO.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        customerDAO.delete(customer);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteCustomerWithUser(Integer customerId) throws ResourceNotFoundException {
        Customer customer = customerDAO.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        User user = customer.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("Customer has no associated user");
        }

        customerDAO.delete(customer);
        userDAO.delete(user);
    }

    @Override
    public boolean userHasCustomer(Integer userId) {
        return customerDAO.findByUserId(userId).isPresent();
    }
}