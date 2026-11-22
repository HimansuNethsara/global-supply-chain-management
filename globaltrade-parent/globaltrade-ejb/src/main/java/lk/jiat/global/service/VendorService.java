package lk.jiat.global.service;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lk.jiat.global.dao.VendorDAO;
import lk.jiat.global.dao.VendorPerformanceEvaluationDAO;
import lk.jiat.global.dao.UserDAO;
import lk.jiat.global.entity.Vendor;
import lk.jiat.global.entity.VendorPerformanceEvaluation;
import lk.jiat.global.entity.User;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.exception.VendorSuspendedException;
import lk.jiat.global.interceptor.AuditLogged;
import lk.jiat.global.interceptor.ExceptionLogged;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Stateless
@AuditLogged(skipReadOnly = true)
@ExceptionLogged
public class VendorService implements VendorServiceLocal {

    @Inject
    private VendorDAO vendorDAO;

    @Inject
    private VendorPerformanceEvaluationDAO evaluationDAO;

    @Inject
    private UserDAO userDAO;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Vendor createVendor(String vendorName, String contactEmail, String contactPhone,
                               String country, String status)
            throws ValidationException, DuplicateResourceException {

        if (vendorName == null || vendorName.trim().isEmpty()) {
            throw new ValidationException("Vendor name is required");
        }
        if (contactEmail == null || contactEmail.trim().isEmpty()) {
            throw new ValidationException("Contact email is required");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new ValidationException("Country is required");
        }

        if (vendorDAO.findByContactEmail(contactEmail.trim()).isPresent()) {
            throw new DuplicateResourceException("Vendor with email '" + contactEmail + "' already exists");
        }

        Vendor vendor = new Vendor();
        vendor.setVendorName(vendorName.trim());
        vendor.setContactEmail(contactEmail.trim());
        vendor.setContactPhone(contactPhone != null ? contactPhone.trim() : null);
        vendor.setCountry(country.trim());
        vendor.setStatus(status != null ? status : "ACTIVE");
        vendor.setCreatedAt(LocalDateTime.now());

        vendorDAO.create(vendor);
        return vendor;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Vendor updateVendor(Integer vendorId, String vendorName, String contactEmail,
                               String contactPhone, String country, String status)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException, VendorSuspendedException {

        Vendor vendor = vendorDAO.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));

        if (vendorName != null && !vendorName.trim().isEmpty()) {
            vendor.setVendorName(vendorName.trim());
        }
        if (contactEmail != null && !contactEmail.trim().isEmpty()) {
            Optional<Vendor> existing = vendorDAO.findByContactEmail(contactEmail.trim());
            if (existing.isPresent() && !existing.get().getVendorId().equals(vendorId)) {
                throw new DuplicateResourceException("Vendor with email '" + contactEmail + "' already exists");
            }
            vendor.setContactEmail(contactEmail.trim());
        }
        if (contactPhone != null) {
            vendor.setContactPhone(contactPhone.trim());
        }
        if (country != null && !country.trim().isEmpty()) {
            vendor.setCountry(country.trim());
        }
        if (status != null && !status.trim().isEmpty()) {
            if ("SUSPENDED".equals(vendor.getStatus()) && !"ACTIVE".equals(status)) {
                throw new VendorSuspendedException(
                        "Vendor '" + vendor.getVendorName() + "' is suspended. " +
                                "Only Logistics Coordinators can reactivate suspended vendors."
                );
            }
            vendor.setStatus(status.trim());
        }

        return vendorDAO.update(vendor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteVendor(Integer vendorId) throws ResourceNotFoundException {
        Vendor vendor = vendorDAO.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));

        List<User> users = userDAO.findByVendorId(vendorId);
        for (User user : users) {
            user.setVendor(null);
            user.setStatus("DISABLED");
            userDAO.update(user);
        }

        vendorDAO.delete(vendor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public VendorPerformanceEvaluation evaluateVendor(Integer vendorId, Integer evaluatorId,
                                                      BigDecimal score, LocalDate evaluationDate, String comments)
            throws ResourceNotFoundException, ValidationException {

        Vendor vendor = vendorDAO.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));

        User evaluator = userDAO.findById(evaluatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluator not found with ID: " + evaluatorId));

        if (score == null || score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("5.0")) > 0) {
            throw new ValidationException("Score must be between 0.0 and 5.0");
        }

        VendorPerformanceEvaluation evaluation = new VendorPerformanceEvaluation();
        evaluation.setVendor(vendor);
        evaluation.setEvaluator(evaluator);
        evaluation.setScore(score.setScale(2, RoundingMode.HALF_UP));
        evaluation.setEvaluationDate(evaluationDate != null ? evaluationDate : LocalDate.now());
        evaluation.setComments(comments);

        evaluationDAO.create(evaluation);
        updateVendorRating(vendorId);

        return evaluation;
    }

    @Override
    public List<VendorPerformanceEvaluation> getVendorEvaluations(Integer vendorId)
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
        return evaluationDAO.getAverageScoreForVendor(vendorId);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateVendorRating(Integer vendorId) throws ResourceNotFoundException {
        Vendor vendor = vendorDAO.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));

        BigDecimal avgRating = evaluationDAO.getAverageScoreForVendor(vendorId);
        vendor.setPerformanceRating(avgRating != null ? avgRating.setScale(2, RoundingMode.HALF_UP) : null);
        vendorDAO.update(vendor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void activateVendor(Integer vendorId) throws ResourceNotFoundException {
        Vendor vendor = vendorDAO.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));
        vendor.setStatus("ACTIVE");
        vendorDAO.update(vendor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void suspendVendor(Integer vendorId) throws ResourceNotFoundException {
        Vendor vendor = vendorDAO.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));
        vendor.setStatus("SUSPENDED");
        vendorDAO.update(vendor);
    }

    @Override
    public Optional<Vendor> findVendorById(Integer vendorId) {
        return vendorDAO.findById(vendorId);
    }

    @Override
    public Optional<Vendor> findVendorByEmail(String email) {
        return vendorDAO.findByContactEmail(email);
    }

    @Override
    public List<Vendor> findAllVendors() {
        return vendorDAO.findAll();
    }

    @Override
    public List<Vendor> findVendorsByCountry(String country) {
        return vendorDAO.findByCountry(country);
    }

    @Override
    public List<Vendor> findActiveVendors() {
        return vendorDAO.findByStatus("ACTIVE");
    }

    @Override
    public List<Vendor> findVendorsByRating(BigDecimal minRating) {
        return vendorDAO.findByPerformanceRatingGreaterThan(minRating);
    }

    @Override
    @PermitAll
    public boolean isEmailUnique(String email, Integer excludeVendorId) {
        Optional<Vendor> existing = vendorDAO.findByContactEmail(email);
        if (existing.isEmpty()) {
            return true;
        }
        if (excludeVendorId != null && existing.get().getVendorId().equals(excludeVendorId)) {
            return true;
        }
        return false;
    }
}