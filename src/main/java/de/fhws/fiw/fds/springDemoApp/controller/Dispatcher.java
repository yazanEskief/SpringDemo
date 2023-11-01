package de.fhws.fiw.fds.springDemoApp.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import de.fhws.fiw.fds.springDemoApp.dao.LocationDAO;
import de.fhws.fiw.fds.springDemoApp.dao.PersonDAOImpl;
import de.fhws.fiw.fds.springDemoApp.entity.Person;
import de.fhws.fiw.fds.springDemoApp.util.DataFaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class Dispatcher {

    private PersonDAOImpl personDAOImpl;
    private LocationDAO locationDAO;

    @Autowired
    public Dispatcher(PersonDAOImpl personDAOImpl, LocationDAO locationDAO) {
        this.personDAOImpl = personDAOImpl;
        this.locationDAO = locationDAO;
    }

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<RepresentationModel<?>> mainPoint() {
        RepresentationModel<?> model = new RepresentationModel<>();
        model.add(linkTo(PersonController.class).withRel("persons").withType(MediaType.APPLICATION_JSON_VALUE));
        model.add(linkTo(LocationController.class).withRel("locations").withType(MediaType.APPLICATION_JSON_VALUE));

        return ResponseEntity.ok(model);
    }

    @GetMapping("/initializedatabase")
    public void initializeDatabase() {
        List<Person> people = DataFaker.generatePeopleWithLocations(10, 3);

        people.forEach(p -> {
            personDAOImpl.persistPerson(p);
        });
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
