package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.SystemTimer;

import java.util.List;
import java.util.Optional;

@Stateless
public class SystemTimerDAO extends GenericDAO<SystemTimer, Integer> {

    public SystemTimerDAO() {
        super(SystemTimer.class);
    }

    public Optional<SystemTimer> findByTimerName(String timerName) {
        try {
            SystemTimer timer = em.createQuery("SELECT t FROM SystemTimer t WHERE t.timerName = :timerName", SystemTimer.class)
                    .setParameter("timerName", timerName)
                    .getSingleResult();
            return Optional.ofNullable(timer);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<SystemTimer> findByStatus(String status) {
        return em.createQuery("SELECT t FROM SystemTimer t WHERE t.status = :status", SystemTimer.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<SystemTimer> findByType(String timerType) {
        return em.createQuery("SELECT t FROM SystemTimer t WHERE t.timerType = :timerType", SystemTimer.class)
                .setParameter("timerType", timerType)
                .getResultList();
    }

    public List<SystemTimer> findTimersNeedingRun() {
        return em.createQuery("SELECT t FROM SystemTimer t WHERE t.status = 'ACTIVE' AND (t.nextRun IS NULL OR t.nextRun <= CURRENT_TIMESTAMP)",
                        SystemTimer.class)
                .getResultList();
    }
}