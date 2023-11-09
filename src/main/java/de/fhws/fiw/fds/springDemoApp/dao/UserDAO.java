package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Role;
import de.fhws.fiw.fds.springDemoApp.entity.User;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingContext;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserDAO {
    long countUsers();

    User getUserById(long id);

    User getUserByUsername(String username);

    Page<User> getAllUsersByRole(String roleName, PagingAndSortingContext pagingAndSortingContext);

    User persistUser(User user);

    User updateUser(long userId, User user);

    User addRoleToUser(long userId, String role);

    void deleteUserById(long userId);
}
