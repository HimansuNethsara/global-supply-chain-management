package lk.jiat.global.service;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lk.jiat.global.dao.AlertDAO;
import lk.jiat.global.entity.Alert;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.interceptor.ExceptionLogged;
import lk.jiat.global.util.AppLogger;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
@ExceptionLogged
@PermitAll
public class AlertService implements AlertServiceLocal {

    @Inject
    private AlertDAO alertDAO;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Alert createAlert(String alertType, String severity, String message) throws ValidationException {
        return createAlert(alertType, null, null, severity, message);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Alert createAlert(String alertType, String entityType, Integer entityId,
                             String severity, String message) throws ValidationException {

        if (alertType == null || alertType.trim().isEmpty()) {
            throw new ValidationException("Alert type is required");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new ValidationException("Message is required");
        }

        Alert alert = new Alert();
        alert.setAlertType(alertType);
        alert.setEntityType(entityType);
        alert.setEntityId(entityId);
        alert.setSeverity(severity != null ? severity : "MEDIUM");
        alert.setMessage(message);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setResolved(false);

        alertDAO.create(alert);
        AppLogger.info("Alert created: {} - {}", alertType, message);
        return alert;
    }

    @Override
    public List<Alert> getActiveAlerts() {
        return alertDAO.findUnresolved();
    }

    @Override
    public List<Alert> getAlertsByType(String alertType) {
        return alertDAO.findByAlertType(alertType);
    }

    @Override
    public List<Alert> getAlertsBySeverity(String severity) {
        return alertDAO.findBySeverity(severity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Alert resolveAlert(Integer alertId) throws ResourceNotFoundException, ValidationException {
        Alert alert = alertDAO.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with ID: " + alertId));

        if (alert.getResolved()) {
            throw new ValidationException("Alert is already resolved");
        }

        alert.setResolved(true);
        alertDAO.update(alert);
        return alert;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int resolveAllAlerts() {
        List<Alert> activeAlerts = alertDAO.findUnresolved();
        for (Alert alert : activeAlerts) {
            alert.setResolved(true);
            alertDAO.update(alert);
        }
        AppLogger.info("Resolved {} alerts", activeAlerts.size());
        return activeAlerts.size();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Alert createOrUpdateAlert(String alertType, String entityType, Integer entityId,
                                     String severity, String message) throws ValidationException {

        if (entityType != null && entityId != null) {
            List<Alert> existingAlerts = alertDAO.findByEntity(entityType, entityId);

            for (Alert existing : existingAlerts) {
                if (!existing.getResolved() && existing.getAlertType().equals(alertType)) {
                    existing.setCreatedAt(LocalDateTime.now());
                    existing.setMessage(message);
                    existing.setSeverity(severity != null ? severity : existing.getSeverity());
                    alertDAO.update(existing);
                    AppLogger.info("Updated existing alert: {} - {}", alertType, message);
                    return existing;
                }
            }
        }

        return createAlert(alertType, entityType, entityId, severity, message);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteAlert(Integer alertId) throws ResourceNotFoundException {
        Alert alert = alertDAO.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with ID: " + alertId));
        alertDAO.delete(alert);
    }

    @Override
    @PermitAll
    public long getActiveAlertCount() {
        return alertDAO.findUnresolved().size();
    }

    @Override
    public long getAlertCountBySeverity(String severity) {
        return alertDAO.findBySeverity(severity).size();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void cleanupResolvedAlerts(int daysOld) {
        AppLogger.info("Cleaning up resolved alerts older than {} days", daysOld);
    }
}