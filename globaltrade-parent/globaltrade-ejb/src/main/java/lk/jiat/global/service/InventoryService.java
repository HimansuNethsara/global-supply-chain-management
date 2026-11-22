package lk.jiat.global.service;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lk.jiat.global.dao.InventoryItemDAO;
import lk.jiat.global.dao.WarehouseDAO;
import lk.jiat.global.entity.InventoryItem;
import lk.jiat.global.entity.Warehouse;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.interceptor.AuditLogged;
import lk.jiat.global.interceptor.ExceptionLogged;
import lk.jiat.global.interceptor.PerformanceLogged;
import lk.jiat.global.interceptor.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
@Validated
@AuditLogged(skipReadOnly = true)
@ExceptionLogged
public class InventoryService implements InventoryServiceLocal {

    @Inject
    private InventoryItemDAO inventoryItemDAO;

    @Inject
    private WarehouseDAO warehouseDAO;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @PerformanceLogged(threshold = 300)
    public InventoryItem createItem(String sku, String name, Integer warehouseId,
                                    Integer quantity, Integer reorderLevel, BigDecimal unitPrice)
            throws ValidationException, DuplicateResourceException, ResourceNotFoundException {

        if (!isSkuUnique(sku.trim(), null)) {
            throw new DuplicateResourceException("Item with SKU '" + sku + "' already exists");
        }

        Warehouse warehouse = warehouseDAO.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId));

        InventoryItem item = new InventoryItem();
        item.setSku(sku.trim().toUpperCase());
        item.setName(name.trim());
        item.setWarehouse(warehouse);
        item.setQuantity(quantity);
        item.setReorderLevel(reorderLevel != null ? reorderLevel : 10);
        item.setUnitPrice(unitPrice);
        item.setLastUpdated(LocalDateTime.now());

        inventoryItemDAO.create(item);
        return item;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public InventoryItem updateItem(Integer itemId, String sku, String name, Integer warehouseId,
                                    Integer quantity, Integer reorderLevel, BigDecimal unitPrice)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException {

        InventoryItem item = inventoryItemDAO.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

        if (sku != null && !sku.trim().isEmpty()) {
            if (!isSkuUnique(sku.trim(), itemId)) {
                throw new DuplicateResourceException("Item with SKU '" + sku + "' already exists");
            }
            item.setSku(sku.trim().toUpperCase());
        }
        if (name != null && !name.trim().isEmpty()) {
            item.setName(name.trim());
        }
        if (warehouseId != null) {
            Warehouse warehouse = warehouseDAO.findById(warehouseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId));
            item.setWarehouse(warehouse);
        }
        if (quantity != null && quantity >= 0) {
            item.setQuantity(quantity);
        }
        if (reorderLevel != null && reorderLevel >= 0) {
            item.setReorderLevel(reorderLevel);
        }
        if (unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) >= 0) {
            item.setUnitPrice(unitPrice);
        }
        item.setLastUpdated(LocalDateTime.now());

        return inventoryItemDAO.update(item);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteItem(Integer itemId) throws ResourceNotFoundException {
        InventoryItem item = inventoryItemDAO.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));
        inventoryItemDAO.delete(item);
    }

    @Override
    public List<InventoryItem> findLowStockItems() {
        return inventoryItemDAO.findLowStockItems();
    }

    @Override
    public Optional<InventoryItem> findItemById(Integer itemId) {
        return inventoryItemDAO.findById(itemId);
    }

    @Override
    public Optional<InventoryItem> findItemBySku(String sku) {
        return inventoryItemDAO.findBySku(sku);
    }

    @Override
    public List<InventoryItem> findAllItems() {
        return inventoryItemDAO.findAll();
    }

    @Override
    public List<InventoryItem> findItemsByWarehouse(Integer warehouseId) {
        return inventoryItemDAO.findByWarehouseId(warehouseId);
    }

    @Override
    @PermitAll
    public boolean isSkuUnique(String sku, Integer excludeId) {
        Optional<InventoryItem> existing = inventoryItemDAO.findBySku(sku);
        if (existing.isEmpty()) {
            return true;
        }
        if (excludeId != null && existing.get().getItemId().equals(excludeId)) {
            return true;
        }
        return false;
    }
}