package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.ExceptionLog;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class ExceptionLogDAO extends GenericDAO<ExceptionLog, Long> {

    public ExceptionLogDAO() {
        super(ExceptionLog.class);
    }

    public List<ExceptionLog> findByExceptionType(String exceptionType) {
        return em.createQuery("SELECT e FROM ExceptionLog e WHERE e.exceptionType = :exceptionType ORDER BY e.occurredAt DESC",
                        ExceptionLog.class)
                .setParameter("exceptionType", exceptionType)
                .getResultList();
    }

    public List<ExceptionLog> findByEntity(String entityType, Integer entityId) {
        return em.createQuery("SELECT e FROM ExceptionLog e WHERE e.entityType = :entityType AND e.entityId = :entityId ORDER BY e.occurredAt DESC",
                        ExceptionLog.class)
                .setParameter("entityType", entityType)
                .setParameter("entityId", entityId)
                .getResultList();
    }

    public List<ExceptionLog> findUnresolved() {
        return em.createQuery("SELECT e FROM ExceptionLog e WHERE e.resolved = false ORDER BY e.occurredAt DESC", ExceptionLog.class)
                .getResultList();
    }

    public List<ExceptionLog> findLogsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return em.createQuery("SELECT e FROM ExceptionLog e WHERE e.occurredAt BETWEEN :startDate AND :endDate ORDER BY e.occurredAt DESC",
                        ExceptionLog.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
}