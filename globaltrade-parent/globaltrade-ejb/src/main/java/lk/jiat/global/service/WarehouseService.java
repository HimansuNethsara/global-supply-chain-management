package lk.jiat.global.service;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lk.jiat.global.dao.WarehouseDAO;
import lk.jiat.global.entity.Warehouse;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.interceptor.AuditLogged;
import lk.jiat.global.interceptor.ExceptionLogged;

import java.util.List;
import java.util.Optional;

@Stateless
@AuditLogged(skipReadOnly = true)
@ExceptionLogged
public class WarehouseService implements WarehouseServiceLocal {

    @Inject
    private WarehouseDAO warehouseDAO;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Warehouse createWarehouse(String name, String location, String country, Integer capacity)
            throws ValidationException, DuplicateResourceException {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Warehouse name is required");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new ValidationException("Location is required");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new ValidationException("Country is required");
        }
        if (capacity == null || capacity < 0) {
            throw new ValidationException("Capacity must be 0 or greater");
        }

        if (!isWarehouseNameUnique(name.trim(), null)) {
            throw new DuplicateResourceException("Warehouse with name '" + name + "' already exists");
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setName(name.trim());
        warehouse.setLocation(location.trim());
        warehouse.setCountry(country.trim());
        warehouse.setCapacity(capacity);

        warehouseDAO.create(warehouse);
        return warehouse;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Warehouse updateWarehouse(Integer warehouseId, String name, String location, String country, Integer capacity)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException {

        Warehouse warehouse = warehouseDAO.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId));

        if (name != null && !name.trim().isEmpty()) {
            if (!isWarehouseNameUnique(name.trim(), warehouseId)) {
                throw new DuplicateResourceException("Warehouse with name '" + name + "' already exists");
            }
            warehouse.setName(name.trim());
        }
        if (location != null && !location.trim().isEmpty()) {
            warehouse.setLocation(location.trim());
        }
        if (country != null && !country.trim().isEmpty()) {
            warehouse.setCountry(country.trim());
        }
        if (capacity != null && capacity >= 0) {
            warehouse.setCapacity(capacity);
        }

        return warehouseDAO.update(warehouse);
    }

    @Override
    public Optional<Warehouse> findWarehouseById(Integer warehouseId) {
        return warehouseDAO.findById(warehouseId);
    }

    @Override
    public List<Warehouse> findAllWarehouses() {
        return warehouseDAO.findAll();
    }

    @Override

    public List<Warehouse> findWarehousesByCountry(String country) {
        return warehouseDAO.findByCountry(country);
    }

    @Override
    public List<Warehouse> findWarehousesByMinCapacity(Integer minCapacity) {
        return warehouseDAO.findByCapacityGreaterThan(minCapacity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteWarehouse(Integer warehouseId) throws ResourceNotFoundException {
        Warehouse warehouse = warehouseDAO.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId));
        warehouseDAO.delete(warehouse);
    }

    @Override
    public boolean isWarehouseNameUnique(String name, Integer excludeId) {
        List<Warehouse> all = warehouseDAO.findAll();
        return all.stream()
                .filter(w -> excludeId == null || !w.getWarehouseId().equals(excludeId))
                .noneMatch(w -> w.getName().equalsIgnoreCase(name));
    }
}