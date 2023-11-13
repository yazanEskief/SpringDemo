package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Role;
import de.fhws.fiw.fds.springDemoApp.exception.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDAOImpl implements RoleDAO {

    private final EntityManager entityManager;

    public RoleDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Role getRoleByName(String role) {
        try {
            return entityManager.createQuery("FROM Role WHERE roleName LIKE :role", Role.class)
                    .setParameter("role", "%" + role + "%")
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("Role with name: " + role + " was Not Found");
        }
    }
}
