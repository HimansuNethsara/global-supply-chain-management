package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.OrderItem;

import java.util.List;

@Stateless
public class OrderItemDAO extends GenericDAO<OrderItem, Long> {

    public OrderItemDAO() {
        super(OrderItem.class);
    }

    public List<OrderItem> findByOrderId(Integer orderId) {
        return em.createQuery(
                        "SELECT oi FROM OrderItem oi " +
                                "JOIN FETCH oi.item " +
                                "JOIN FETCH oi.item.warehouse " +
                                "WHERE oi.order.orderId = :orderId", OrderItem.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderItem> findByItemId(Integer itemId) {
        return em.createQuery(
                        "SELECT oi FROM OrderItem oi " +
                                "WHERE oi.item.itemId = :itemId", OrderItem.class)
                .setParameter("itemId", itemId)
                .getResultList();
    }

    public int getTotalItemsForOrder(Integer orderId) {
        try {
            Long count = em.createQuery(
                            "SELECT SUM(oi.quantity) FROM OrderItem oi " +
                                    "WHERE oi.order.orderId = :orderId", Long.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public int countByOrderId(Integer orderId) {
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(oi) FROM OrderItem oi " +
                                    "WHERE oi.order.orderId = :orderId", Long.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}