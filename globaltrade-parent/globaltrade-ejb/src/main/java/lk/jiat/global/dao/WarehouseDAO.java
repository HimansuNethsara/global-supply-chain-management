package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.Warehouse;

import java.util.List;

@Stateless
public class WarehouseDAO extends GenericDAO<Warehouse, Integer> {

    public WarehouseDAO() {
        super(Warehouse.class);
    }

    public List<Warehouse> findByCountry(String country) {
        return em.createQuery("SELECT w FROM Warehouse w WHERE w.country = :country", Warehouse.class)
                .setParameter("country", country)
                .getResultList();
    }

    public List<Warehouse> findByCapacityGreaterThan(Integer minCapacity) {
        return em.createQuery("SELECT w FROM Warehouse w WHERE w.capacity >= :minCapacity", Warehouse.class)
                .setParameter("minCapacity", minCapacity)
                .getResultList();
    }
}