package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.Alert;

import java.util.List;

@Stateless
public class AlertDAO extends GenericDAO<Alert, Integer> {

    public AlertDAO() {
        super(Alert.class);
    }

    public List<Alert> findByAlertType(String alertType) {
        return em.createQuery("SELECT a FROM Alert a WHERE a.alertType = :alertType ORDER BY a.createdAt DESC", Alert.class)
                .setParameter("alertType", alertType)
                .getResultList();
    }

    public List<Alert> findBySeverity(String severity) {
        return em.createQuery("SELECT a FROM Alert a WHERE a.severity = :severity ORDER BY a.createdAt DESC", Alert.class)
                .setParameter("severity", severity)
                .getResultList();
    }

    public List<Alert> findByEntity(String entityType, Integer entityId) {
        return em.createQuery("SELECT a FROM Alert a WHERE a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.createdAt DESC",
                        Alert.class)
                .setParameter("entityType", entityType)
                .setParameter("entityId", entityId)
                .getResultList();
    }

    public List<Alert> findUnresolved() {
        return em.createQuery("SELECT a FROM Alert a WHERE a.resolved = false ORDER BY a.createdAt DESC", Alert.class)
                .getResultList();
    }

    public List<Alert> findUnresolvedBySeverity(String severity) {
        return em.createQuery("SELECT a FROM Alert a WHERE a.resolved = false AND a.severity = :severity ORDER BY a.createdAt DESC",
                        Alert.class)
                .setParameter("severity", severity)
                .getResultList();
    }
}