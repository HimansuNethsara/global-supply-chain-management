package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.Carrier;

import java.util.List;

@Stateless
public class CarrierDAO extends GenericDAO<Carrier, Integer> {

    public CarrierDAO() {
        super(Carrier.class);
    }

    public List<Carrier> findByServiceType(String serviceType) {
        return em.createQuery("SELECT c FROM Carrier c WHERE c.serviceType = :serviceType", Carrier.class)
                .setParameter("serviceType", serviceType)
                .getResultList();
    }
}