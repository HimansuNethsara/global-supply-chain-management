package lk.jiat.global.service;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Local;
import lk.jiat.global.entity.SystemTimer;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@Local
public interface SystemTimerServiceLocal {

    @PermitAll
    void registerTimerExecution(String timerName, String status, String errorMessage);

    @PermitAll
    Optional<SystemTimer> getTimerByName(String timerName);

    @PermitAll
    List<SystemTimer> getAllTimers();

    @PermitAll
    List<SystemTimer> getActiveTimers();

    SystemTimer createTimer(String timerName, String timerType, String scheduleExpression)
            throws ValidationException, DuplicateResourceException;

    SystemTimer updateTimerStatus(Integer timerId, String status)
            throws ResourceNotFoundException, ValidationException;

    void updateLastRun(Integer timerId) throws ResourceNotFoundException;

    @PermitAll
    List<SystemTimer> getTimersForMonitoring();
}