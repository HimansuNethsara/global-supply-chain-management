package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.Customer;

import java.util.List;
import java.util.Optional;

@Stateless
public class CustomerDAO extends GenericDAO<Customer, Integer> {

    public CustomerDAO() {
        super(Customer.class);
    }

    @Override
    public Optional<Customer> findById(Integer id) {
        try {
            Customer customer = em.createQuery(
                            "SELECT c FROM Customer c " +
                                    "JOIN FETCH c.user " +
                                    "WHERE c.customerId = :id", Customer.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.ofNullable(customer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Customer> findByIdWithUser(Integer id) {
        try {
            Customer customer = em.createQuery(
                            "SELECT c FROM Customer c " +
                                    "JOIN FETCH c.user " +
                                    "WHERE c.customerId = :id", Customer.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.ofNullable(customer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Customer> findByUserId(Integer userId) {
        try {
            Customer customer = em.createQuery(
                            "SELECT c FROM Customer c " +
                                    "JOIN FETCH c.user " +
                                    "WHERE c.user.userId = :userId", Customer.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
            return Optional.ofNullable(customer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Customer> findByCountry(String country) {
        return em.createQuery(
                        "SELECT c FROM Customer c " +
                                "JOIN FETCH c.user " +
                                "WHERE c.country = :country", Customer.class)
                .setParameter("country", country)
                .getResultList();
    }

    @Override
    public List<Customer> findAll() {
        return em.createQuery(
                        "SELECT c FROM Customer c " +
                                "JOIN FETCH c.user", Customer.class)
                .getResultList();
    }
}