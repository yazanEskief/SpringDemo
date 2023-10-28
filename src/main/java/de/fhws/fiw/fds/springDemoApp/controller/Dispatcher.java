package de.fhws.fiw.fds.springDemoApp.controller;

import de.fhws.fiw.fds.springDemoApp.dao.LocationDAO;
import de.fhws.fiw.fds.springDemoApp.dao.PersonDAOImpl;
import de.fhws.fiw.fds.springDemoApp.entity.Person;
import de.fhws.fiw.fds.springDemoApp.util.DataFaker;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("")
    public String mainPoint() {
        return "";
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
