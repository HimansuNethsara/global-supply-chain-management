package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.VendorPerformanceEvaluation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Stateless
public class VendorPerformanceEvaluationDAO extends GenericDAO<VendorPerformanceEvaluation, Integer> {

    public VendorPerformanceEvaluationDAO() {
        super(VendorPerformanceEvaluation.class);
    }

    public List<VendorPerformanceEvaluation> findByVendorId(Integer vendorId) {
        return em.createQuery(
                        "SELECT e FROM VendorPerformanceEvaluation e " +
                                "JOIN FETCH e.evaluator " +
                                "WHERE e.vendor.vendorId = :vendorId " +
                                "ORDER BY e.evaluationDate DESC",
                        VendorPerformanceEvaluation.class)
                .setParameter("vendorId", vendorId)
                .getResultList();
    }

    public List<VendorPerformanceEvaluation> findByEvaluatorId(Integer evaluatorId) {
        return em.createQuery("SELECT e FROM VendorPerformanceEvaluation e WHERE e.evaluator.userId = :evaluatorId ORDER BY e.evaluationDate DESC",
                        VendorPerformanceEvaluation.class)
                .setParameter("evaluatorId", evaluatorId)
                .getResultList();
    }

    public List<VendorPerformanceEvaluation> findEvaluationsBetween(LocalDate startDate, LocalDate endDate) {
        return em.createQuery("SELECT e FROM VendorPerformanceEvaluation e WHERE e.evaluationDate BETWEEN :startDate AND :endDate ORDER BY e.evaluationDate DESC",
                        VendorPerformanceEvaluation.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    public BigDecimal getAverageScoreForVendor(Integer vendorId) {
        try {
            return em.createQuery(
                            "SELECT COALESCE(AVG(e.score), 0) FROM VendorPerformanceEvaluation e " +
                                    "WHERE e.vendor.vendorId = :vendorId",
                            BigDecimal.class)
                    .setParameter("vendorId", vendorId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Object[]> findVendorsWithLowRating(BigDecimal threshold) {
        try {
            return em.createQuery(
                            "SELECT v.vendorId, v.vendorName, AVG(e.score) " +
                                    "FROM VendorPerformanceEvaluation e " +
                                    "JOIN e.vendor v " +
                                    "GROUP BY v.vendorId, v.vendorName " +
                                    "HAVING AVG(e.score) <= :threshold", Object[].class)
                    .setParameter("threshold", threshold)
                    .getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Object[]> findTopPerformingVendors(int limit) {
        try {
            return em.createQuery(
                            "SELECT v.vendorId, v.vendorName, v.country, AVG(e.score) " +
                                    "FROM VendorPerformanceEvaluation e " +
                                    "JOIN e.vendor v " +
                                    "GROUP BY v.vendorId, v.vendorName, v.country " +
                                    "ORDER BY AVG(e.score) DESC", Object[].class)
                    .setMaxResults(limit)
                    .getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Object[]> findVendorPerformanceWithLastEval() {
        try {
            return em.createQuery(
                            "SELECT v.vendorId, v.vendorName, v.country, AVG(e.score), MAX(e.evaluationDate) " +
                                    "FROM VendorPerformanceEvaluation e " +
                                    "JOIN e.vendor v " +
                                    "GROUP BY v.vendorId, v.vendorName, v.country " +
                                    "ORDER BY AVG(e.score) DESC", Object[].class)
                    .getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }

}