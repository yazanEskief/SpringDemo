package de.fhws.fiw.fds.springDemoApp.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import de.fhws.fiw.fds.springDemoApp.dao.LocationDAO;
import de.fhws.fiw.fds.springDemoApp.dao.PersonDAOImpl;
import de.fhws.fiw.fds.springDemoApp.dao.UserDAO;
import de.fhws.fiw.fds.springDemoApp.entity.Person;
import de.fhws.fiw.fds.springDemoApp.entity.Role;
import de.fhws.fiw.fds.springDemoApp.entity.User;
import de.fhws.fiw.fds.springDemoApp.util.DataFaker;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class Dispatcher {

    final private PersonDAOImpl personDAOImpl;

    final private LocationDAO locationDAO;

    final private UserDAO userDAO;

    @Autowired
    public Dispatcher(PersonDAOImpl personDAOImpl, LocationDAO locationDAO, UserDAO userDAO) {
        this.personDAOImpl = personDAOImpl;
        this.locationDAO = locationDAO;
        this.userDAO = userDAO;
    }

    @PostConstruct
    public void generateUsersAfterBootUp() {
        Role admin = new Role("ROLE_ADMIN");
        Role manager = new Role("ROLE_MANAGER");
        Role user = new Role("ROLE_USER");
        List<List<Role>> roles = List.of(
                List.of(admin, manager, user),
                List.of(manager, user),
                List.of(user)
        );

        User adminUser = new User("admin", "user123", true);
        User managerUser = new User("manager", "user123", true);
        User userUser = new User("user", "user123", true);

        adminUser.addAllRoles(roles.get(0));

        Arrays.asList(adminUser, managerUser, userUser)
                .forEach(userDAO::persistUser);

        userDAO.addRoleToUser(managerUser.getId(), "MANAGER");
        userDAO.addRoleToUser(userUser.getId(), "USER");
    }

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<RepresentationModel<?>> mainPoint() {
        RepresentationModel<?> model = new RepresentationModel<>();
        model.add(linkTo(PersonController.class).withRel("persons").withType(MediaType.APPLICATION_JSON_VALUE));
        model.add(linkTo(LocationController.class).withRel("locations").withType(MediaType.APPLICATION_JSON_VALUE));
        model.add(linkTo(UserController.class).withRel("users").withType(MediaType.APPLICATION_JSON_VALUE));

        return ResponseEntity.ok(model);
    }

    @GetMapping("/initializedatabase")
    public void initializeDatabase() {
        List<Person> people = DataFaker.generatePeopleWithLocations(10, 3);

        people.forEach(personDAOImpl::persistPerson);
    }

    @GetMapping("/cleardatabase")
    public void clearDatabase() {
        personDAOImpl.deleteAllPeople();
    }

    @GetMapping("/clearLocations")
    public void clearLocations() {
        locationDAO.deleteAllLocations();
    }
}
