package lk.jiat.global.service;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.jiat.global.dao.VendorDAO;
import lk.jiat.global.dao.VendorPerformanceEvaluationDAO;
import lk.jiat.global.dao.UserDAO;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.entity.User;
import lk.jiat.global.entity.VendorPerformanceEvaluation;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.interceptor.ExceptionLogged;
import lk.jiat.global.util.AppLogger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Stateless
@ExceptionLogged
public class VendorPerformanceEvaluationService implements VendorPerformanceEvaluationServiceLocal {

    @Inject
    private VendorPerformanceEvaluationDAO evaluationDAO;

    @Inject
    private VendorDAO vendorDAO;

    @Inject
    private UserDAO userDAO;

    @PersistenceContext(unitName = "SupplyChainPU")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public VendorPerformanceEvaluation createEvaluation(Integer vendorId, Integer evaluatorId,
                                                        BigDecimal score, LocalDate evaluationDate,
                                                        String comments)
            throws ResourceNotFoundException, ValidationException {

        if (vendorId == null) {
            throw new ValidationException("Vendor ID is required");
        }
        if (evaluatorId == null) {
            throw new ValidationException("Evaluator ID is required");
        }
        if (score == null) {
            throw new ValidationException("Score is required");
        }
        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("5.0")) > 0) {
            throw new ValidationException("Score must be between 0.0 and 5.0");
        }

        Vendor vendor = vendorDAO.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));

        User evaluator = userDAO.findById(evaluatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + evaluatorId));

        VendorPerformanceEvaluation evaluation = new VendorPerformanceEvaluation();
        evaluation.setVendor(vendor);
        evaluation.setEvaluator(evaluator);
        evaluation.setScore(score.setScale(2, RoundingMode.HALF_UP));
        evaluation.setEvaluationDate(evaluationDate != null ? evaluationDate : LocalDate.now());
        evaluation.setComments(comments);

        // Save the evaluation
        evaluationDAO.create(evaluation);

        // Calculate average using native query
        BigDecimal avgRating = calculateAverageRating(vendorId);

        // Update rating in a separate transaction
        updateVendorRatingDirectly(vendorId, avgRating);

        AppLogger.info("Vendor evaluation created for {} with score {}", vendor.getVendorName(), score);
        return evaluation;
    }

    /**
     * Calculate average rating using native query
     */
    private BigDecimal calculateAverageRating(Integer vendorId) {
        try {
            String sql = "SELECT AVG(score) FROM vendor_performance_evaluations WHERE vendor_id = ?";
            Object result = em.createNativeQuery(sql)
                    .setParameter(1, vendorId)
                    .getSingleResult();

            if (result == null) {
                return null;
            }

            if (result instanceof BigDecimal) {
                return (BigDecimal) result;
            } else if (result instanceof Double) {
                return BigDecimal.valueOf((Double) result);
            } else if (result instanceof Number) {
                return BigDecimal.valueOf(((Number) result).doubleValue());
            }
            return null;

        } catch (Exception e) {
            AppLogger.warning("Failed to calculate average: {}", e.getMessage());
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateVendorRatingDirectly(Integer vendorId, BigDecimal avgRating) {
        try {
            String sql;
            if (avgRating != null && avgRating.compareTo(BigDecimal.ZERO) > 0) {
                sql = "UPDATE vendors SET performance_rating = ? WHERE vendor_id = ?";
                em.createNativeQuery(sql)
                        .setParameter(1, avgRating.setScale(2, RoundingMode.HALF_UP))
                        .setParameter(2, vendorId)
                        .executeUpdate();
                AppLogger.info("Vendor {} rating updated to {}", vendorId, avgRating);
            } else {
                sql = "UPDATE vendors SET performance_rating = NULL WHERE vendor_id = ?";
                em.createNativeQuery(sql)
                        .setParameter(1, vendorId)
                        .executeUpdate();
                AppLogger.info("Vendor {} rating set to NULL", vendorId);
            }
        } catch (Exception e) {
            AppLogger.error("Failed to update vendor rating: {}", e.getMessage());
            throw new RuntimeException("Failed to update vendor rating", e);
        }
    }

    @Override
    public List<VendorPerformanceEvaluation> getEvaluationsForVendor(Integer vendorId)
            throws ResourceNotFoundException {
        if (!vendorDAO.findById(vendorId).isPresent()) {
            throw new ResourceNotFoundException("Vendor not found with ID: " + vendorId);
        }
        return evaluationDAO.findByVendorId(vendorId);
    }

    @Override
    public BigDecimal getAverageRating(Integer vendorId) throws ResourceNotFoundException {
        if (!vendorDAO.findById(vendorId).isPresent()) {
            throw new ResourceNotFoundException("Vendor not found with ID: " + vendorId);
        }
        return calculateAverageRating(vendorId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void recalculateVendorRating(Integer vendorId) throws ResourceNotFoundException {
        if (!vendorDAO.findById(vendorId).isPresent()) {
            throw new ResourceNotFoundException("Vendor not found with ID: " + vendorId);
        }
        BigDecimal avgRating = calculateAverageRating(vendorId);
        updateVendorRatingDirectly(vendorId, avgRating);
        AppLogger.info("Vendor rating recalculated for {}", vendorId);
    }

    @Override
    public List<Object[]> findVendorsWithLowRating(BigDecimal threshold) {
        return evaluationDAO.findVendorsWithLowRating(threshold);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteEvaluation(Integer evalId) throws ResourceNotFoundException {
        VendorPerformanceEvaluation evaluation = evaluationDAO.findById(evalId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found with ID: " + evalId));
        Integer vendorId = evaluation.getVendor().getVendorId();
        evaluationDAO.delete(evaluation);
        recalculateVendorRating(vendorId);
    }
}