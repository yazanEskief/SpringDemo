package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Role;
import de.fhws.fiw.fds.springDemoApp.entity.User;
import de.fhws.fiw.fds.springDemoApp.exception.EntityNotFoundException;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO, UserDetailsService {

    final private EntityManager entityManager;

    final private RoleDAO roleDAO;

    public UserDAOImpl(EntityManager entityManager, RoleDAO roleDAO) {
        this.entityManager = entityManager;
        this.roleDAO = roleDAO;
    }

    @Override
    public long countUsers() {
        return (long) entityManager.createQuery("SELECT COUNT(*) FROM User")
                .getSingleResult();
    }

    @Override
    public User getUserById(long id) {
        User user = entityManager.createQuery("FROM User WHERE id = :id", User.class)
                .setParameter("id", id)
                .getSingleResult();

        if (user == null) {
            throw new EntityNotFoundException("User with ID: " + id + "Not Found");
        }

        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            User user = entityManager.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return user;
        } catch (NoResultException e) {
            throw new EntityNotFoundException("User with username: " + username + " was Not Found");
        }
    }

    @Override
    public Page<User> getAllUsersByRole(String roleName, PagingAndSortingContext pagingAndSortingContext) {
        long total = countUsers();

        int offset = pagingAndSortingContext.calculateOffset(total);

        List<User> users = entityManager.createQuery("FROM User u JOIN FETCH u.roles r WHERE r.roleName LIKE :role" +
                                " ORDER BY u." + pagingAndSortingContext.getProperty() + " " + pagingAndSortingContext.getDirection(),
                        User.class)
                .setParameter("role", "%" + roleName + "%")
                .setFirstResult(offset)
                .setMaxResults(pagingAndSortingContext.getSize())
                .getResultList();

        Pageable pageable = pagingAndSortingContext.getPageable();
        return new PageImpl<>(users, pageable, total);
    }

    @Override
    @Transactional
    public User persistUser(User user) {
        user.setId(0);
        entityManager.persist(user);
        Role userRole = roleDAO.getRoleByName("ROLE_USER");
        user.addRole(userRole);
        return user;
    }

    @Override
    @Transactional
    public User updateUser(long userId, User user) {
        User userFromDB = getUserById(userId);

        if (!user.getUsername().isEmpty()) {
            userFromDB.setUsername(user.getUsername());
        }
        if(!user.getUsername().isEmpty()) {
            userFromDB.setPassword(user.getPassword());
        }

        userFromDB.setActive(user.isActive());

        entityManager.merge(userFromDB);

        return userFromDB;
    }

    @Override
    @Transactional
    public User addRoleToUser(long userId, String role) {
        User userFromDB = getUserById(userId);
        Role admin = roleDAO.getRoleByName("ROLE_ADMIN");
        Role manager = roleDAO.getRoleByName("ROLE_MANAGER");

        if (role.equals("ROLE_ADMIN")) {
            userFromDB.addRole(admin);
            userFromDB.addRole(manager);
        }

        if (role.equals("ROLE_MANAGER")) {
            userFromDB.removeRole("ROLE_ADMIN");
            userFromDB.addRole(manager);
        }

        if (role.equals("ROLE_USER")) {
            userFromDB.removeRole("ROLE_ADMIN");
            userFromDB.removeRole("ROLE_MANAGER");
        }

        entityManager.merge(userFromDB);

        return userFromDB;
    }

    @Override
    @Transactional
    public void deleteUserById(long userId) {
        User userFromDB = getUserById(userId);

        entityManager.remove(userFromDB);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User userFromDB = getUserByUsername(username);

            return new org.springframework.security.core.userdetails.User(
                    userFromDB.getUsername(),
                    userFromDB.getPassword(),
                    userFromDB.getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority(r.getRoleName()))
                            .toList()
            );
        } catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
