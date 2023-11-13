package de.fhws.fiw.fds.springDemoApp.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import de.fhws.fiw.fds.springDemoApp.caching.CacheController;
import de.fhws.fiw.fds.springDemoApp.dao.UserDAO;
import de.fhws.fiw.fds.springDemoApp.dao.UserRoleDAO;
import de.fhws.fiw.fds.springDemoApp.entity.Role;
import de.fhws.fiw.fds.springDemoApp.entity.User;
import de.fhws.fiw.fds.springDemoApp.hateoas.RoleModelAssembler;
import de.fhws.fiw.fds.springDemoApp.hateoas.UserModelAssembler;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingConfig;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingContext;
import de.fhws.fiw.fds.springDemoApp.util.Roles;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

@RestController
@RequestMapping("/api/user")
public class UserController {

    final private UserDAO userDAO;

    final private UserRoleDAO userRoleDAO;

    final private UserModelAssembler userModelAssembler;

    final private RoleModelAssembler roleModelAssembler;

    final private CacheController cacheController;

    final private HttpServletRequest request;

    public UserController(UserDAO userDAO, UserModelAssembler userModelAssembler, CacheController cacheController,
                          HttpServletRequest request, UserRoleDAO userRoleDAO, RoleModelAssembler roleModelAssembler) {
        this.userDAO = userDAO;
        this.userModelAssembler = userModelAssembler;
        this.cacheController = cacheController;
        this.request = request;
        this.userRoleDAO = userRoleDAO;
        this.roleModelAssembler = roleModelAssembler;
    }

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PagedModel<EntityModel<User>>> getAllUsers(
            @RequestParam(name = "role", defaultValue = "") final String role,
            @RequestParam(name = "page", defaultValue = PagingAndSortingConfig.PAGE_STRING) final int page,
            @RequestParam(name = "size", defaultValue = PagingAndSortingConfig.SIZE_STRING) final int size,
            @RequestParam(name = "sort", defaultValue = PagingAndSortingConfig.SORT) final String sort
    ) {
        var pagingAndSortingContext = new PagingAndSortingContext(page, size, sort, User.class);

        Page<User> users = userDAO.getAllUsersByRole(role, pagingAndSortingContext);

        return ResponseEntity
                .ok()
                .cacheControl(cacheController.publicCache30Seconds())
                .varyBy(HttpHeaders.ACCEPT)
                .body(userModelAssembler.toPagedModel(users, pagingAndSortingContext));
    }

    @GetMapping(value = "/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<EntityModel<User>> getUserById(@PathVariable final long userId) {
        User userFromDB = userDAO.getUserById(userId);

        EntityModel<User> userEntityModel = userModelAssembler.toModel(userFromDB);

        return cacheController.configureCachingForGETSingleRequests(request, userEntityModel);
    }

    @GetMapping(value = "/{userId}/roles", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PagedModel<Role>> getRolesOfUser(
            @PathVariable final long userId,
            @RequestParam(name = "page", defaultValue = PagingAndSortingConfig.PAGE_STRING) final int page,
            @RequestParam(name = "size", defaultValue = PagingAndSortingConfig.SIZE_STRING) final int size) {
        PagingAndSortingContext pagingAndSortingContext = new PagingAndSortingContext(page, size, "id", Role.class);

        Page<Role> rolesPage = userRoleDAO.getAllRoleOfUser(userId, pagingAndSortingContext);

        return ResponseEntity.ok()
                .cacheControl(cacheController.publicCache30Seconds())
                .varyBy(HttpHeaders.ACCEPT)
                .body(roleModelAssembler.toPagedModelOnUser(rolesPage, userId));
    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> postUser(
            @RequestBody final User user
    ) {
        User savedUser = userDAO.persistUser(user);

        return ResponseEntity.created(
                linkTo(methodOn(UserController.class).getUserById(savedUser.getId()))
                        .withSelfRel()
                        .withType(MediaType.APPLICATION_JSON_VALUE)
                        .toUri()
        ).build();
    }

    @PutMapping(value = "/{userId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<EntityModel<User>> updateUser(
            @PathVariable final long userId,
            @RequestBody final User newUser
    ) {
        User userFromDB = userDAO.getUserById(userId);

        Supplier<EntityModel<User>> userEntityModelSupplier = () -> {
            User updatedUser = userDAO.updateUser(userId, newUser);
            return userModelAssembler.toModel(updatedUser);
        };

        return cacheController.configureCachingForPUTRequests(
                request,
                userEntityModelSupplier,
                userFromDB
        );
    }

    @PatchMapping(value = "/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<EntityModel<User>> makeUserAdmin(
            @PathVariable final long userId,
            @RequestParam(value = "role", defaultValue = "ROLE_USER") final Roles roles
    ) {
        User updatedUser = userDAO.addRoleToUser(userId, roles.toString());

        return ResponseEntity.accepted()
                .body(userModelAssembler.toModel(updatedUser));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable final long userId
    ) {
        userDAO.deleteUserById(userId);

        Link allUsersLink = linkTo(methodOn(UserController.class).getAllUsers("",
                PagingAndSortingConfig.PAGE, PagingAndSortingConfig.SIZE, PagingAndSortingConfig.SORT))
                .withRel("users")
                .withType(MediaType.APPLICATION_JSON_VALUE);

        return ResponseEntity.noContent()
                .header("Link", allUsersLink.getHref())
                .build();
    }
}
