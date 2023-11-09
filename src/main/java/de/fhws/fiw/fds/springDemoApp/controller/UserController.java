package de.fhws.fiw.fds.springDemoApp.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import de.fhws.fiw.fds.springDemoApp.dao.UserDAO;
import de.fhws.fiw.fds.springDemoApp.entity.Role;
import de.fhws.fiw.fds.springDemoApp.entity.User;
import de.fhws.fiw.fds.springDemoApp.hateoas.UserModelAssembler;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingContext;
import de.fhws.fiw.fds.springDemoApp.util.Roles;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    final private UserDAO userDAO;

    final private UserModelAssembler userModelAssembler;

    public UserController(UserDAO userDAO, UserModelAssembler userModelAssembler) {
        this.userDAO = userDAO;
        this.userModelAssembler = userModelAssembler;
    }

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PagedModel<EntityModel<User>>> getAllUsers(
            @RequestParam(name = "role", defaultValue = "") final String role,
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "20") final int size,
            @RequestParam(name = "sort", defaultValue = "id") final String sort
    ) {
        var pagingAndSortingContext = new PagingAndSortingContext(page, size, sort, User.class);

        Page<User> users = userDAO.getAllUsersByRole(role, pagingAndSortingContext);

        return ResponseEntity.ok(userModelAssembler.toPagedModel(users, pagingAndSortingContext));
    }

    @GetMapping(value = "/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<EntityModel<User>> getUserById(@PathVariable final long userId) {
        User userFromDB = userDAO.getUserById(userId);

        return ResponseEntity.ok(userModelAssembler.toModel(userFromDB));
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
        User updatedUser = userDAO.updateUser(userId, newUser);

        return ResponseEntity.accepted()
                .body(userModelAssembler.toModel(updatedUser));
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

        Link allUsersLink = linkTo(methodOn(UserController.class).getAllUsers("", 0, 20, "id"))
                .withRel("users")
                .withType(MediaType.APPLICATION_JSON_VALUE);

        return ResponseEntity.noContent()
                .header("Link", allUsersLink.getHref())
                .build();
    }
}
