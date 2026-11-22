package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.InventoryItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Stateless
public class InventoryItemDAO extends GenericDAO<InventoryItem, Integer> {

    public InventoryItemDAO() {
        super(InventoryItem.class);
    }

    @Override
    public Optional<InventoryItem> findById(Integer id) {
        try {
            InventoryItem item = em.createQuery(
                            "SELECT i FROM InventoryItem i " +
                                    "JOIN FETCH i.warehouse " +
                                    "WHERE i.itemId = :id", InventoryItem.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.ofNullable(item);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<InventoryItem> findBySku(String sku) {
        try {
            InventoryItem item = em.createQuery(
                            "SELECT i FROM InventoryItem i " +
                                    "JOIN FETCH i.warehouse " +
                                    "WHERE i.sku = :sku", InventoryItem.class)
                    .setParameter("sku", sku)
                    .getSingleResult();
            return Optional.ofNullable(item);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<InventoryItem> findAll() {
        return em.createQuery(
                        "SELECT i FROM InventoryItem i " +
                                "JOIN FETCH i.warehouse", InventoryItem.class)
                .getResultList();
    }

    public List<InventoryItem> findByWarehouseId(Integer warehouseId) {
        return em.createQuery(
                        "SELECT i FROM InventoryItem i " +
                                "JOIN FETCH i.warehouse " +
                                "WHERE i.warehouse.warehouseId = :warehouseId", InventoryItem.class)
                .setParameter("warehouseId", warehouseId)
                .getResultList();
    }

    public List<InventoryItem> findLowStockItems() {
        return em.createQuery(
                        "SELECT i FROM InventoryItem i " +
                                "JOIN FETCH i.warehouse " +
                                "WHERE i.quantity <= i.reorderLevel", InventoryItem.class)
                .getResultList();
    }

    public List<InventoryItem> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return em.createQuery(
                        "SELECT i FROM InventoryItem i " +
                                "JOIN FETCH i.warehouse " +
                                "WHERE i.unitPrice BETWEEN :minPrice AND :maxPrice", InventoryItem.class)
                .setParameter("minPrice", minPrice)
                .setParameter("maxPrice", maxPrice)
                .getResultList();
    }
}