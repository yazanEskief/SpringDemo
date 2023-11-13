package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Role;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingContext;
import org.springframework.data.domain.Page;

public interface UserRoleDAO {

    long countRolesOfUser(final long userid);

    Page<Role> getAllRoleOfUser(final long userId, PagingAndSortingContext pagingAndSortingContext);
}
