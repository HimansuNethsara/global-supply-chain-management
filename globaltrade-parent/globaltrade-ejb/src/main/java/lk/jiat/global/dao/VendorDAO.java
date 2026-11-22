package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.Vendor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Stateless
public class VendorDAO extends GenericDAO<Vendor, Integer> {

    public VendorDAO() {
        super(Vendor.class);
    }

    public Optional<Vendor> findByContactEmail(String email) {
        try {
            Vendor vendor = em.createQuery("SELECT v FROM Vendor v WHERE v.contactEmail = :email", Vendor.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.ofNullable(vendor);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Vendor> findByCountry(String country) {
        return em.createQuery("SELECT v FROM Vendor v WHERE v.country = :country", Vendor.class)
                .setParameter("country", country)
                .getResultList();
    }

    public List<Vendor> findByStatus(String status) {
        return em.createQuery("SELECT v FROM Vendor v WHERE v.status = :status", Vendor.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Vendor> findByPerformanceRatingGreaterThan(BigDecimal rating) {
        return em.createQuery("SELECT v FROM Vendor v WHERE v.performanceRating >= :rating", Vendor.class)
                .setParameter("rating", rating)
                .getResultList();
    }

    public void updatePerformanceRating(Integer vendorId, BigDecimal rating) {
        if (rating != null) {
            String sql = "UPDATE vendors SET performance_rating = ? WHERE vendor_id = ?";
            em.createNativeQuery(sql)
                    .setParameter(1, rating)
                    .setParameter(2, vendorId)
                    .executeUpdate();
        } else {
            String sql = "UPDATE vendors SET performance_rating = NULL WHERE vendor_id = ?";
            em.createNativeQuery(sql)
                    .setParameter(1, vendorId)
                    .executeUpdate();
        }
    }

    public Vendor safeUpdate(Vendor vendor) {

        Vendor managed = em.find(Vendor.class, vendor.getVendorId());
        if (managed == null) {
            return em.merge(vendor);
        }

        managed.setVendorName(vendor.getVendorName());
        managed.setContactEmail(vendor.getContactEmail());
        managed.setContactPhone(vendor.getContactPhone());
        managed.setCountry(vendor.getCountry());
        managed.setPerformanceRating(vendor.getPerformanceRating());
        managed.setStatus(vendor.getStatus());

        return managed;
    }

}