package lk.jiat.global.service;

import jakarta.ejb.Local;
import lk.jiat.global.entity.InventoryItem;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Local
public interface InventoryServiceLocal {

    // CRUD Operations
    InventoryItem createItem(String sku, String name, Integer warehouseId,
                             Integer quantity, Integer reorderLevel, BigDecimal unitPrice)
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException;

    InventoryItem updateItem(Integer itemId, String sku, String name, Integer warehouseId,
                             Integer quantity, Integer reorderLevel, BigDecimal unitPrice)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException;

    Optional<InventoryItem> findItemById(Integer itemId);

    Optional<InventoryItem> findItemBySku(String sku);

    List<InventoryItem> findAllItems();

    List<InventoryItem> findItemsByWarehouse(Integer warehouseId);

    List<InventoryItem> findLowStockItems();

    void deleteItem(Integer itemId) throws ResourceNotFoundException;

    // Validation
    boolean isSkuUnique(String sku, Integer excludeId);
}