package lk.jiat.global.service;

import jakarta.ejb.Local;
import lk.jiat.global.entity.Warehouse;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@Local
public interface WarehouseServiceLocal {

    // CRUD Operations
    Warehouse createWarehouse(String name, String location, String country, Integer capacity)
            throws ValidationException, DuplicateResourceException;

    Warehouse updateWarehouse(Integer warehouseId, String name, String location, String country, Integer capacity)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException;

    Optional<Warehouse> findWarehouseById(Integer warehouseId);

    List<Warehouse> findAllWarehouses();

    List<Warehouse> findWarehousesByCountry(String country);

    List<Warehouse> findWarehousesByMinCapacity(Integer minCapacity);

    void deleteWarehouse(Integer warehouseId) throws ResourceNotFoundException;

    // Validation
    boolean isWarehouseNameUnique(String name, Integer excludeId);
}