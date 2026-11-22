package lk.jiat.global.dao;

import jakarta.ejb.Stateless;
import lk.jiat.global.entity.Role;

@Stateless
public class RoleDAO extends GenericDAO<Role, Integer> {

    public RoleDAO() {
        super(Role.class);
    }

    public Role findByRoleName(String roleName) {
        try {
            return em.createQuery("SELECT r FROM Role r WHERE r.roleName = :roleName", Role.class)
                    .setParameter("roleName", roleName)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}