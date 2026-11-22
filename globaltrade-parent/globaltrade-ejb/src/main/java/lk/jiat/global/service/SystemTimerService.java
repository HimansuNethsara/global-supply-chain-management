package lk.jiat.global.service;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lk.jiat.global.dao.SystemTimerDAO;
import lk.jiat.global.entity.SystemTimer;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.interceptor.ExceptionLogged;
import lk.jiat.global.util.AppLogger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
@ExceptionLogged
public class SystemTimerService implements SystemTimerServiceLocal {

    @Inject
    private SystemTimerDAO systemTimerDAO;

    @Override
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerTimerExecution(String timerName, String status, String errorMessage) {
        try {
            Optional<SystemTimer> timerOpt = systemTimerDAO.findByTimerName(timerName);
            SystemTimer timer;
            if (timerOpt.isPresent()) {
                timer = timerOpt.get();
                timer.setLastRun(LocalDateTime.now());
                timer.setStatus(status);
                timer.setNextRun(LocalDateTime.now().plusMinutes(1));
                systemTimerDAO.update(timer);
            } else {
                timer = new SystemTimer(timerName, "DECLARATIVE", "Schedule");
                timer.setLastRun(LocalDateTime.now());
                timer.setStatus(status);
                timer.setNextRun(LocalDateTime.now().plusMinutes(1));
                systemTimerDAO.create(timer);
            }
            AppLogger.info("Timer {} registered with status: {}", timerName, status);
        } catch (Exception e) {
            AppLogger.error("Failed to register timer {}: {}", timerName, e.getMessage());
        }
    }

    @Override
    @PermitAll
    public Optional<SystemTimer> getTimerByName(String timerName) {
        return systemTimerDAO.findByTimerName(timerName);
    }

    @Override
    @PermitAll
    public List<SystemTimer> getAllTimers() {
        return systemTimerDAO.findAll();
    }

    @Override
    @PermitAll
    public List<SystemTimer> getActiveTimers() {
        return systemTimerDAO.findByStatus("ACTIVE");
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SystemTimer createTimer(String timerName, String timerType, String scheduleExpression)
            throws ValidationException, DuplicateResourceException {

        if (timerName == null || timerName.trim().isEmpty()) {
            throw new ValidationException("Timer name is required");
        }
        if (timerType == null || timerType.trim().isEmpty()) {
            throw new ValidationException("Timer type is required");
        }

        if (systemTimerDAO.findByTimerName(timerName.trim()).isPresent()) {
            throw new DuplicateResourceException("Timer with name '" + timerName + "' already exists");
        }

        SystemTimer timer = new SystemTimer();
        timer.setTimerName(timerName.trim());
        timer.setTimerType(timerType.trim());
        timer.setScheduleExpression(scheduleExpression);
        timer.setStatus("ACTIVE");
        timer.setLastRun(null);
        timer.setNextRun(null);

        systemTimerDAO.create(timer);
        return timer;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SystemTimer updateTimerStatus(Integer timerId, String status)
            throws ResourceNotFoundException, ValidationException {

        if (status == null || status.trim().isEmpty()) {
            throw new ValidationException("Status is required");
        }
        if (!"ACTIVE".equals(status) && !"PAUSED".equals(status) && !"FAILED".equals(status)) {
            throw new ValidationException("Invalid status: " + status);
        }

        SystemTimer timer = systemTimerDAO.findById(timerId)
                .orElseThrow(() -> new ResourceNotFoundException("Timer not found with ID: " + timerId));

        timer.setStatus(status);
        return systemTimerDAO.update(timer);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateLastRun(Integer timerId) throws ResourceNotFoundException {
        SystemTimer timer = systemTimerDAO.findById(timerId)
                .orElseThrow(() -> new ResourceNotFoundException("Timer not found with ID: " + timerId));
        timer.setLastRun(LocalDateTime.now());
        systemTimerDAO.update(timer);
    }

    @Override
    public List<SystemTimer> getTimersForMonitoring() {
        return systemTimerDAO.findTimersNeedingRun();
    }
}