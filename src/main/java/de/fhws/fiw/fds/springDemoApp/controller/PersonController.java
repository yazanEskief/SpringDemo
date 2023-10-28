package de.fhws.fiw.fds.springDemoApp.controller;

import de.fhws.fiw.fds.springDemoApp.dao.PersonDAOImpl;
import de.fhws.fiw.fds.springDemoApp.entity.Location;
import de.fhws.fiw.fds.springDemoApp.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    private PersonDAOImpl personDAOImpl;

    @Autowired
    public PersonController(PersonDAOImpl personDAOImpl) {
        this.personDAOImpl = personDAOImpl;
    }

    @GetMapping("")
    public List<Person> getAllPeople(@RequestParam(name = "firstname", defaultValue = "") String firstname,
                                     @RequestParam(name = "lastname", defaultValue = "") String lastname) {
        return personDAOImpl.realAllPeopleByFirstNameOrLastname(firstname, lastname);
    }

    @GetMapping("/{id}")
    public Person getPersonById(@PathVariable long id) {
        return personDAOImpl.readPersonById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void savePerson(@RequestBody Person person) {
        person.setId(0);
        personDAOImpl.persistPerson(person);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePerson(@PathVariable long id, @RequestBody Person updatedPerson) {
        updatedPerson.setId(id);
        personDAOImpl.updatePerson(id, updatedPerson);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(@PathVariable long id) {
        personDAOImpl.deletePerson(id);
    }

    @GetMapping("/{personId}/location/{locationId}")
    public Location getSingleLocationOfPerson(@PathVariable long personId, @PathVariable long locationId) {
        return personDAOImpl.readSingleLocationOfPerson(personId, locationId);
    }

    @GetMapping("/{personId}/location")
    public List<Location> getAllLocationsOfPerson(@PathVariable long personId) {
        return personDAOImpl.readAllLocationOfPerson(personId);
    }

    @PostMapping("/{personId}/location")
    @ResponseStatus(HttpStatus.CREATED)
    public void addLocationToPerson(@PathVariable long personId, @RequestBody Location location) {
        location.setId(0);

        personDAOImpl.addLocationToPerson(personId, location);
    }

    @PostMapping("/{personId}/locations")
    @ResponseStatus(HttpStatus.CREATED)
    public void addLocationsToPerson(@PathVariable long personId, @RequestBody List<Location> locations) {
        locations.forEach(l -> l.setId(0));

        personDAOImpl.addAllLocationsToPerson(personId, locations);
    }

    @PutMapping("/{personId}/location/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLocationOfPerson(@PathVariable long personId, @PathVariable long locationId,
                                       @RequestBody Location updatedLocation) {
        updatedLocation.setId(locationId);

        personDAOImpl.updateLocationOfPerson(personId, locationId, updatedLocation);
    }

    @DeleteMapping("{personId}/location/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSingleLocationOfPerson(@PathVariable long personId, @PathVariable long locationId) {
        personDAOImpl.deleteLocationOfPerson(personId, locationId);
    }

    @DeleteMapping("/{personId}/location")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpecificLocationsOfPerson(@PathVariable final long personId, @RequestBody(required = false) final List<Long> locationIds,
                                                @RequestParam(name = "all", defaultValue = "false") final boolean all) {
        if(all) {
            List<Location> locationsToDelete = personDAOImpl.readAllLocationOfPerson(personId);
            List<Long> locationsIdsToDelete = locationsToDelete.stream().map(Location::getId).toList();
            personDAOImpl.deleteAllLocationsOfPerson(personId, locationsIdsToDelete);

        } else personDAOImpl.deleteAllLocationsOfPerson(personId, locationIds);
    }
}
