package lk.jiat.global.service;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Local;
import lk.jiat.global.entity.Alert;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;

import java.util.List;

@Local
public interface AlertServiceLocal {

    Alert createAlert(String alertType, String severity, String message) throws ValidationException;

    Alert createAlert(String alertType, String entityType, Integer entityId,
                      String severity, String message) throws ValidationException;

    List<Alert> getActiveAlerts();

    List<Alert> getAlertsByType(String alertType);

    List<Alert> getAlertsBySeverity(String severity);

    Alert resolveAlert(Integer alertId) throws ResourceNotFoundException, ValidationException;

    int resolveAllAlerts();

    Alert createOrUpdateAlert(String alertType, String entityType, Integer entityId,
                              String severity, String message) throws ValidationException;

    void deleteAlert(Integer alertId) throws ResourceNotFoundException;

    long getActiveAlertCount();

    long getAlertCountBySeverity(String severity);

    void cleanupResolvedAlerts(int daysOld);
}