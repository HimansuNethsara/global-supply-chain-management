package lk.jiat.global.service;

import jakarta.ejb.Local;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.entity.VendorPerformanceEvaluation;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.exception.VendorSuspendedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Local
public interface VendorServiceLocal {

    // CRUD Operations
    Vendor createVendor(String vendorName, String contactEmail, String contactPhone,
                        String country, String status)
            throws ValidationException, DuplicateResourceException;

    Vendor updateVendor(Integer vendorId, String vendorName, String contactEmail,
                        String contactPhone, String country, String status)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException, VendorSuspendedException;

    Optional<Vendor> findVendorById(Integer vendorId);

    Optional<Vendor> findVendorByEmail(String email);

    List<Vendor> findAllVendors();

    List<Vendor> findVendorsByCountry(String country);

    List<Vendor> findActiveVendors();

    List<Vendor> findVendorsByRating(BigDecimal minRating);

    void deleteVendor(Integer vendorId) throws ResourceNotFoundException;

    // Performance Evaluations
    VendorPerformanceEvaluation evaluateVendor(Integer vendorId, Integer evaluatorId,
                                               BigDecimal score, LocalDate evaluationDate, String comments)
            throws ResourceNotFoundException, ValidationException;

    List<VendorPerformanceEvaluation> getVendorEvaluations(Integer vendorId)
            throws ResourceNotFoundException;

    BigDecimal getAverageRating(Integer vendorId) throws ResourceNotFoundException;

    void updateVendorRating(Integer vendorId) throws ResourceNotFoundException;

    // Status Management
    void activateVendor(Integer vendorId) throws ResourceNotFoundException;

    void suspendVendor(Integer vendorId) throws ResourceNotFoundException;

    // Validation
    boolean isEmailUnique(String email, Integer excludeVendorId);
}