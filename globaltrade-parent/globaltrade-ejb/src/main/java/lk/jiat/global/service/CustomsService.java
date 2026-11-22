package lk.jiat.global.service;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lk.jiat.global.dao.CustomsDocumentDAO;
import lk.jiat.global.dao.ShipmentDAO;
import lk.jiat.global.dao.UserDAO;
import lk.jiat.global.entity.CustomsDocument;
import lk.jiat.global.entity.Shipment;
import lk.jiat.global.entity.User;
import lk.jiat.global.exception.BusinessException;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.interceptor.ExceptionLogged;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Stateless
@ExceptionLogged
@PermitAll
public class CustomsService implements CustomsServiceLocal {

    private static final Set<String> VALID_STATUSES = new HashSet<>(Arrays.asList(
            "PENDING", "SUBMITTED", "APPROVED", "REJECTED"
    ));

    @Inject
    private CustomsDocumentDAO customsDocumentDAO;

    @Inject
    private ShipmentDAO shipmentDAO;

    @Inject
    private UserDAO userDAO;

    private void validateRole(User user, String... allowedRoles) throws ValidationException {
        if (user == null || user.getRole() == null) {
            throw new ValidationException("User or role information is missing");
        }
        String userRole = user.getRole().getRoleName();
        for (String role : allowedRoles) {
            if (role.equals(userRole)) {
                return;
            }
        }
        throw new ValidationException("User with role '" + userRole + "' is not authorized for this operation. " +
                "Required roles: " + String.join(", ", allowedRoles));
    }

    private User validateAndGetUser(Integer userId, String... allowedRoles) throws ResourceNotFoundException, ValidationException {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        validateRole(user, allowedRoles);
        return user;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CustomsDocument createDocument(Integer shipmentId, String documentType, Integer createdByUserId)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException {

        if (shipmentId == null) {
            throw new ValidationException("Shipment ID is required");
        }
        if (documentType == null || documentType.trim().isEmpty()) {
            throw new ValidationException("Document type is required");
        }
        if (createdByUserId == null) {
            throw new ValidationException("Created by user is required");
        }

        User createdBy = validateAndGetUser(createdByUserId, "LOGISTICS_COORDINATOR");

        Shipment shipment = shipmentDAO.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        // Check if shipment already has this document type
        List<CustomsDocument> existing = customsDocumentDAO.findByShipmentId(shipmentId);
        for (CustomsDocument doc : existing) {
            if (doc.getDocumentType().equalsIgnoreCase(documentType.trim())) {
                throw new DuplicateResourceException("Document type '" + documentType + "' already exists for this shipment");
            }
        }

        CustomsDocument document = new CustomsDocument();
        document.setShipment(shipment);
        document.setDocumentType(documentType.trim());
        document.setStatus("PENDING");
        document.setSubmittedDate(null);
        document.setApprovedDate(null);
        document.setCustomsAgent(null);

        customsDocumentDAO.create(document);
        return document;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CustomsDocument submitDocument(Integer docId, Integer submittedByUserId)
            throws ResourceNotFoundException, ValidationException, BusinessException {

        if (docId == null) {
            throw new ValidationException("Document ID is required");
        }
        if (submittedByUserId == null) {
            throw new ValidationException("Submitted by user is required");
        }

        User submittedBy = validateAndGetUser(submittedByUserId, "LOGISTICS_COORDINATOR");

        CustomsDocument document = customsDocumentDAO.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + docId));

        String currentStatus = document.getStatus();

        if (!"PENDING".equals(currentStatus)) {
            throw new ValidationException("Only PENDING documents can be submitted. Current status: " + currentStatus);
        }

        document.setStatus("SUBMITTED");
        document.setSubmittedDate(LocalDateTime.now());

        return customsDocumentDAO.update(document);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CustomsDocument reviewDocument(Integer docId, String decision, String remarks, Integer reviewerId)
            throws ResourceNotFoundException, ValidationException, BusinessException {

        if (docId == null) {
            throw new ValidationException("Document ID is required");
        }
        if (decision == null || decision.trim().isEmpty()) {
            throw new ValidationException("Decision is required (APPROVED or REJECTED)");
        }
        if (reviewerId == null) {
            throw new ValidationException("Reviewer ID is required");
        }

        String trimmedDecision = decision.trim().toUpperCase();
        if (!"APPROVED".equals(trimmedDecision) && !"REJECTED".equals(trimmedDecision)) {
            throw new ValidationException("Decision must be APPROVED or REJECTED");
        }

        User reviewer = validateAndGetUser(reviewerId, "CUSTOMS_AGENT");

        CustomsDocument document = customsDocumentDAO.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + docId));

        String currentStatus = document.getStatus();

        if (!"SUBMITTED".equals(currentStatus)) {
            throw new ValidationException("Only SUBMITTED documents can be reviewed. Current status: " + currentStatus);
        }

        document.setStatus(trimmedDecision);
        document.setApprovedDate(LocalDateTime.now());
        document.setCustomsAgent(reviewer);

        return customsDocumentDAO.update(document);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteDocument(Integer docId) throws ResourceNotFoundException, BusinessException {

        CustomsDocument document = customsDocumentDAO.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + docId));

        if ("APPROVED".equals(document.getStatus())) {
            throw new BusinessException("Cannot delete an APPROVED document");
        }

        customsDocumentDAO.delete(document);
    }

    @Override
    public Optional<CustomsDocument> findDocumentById(Integer docId) {
        return customsDocumentDAO.findById(docId);
    }

    @Override
    public List<CustomsDocument> findAllDocuments() {
        return customsDocumentDAO.findAll();
    }

    @Override
    public List<CustomsDocument> findDocumentsByShipment(Integer shipmentId) {
        return customsDocumentDAO.findByShipmentId(shipmentId);
    }

    @Override
    public List<CustomsDocument> findDocumentsByStatus(String status) {
        return customsDocumentDAO.findByStatus(status);
    }

    @Override
    public List<CustomsDocument> findDocumentsByAgent(Integer agentId) {
        return customsDocumentDAO.findByAgentId(agentId);
    }

    @Override
    @PermitAll
    public boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }

        // Valid transitions: PENDING -> SUBMITTED, SUBMITTED -> APPROVED/REJECTED
        if ("PENDING".equals(currentStatus) && "SUBMITTED".equals(newStatus)) {
            return true;
        }
        if ("SUBMITTED".equals(currentStatus) && ("APPROVED".equals(newStatus) || "REJECTED".equals(newStatus))) {
            return true;
        }
        return false;
    }
}