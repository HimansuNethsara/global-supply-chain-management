package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.CustomsDocument;

import java.util.List;
import java.util.Optional;

@Stateless
public class CustomsDocumentDAO extends GenericDAO<CustomsDocument, Integer> {

    public CustomsDocumentDAO() {
        super(CustomsDocument.class);
    }

    @Override
    public Optional<CustomsDocument> findById(Integer id) {
        try {
            CustomsDocument doc = em.createQuery(
                            "SELECT c FROM CustomsDocument c " +
                                    "LEFT JOIN FETCH c.shipment " +
                                    "LEFT JOIN FETCH c.customsAgent " +
                                    "WHERE c.docId = :id", CustomsDocument.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.ofNullable(doc);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<CustomsDocument> findByShipmentId(Integer shipmentId) {
        return em.createQuery(
                        "SELECT c FROM CustomsDocument c " +
                                "LEFT JOIN FETCH c.shipment " +
                                "LEFT JOIN FETCH c.customsAgent " +
                                "WHERE c.shipment.shipmentId = :shipmentId", CustomsDocument.class)
                .setParameter("shipmentId", shipmentId)
                .getResultList();
    }

    public List<CustomsDocument> findByStatus(String status) {
        return em.createQuery(
                        "SELECT c FROM CustomsDocument c " +
                                "LEFT JOIN FETCH c.shipment " +
                                "LEFT JOIN FETCH c.customsAgent " +
                                "WHERE c.status = :status", CustomsDocument.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<CustomsDocument> findByAgentId(Integer agentId) {
        return em.createQuery(
                        "SELECT c FROM CustomsDocument c " +
                                "LEFT JOIN FETCH c.shipment " +
                                "LEFT JOIN FETCH c.customsAgent " +
                                "WHERE c.customsAgent.userId = :agentId", CustomsDocument.class)
                .setParameter("agentId", agentId)
                .getResultList();
    }

    @Override
    public List<CustomsDocument> findAll() {
        return em.createQuery(
                        "SELECT c FROM CustomsDocument c " +
                                "LEFT JOIN FETCH c.shipment " +
                                "LEFT JOIN FETCH c.customsAgent", CustomsDocument.class)
                .getResultList();
    }
}