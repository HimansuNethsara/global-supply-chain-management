package lk.jiat.global.service;

import jakarta.ejb.Local;
import lk.jiat.global.entity.Carrier;
import lk.jiat.global.exception.DuplicateResourceException;
import lk.jiat.global.exception.ResourceNotFoundException;
import lk.jiat.global.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@Local
public interface CarrierServiceLocal {

    Carrier createCarrier(String carrierName, String serviceType, String contactInfo)
            throws ValidationException, DuplicateResourceException;

    Carrier updateCarrier(Integer carrierId, String carrierName, String serviceType, String contactInfo)
            throws ResourceNotFoundException, ValidationException, DuplicateResourceException;

    void deleteCarrier(Integer carrierId) throws ResourceNotFoundException;

    Optional<Carrier> findCarrierById(Integer carrierId);

    List<Carrier> findAllCarriers();

    List<Carrier> findCarriersByServiceType(String serviceType);

    boolean isCarrierNameUnique(String name, Integer excludeId);
}