package lk.jiat.global.service;

import jakarta.ejb.Local;
import lk.jiat.global.entity.VendorPerformanceEvaluation;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Local
public interface VendorPerformanceEvaluationServiceLocal {

    VendorPerformanceEvaluation createEvaluation(Integer vendorId, Integer evaluatorId,
                                                 BigDecimal score, LocalDate evaluationDate,
                                                 String comments)
            throws ResourceNotFoundException, ValidationException;

    List<VendorPerformanceEvaluation> getEvaluationsForVendor(Integer vendorId)
            throws ResourceNotFoundException;

    BigDecimal getAverageRating(Integer vendorId) throws ResourceNotFoundException;

    void recalculateVendorRating(Integer vendorId) throws ResourceNotFoundException;

    List<Object[]> findVendorsWithLowRating(BigDecimal threshold);

    void deleteEvaluation(Integer evalId) throws ResourceNotFoundException;
}