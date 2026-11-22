package lk.jiat.global.service;

import jakarta.ejb.Local;
import lk.jiat.global.entity.CustomsDocument;
import lk.jiat.global.exception.BusinessException;
import lk.jiat.global.exception.CustomsDocumentRejectedException;  // ADD THIS
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@Local
public interface CustomsServiceLocal {

    // Create document - LOGISTICS_COORDINATOR only
    CustomsDocument createDocument(Integer shipmentId, String documentType, Integer createdByUserId)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException;

    // Submit document - LOGISTICS_COORDINATOR only
    CustomsDocument submitDocument(Integer docId, Integer submittedByUserId)
            throws ResourceNotFoundException, ValidationException, BusinessException;

    // Review document - CUSTOMS_AGENT only
    CustomsDocument reviewDocument(Integer docId, String decision, String remarks, Integer reviewerId)
            throws ResourceNotFoundException, ValidationException, BusinessException, CustomsDocumentRejectedException;

    // Read operations
    Optional<CustomsDocument> findDocumentById(Integer docId);

    List<CustomsDocument> findAllDocuments();

    List<CustomsDocument> findDocumentsByShipment(Integer shipmentId);

    List<CustomsDocument> findDocumentsByStatus(String status);

    List<CustomsDocument> findDocumentsByAgent(Integer agentId);

    // Delete - LOGISTICS_COORDINATOR only
    void deleteDocument(Integer docId) throws ResourceNotFoundException, BusinessException;

    // Validation
    boolean isValidStatusTransition(String currentStatus, String newStatus);
}