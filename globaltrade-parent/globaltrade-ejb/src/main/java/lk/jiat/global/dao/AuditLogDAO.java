package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class AuditLogDAO extends GenericDAO<AuditLog, Long> {

    public AuditLogDAO() {
        super(AuditLog.class);
    }

    @Override
    public List<AuditLog> findAll() {
        // Eagerly fetch the user to avoid LazyInitializationException
        return em.createQuery(
                        "SELECT a FROM AuditLog a " +
                                "LEFT JOIN FETCH a.user " +
                                "ORDER BY a.loggedAt DESC",
                        AuditLog.class)
                .getResultList();
    }

    public List<AuditLog> findByUserId(Integer userId) {
        return em.createQuery(
                        "SELECT a FROM AuditLog a " +
                                "LEFT JOIN FETCH a.user " +
                                "WHERE a.user.userId = :userId " +
                                "ORDER BY a.loggedAt DESC",
                        AuditLog.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<AuditLog> findByEntity(String entityType, Integer entityId) {
        return em.createQuery(
                        "SELECT a FROM AuditLog a " +
                                "LEFT JOIN FETCH a.user " +
                                "WHERE a.entityType = :entityType AND a.entityId = :entityId " +
                                "ORDER BY a.loggedAt DESC",
                        AuditLog.class)
                .setParameter("entityType", entityType)
                .setParameter("entityId", entityId)
                .getResultList();
    }

    public List<AuditLog> findByAction(String action) {
        return em.createQuery(
                        "SELECT a FROM AuditLog a " +
                                "LEFT JOIN FETCH a.user " +
                                "WHERE a.action LIKE :action " +
                                "ORDER BY a.loggedAt DESC",
                        AuditLog.class)
                .setParameter("action", "%" + action + "%")
                .getResultList();
    }

    public List<AuditLog> findLogsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return em.createQuery(
                        "SELECT a FROM AuditLog a " +
                                "LEFT JOIN FETCH a.user " +
                                "WHERE a.loggedAt BETWEEN :startDate AND :endDate " +
                                "ORDER BY a.loggedAt DESC",
                        AuditLog.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
}