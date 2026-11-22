package lk.jiat.global.service;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lk.jiat.global.dao.CarrierDAO;
import lk.jiat.global.entity.Carrier;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;
import lk.jiat.global.interceptor.AuditLogged;
import lk.jiat.global.interceptor.ExceptionLogged;
import lk.jiat.global.util.ServiceType;

import java.util.List;
import java.util.Optional;

@Stateless
@AuditLogged(skipReadOnly = true)
@ExceptionLogged
@PermitAll
public class CarrierService implements CarrierServiceLocal {

    @Inject
    private CarrierDAO carrierDAO;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Carrier createCarrier(String carrierName, String serviceType, String contactInfo)
            throws ValidationException, DuplicateResourceException {

        if (carrierName == null || carrierName.trim().isEmpty()) {
            throw new ValidationException("Carrier name is required");
        }

        if (!isCarrierNameUnique(carrierName.trim(), null)) {
            throw new DuplicateResourceException("Carrier with name '" + carrierName + "' already exists");
        }

        Carrier carrier = new Carrier();
        carrier.setCarrierName(carrierName.trim());

        if (serviceType != null) {
            carrier.setServiceType(serviceType.trim());
        }

        carrier.setContactInfo(contactInfo != null ? contactInfo.trim() : null);

        carrierDAO.create(carrier);
        return carrier;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Carrier updateCarrier(Integer carrierId, String carrierName, String serviceType, String contactInfo)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException {

        Carrier carrier = carrierDAO.findById(carrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with ID: " + carrierId));

        if (carrierName != null && !carrierName.trim().isEmpty()) {
            if (!isCarrierNameUnique(carrierName.trim(), carrierId)) {
                throw new DuplicateResourceException("Carrier with name '" + carrierName + "' already exists");
            }
            carrier.setCarrierName(carrierName.trim());
        }
        if (serviceType != null && !serviceType.trim().isEmpty()) {
            ServiceType validType = ServiceType.fromDisplayName(serviceType.trim());
            if (validType == null) {
                throw new ValidationException("Invalid service type: " + serviceType);
            }
            carrier.setServiceType(validType.getDisplayName());
        }
        if (contactInfo != null) {
            carrier.setContactInfo(contactInfo.trim());
        }

        return carrierDAO.update(carrier);
    }

    @Override
    public Optional<Carrier> findCarrierById(Integer carrierId) {
        return carrierDAO.findById(carrierId);
    }

    @Override
    public List<Carrier> findAllCarriers() {
        return carrierDAO.findAll();
    }

    @Override
    public List<Carrier> findCarriersByServiceType(String serviceType) {
        return carrierDAO.findByServiceType(serviceType);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteCarrier(Integer carrierId) throws ResourceNotFoundException {
        Carrier carrier = carrierDAO.findById(carrierId)
                .orElseThrow(() -> new ResourceNotFoundException("Carrier not found with ID: " + carrierId));
        carrierDAO.delete(carrier);
    }

    @Override
    @PermitAll
    public boolean isCarrierNameUnique(String name, Integer excludeId) {
        List<Carrier> all = carrierDAO.findAll();
        return all.stream()
                .filter(c -> excludeId == null || !c.getCarrierId().equals(excludeId))
                .noneMatch(c -> c.getCarrierName().equalsIgnoreCase(name));
    }
}