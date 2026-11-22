package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class UserDAO extends GenericDAO<User, Integer> {

    public UserDAO() {
        super(User.class);
    }

    public Optional<User> findByUsername(String username) {
        try {
            User user = em.createQuery(
                            "SELECT u FROM User u " +
                                    "LEFT JOIN FETCH u.vendor " +
                                    "LEFT JOIN FETCH u.role " +
                                    "WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        try {
            User user = em.createQuery(
                            "SELECT u FROM User u " +
                                    "LEFT JOIN FETCH u.vendor " +
                                    "LEFT JOIN FETCH u.role " +
                                    "WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<User> findByRole(String roleName) {
        try {
            return em.createQuery(
                            "SELECT u FROM User u " +
                                    "LEFT JOIN FETCH u.vendor " +
                                    "WHERE u.role.roleName = :roleName", User.class)
                    .setParameter("roleName", roleName)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<User> findByVendorId(Integer vendorId) {
        try {
            return em.createQuery(
                            "SELECT u FROM User u " +
                                    "LEFT JOIN FETCH u.role " +
                                    "WHERE u.vendor.vendorId = :vendorId", User.class)
                    .setParameter("vendorId", vendorId)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<User> findByStatus(String status) {
        try {
            return em.createQuery(
                            "SELECT u FROM User u " +
                                    "LEFT JOIN FETCH u.role " +
                                    "WHERE u.status = :status", User.class)
                    .setParameter("status", status)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<User> findByRoleAndStatus(String roleName, String status) {
        try {
            return em.createQuery(
                            "SELECT u FROM User u " +
                                    "LEFT JOIN FETCH u.vendor " +
                                    "LEFT JOIN FETCH u.role " +
                                    "WHERE u.role.roleName = :roleName " +
                                    "AND u.status = :status", User.class)
                    .setParameter("roleName", roleName)
                    .setParameter("status", status)
                    .getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }
}