package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.Employee;

import java.util.Optional;

@Stateless
public class EmployeeDAO extends GenericDAO<Employee, Integer> {

    public EmployeeDAO() {
        super(Employee.class);
    }

    public Optional<Employee> findByNicNumber(String nicNumber) {
        try {
            Employee employee = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.nicNumber = :nicNumber", Employee.class)
                    .setParameter("nicNumber", nicNumber)
                    .getSingleResult();
            return Optional.ofNullable(employee);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Employee> findActiveUnregisteredByNicAndPosition(String nicNumber, String jobPosition) {
        try {
            Employee employee = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.nicNumber = :nicNumber " +
                                    "AND e.status = 'ACTIVE' AND e.isRegistered = false " +
                                    "AND e.jobPosition = :jobPosition", Employee.class)
                    .setParameter("nicNumber", nicNumber)
                    .setParameter("jobPosition", jobPosition)
                    .getSingleResult();
            return Optional.ofNullable(employee);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Employee> findRegisteredByNic(String nicNumber) {
        try {
            Employee employee = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.nicNumber = :nicNumber " +
                                    "AND e.isRegistered = true", Employee.class)
                    .setParameter("nicNumber", nicNumber)
                    .getSingleResult();
            return Optional.ofNullable(employee);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void markAsRegistered(Integer employeeId) {
        findById(employeeId).ifPresent(emp -> {
            emp.setIsRegistered(true);
            update(emp);
        });
    }
}