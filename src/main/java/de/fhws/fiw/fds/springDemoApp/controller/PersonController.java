package de.fhws.fiw.fds.springDemoApp.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import de.fhws.fiw.fds.springDemoApp.dao.LocationDAO;
import de.fhws.fiw.fds.springDemoApp.dao.PersonDAOImpl;
import de.fhws.fiw.fds.springDemoApp.entity.Location;
import de.fhws.fiw.fds.springDemoApp.entity.Person;
import de.fhws.fiw.fds.springDemoApp.hateoas.LocationModelAssembler;
import de.fhws.fiw.fds.springDemoApp.hateoas.PersonModelAssembler;
import de.fhws.fiw.fds.springDemoApp.util.HyperLinks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    private PersonDAOImpl personDAOImpl;

    private LocationDAO locationDAO;

    private PersonModelAssembler personModelAssembler;

    private LocationModelAssembler locationModelAssembler;

    @Autowired
    public PersonController(PersonDAOImpl personDAOImpl, PersonModelAssembler personModelAssembler,
                            LocationModelAssembler locationModelAssembler, LocationDAO locationDAO) {
        this.personDAOImpl = personDAOImpl;
        this.personModelAssembler = personModelAssembler;
        this.locationModelAssembler = locationModelAssembler;
        this.locationDAO = locationDAO;
    }

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CollectionModel<EntityModel<Person>>> getAllPeople
            (@RequestParam(name = "firstname", defaultValue = "") String firstname,
             @RequestParam(name = "lastname", defaultValue = "") String lastname) {
        List<Person> allPeople = personDAOImpl.realAllPeopleByFirstNameOrLastname(firstname, lastname);

        return ResponseEntity.ok(personModelAssembler.toCollectionModel(allPeople));
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public EntityModel<Person> getPersonById(@PathVariable long id) {
        Person personFromDB = personDAOImpl.readPersonById(id);
        return personModelAssembler.toModel(personFromDB);
    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<EntityModel<Person>> savePerson(@RequestBody Person person) {
        person.setId(0);
        Person createdPerson = personDAOImpl.persistPerson(person);
        EntityModel<Person> personEntityModel = personModelAssembler.toModel(createdPerson);
        return ResponseEntity.created(
                personEntityModel.getRequiredLink("self").toUri()
        ).build();
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> updatePerson(@PathVariable long id, @RequestBody Person updatedPerson) {
        updatedPerson.setId(id);
        personDAOImpl.updatePerson(id, updatedPerson);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable long id) {
        personDAOImpl.deletePerson(id);
        return ResponseEntity
                .noContent()
                .header("Link", HyperLinks.createHyperLink(
                        linkTo(PersonController.class).toUri().toASCIIString(),
                        "Persons",
                        MediaType.APPLICATION_JSON_VALUE
                ))
                .build();
    }

    @GetMapping(value = "/{personId}/location/{locationId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<EntityModel<Location>> getSingleLocationOfPerson(@PathVariable long personId,
                                                                           @PathVariable long locationId) {
        Location locationFromDB = personDAOImpl.readSingleLocationOfPerson(personId, locationId);

        return ResponseEntity.ok()
                .body(locationModelAssembler.toModel(locationFromDB));
    }

    @GetMapping(value = "/{personId}/location",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CollectionModel<EntityModel<Location>>> getAllLocationsOfPerson
            (@PathVariable long personId,
             @RequestParam(name = "showAll", required = false, defaultValue = "false") boolean showAll) {

        if (showAll == false) {
            List<Location> locationsOfPerson = personDAOImpl.readAllLocationOfPerson(personId);
            return ResponseEntity.ok(locationModelAssembler
                    .toCollectionModelOnPerson(locationsOfPerson, showAll, personId));
        }

        List<Location> locationsFromDB = personDAOImpl.readLinkedAndUnlinkedLocationsOfPerson(personId);

        return ResponseEntity.ok(
                locationModelAssembler.toCollectionModelOnPerson(locationsFromDB, showAll, personId)
        );
    }

    @PostMapping(value = "/{personId}/location",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EntityModel<Location>> addLocationToPerson(@PathVariable long personId,
                                                                     @RequestBody Location location) {
        location.setId(0);

        Location createdLocation = personDAOImpl.addLocationToPerson(personId, location);

        return ResponseEntity.created(
                        linkTo(methodOn(PersonController.class)
                                .getSingleLocationOfPerson(personId, createdLocation.getId())).toUri())
                .body(locationModelAssembler.toModel(createdLocation));
    }

    @PostMapping(value = "/{personId}/locations",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public void addLocationsToPerson(@PathVariable long personId, @RequestBody List<Location> locations) {
        locations.forEach(l -> l.setId(0));

        personDAOImpl.addAllLocationsToPerson(personId, locations);
    }

    @PutMapping(value = "/{personId}/location/{locationId}")
    public ResponseEntity<EntityModel<Location>> linkLocationToPerson(@PathVariable long personId,
                                                                      @PathVariable long locationId) {
        Location linkedLocation = personDAOImpl.linkLocationToPerson(personId, locationId);

        return ResponseEntity.ok()
                .body(locationModelAssembler.toModel(linkedLocation));
    }

    @DeleteMapping("{personId}/location/{locationId}")
    public ResponseEntity<?> unLinkLocationFromPerson(@PathVariable long personId, @PathVariable long locationId) {
        personDAOImpl.unlinkLocationFromPerson(personId, locationId);

        return ResponseEntity.noContent()
                .header("Link", HyperLinks.createHyperLink(
                        linkTo(methodOn(PersonController.class).getAllLocationsOfPerson(personId, false))
                                .toUri().toASCIIString(),
                        "locationOfPerson",
                        MediaType.APPLICATION_JSON_VALUE
                )).build();
    }

    @DeleteMapping("/{personId}/location")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpecificLocationsOfPerson(@PathVariable final long personId,
                                                @RequestBody(required = false) final List<Long> locationIds,
                                                @RequestParam(name = "all", defaultValue = "false") final boolean all) {
        if (all) {
            List<Location> locationsToDelete = personDAOImpl.readAllLocationOfPerson(personId);
            List<Long> locationsIdsToDelete = locationsToDelete.stream().map(Location::getId).toList();
            personDAOImpl.deleteAllLocationsOfPerson(personId, locationsIdsToDelete);

        } else personDAOImpl.deleteAllLocationsOfPerson(personId, locationIds);
    }
}
