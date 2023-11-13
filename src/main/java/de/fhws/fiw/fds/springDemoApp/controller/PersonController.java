package de.fhws.fiw.fds.springDemoApp.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import de.fhws.fiw.fds.springDemoApp.caching.CacheController;
import de.fhws.fiw.fds.springDemoApp.dao.PersonDAOImpl;
import de.fhws.fiw.fds.springDemoApp.entity.Location;
import de.fhws.fiw.fds.springDemoApp.entity.Person;
import de.fhws.fiw.fds.springDemoApp.exception.UnsupportedUnlinkOperation;
import de.fhws.fiw.fds.springDemoApp.hateoas.LocationModelAssembler;
import de.fhws.fiw.fds.springDemoApp.hateoas.PersonModelAssembler;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingConfig;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingContext;
import de.fhws.fiw.fds.springDemoApp.util.HyperLinks;
import de.fhws.fiw.fds.springDemoApp.util.Operation;
import de.fhws.fiw.fds.springDemoApp.util.UnlinkResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    final private PersonDAOImpl personDAOImpl;

    final private PersonModelAssembler personModelAssembler;

    final private LocationModelAssembler locationModelAssembler;

    final private HttpServletRequest request;

    final private CacheController cacheController;

    @Autowired
    public PersonController(PersonDAOImpl personDAOImpl, PersonModelAssembler personModelAssembler,
                            LocationModelAssembler locationModelAssembler, CacheController cacheController,
                            HttpServletRequest request) {
        this.personDAOImpl = personDAOImpl;
        this.personModelAssembler = personModelAssembler;
        this.locationModelAssembler = locationModelAssembler;
        this.cacheController = cacheController;
        this.request = request;
    }

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PagedModel<EntityModel<Person>>> getAllPeople
            (@RequestParam(name = "firstname", defaultValue = "") String firstname,
             @RequestParam(name = "lastname", defaultValue = "") String lastname,
             @RequestParam(name = "op", defaultValue = "AND") final Operation operation,
             @RequestParam(name = "page", defaultValue = PagingAndSortingConfig.PAGE_STRING) final int page,
             @RequestParam(name = "size", defaultValue = PagingAndSortingConfig.SIZE_STRING) final int size,
             @RequestParam(name = "sort", defaultValue = PagingAndSortingConfig.SORT) final String sort) {
        var pagingContext = new PagingAndSortingContext(page, size, sort, Person.class);

        Page<Person> allPeople =
                personDAOImpl.realAllPeopleByFirstNameLastname(firstname, lastname, operation, pagingContext);

        var result = personModelAssembler.toPagedModel(allPeople, firstname, lastname, operation, pagingContext);

        return ResponseEntity.ok()
                .cacheControl(cacheController.publicCache30Seconds())
                .varyBy(HttpHeaders.ACCEPT)
                .body(result);
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<EntityModel<Person>> getPersonById(@PathVariable long id) {
        Person personFromDB = personDAOImpl.readPersonById(id);
        EntityModel<Person> personEntityModel = personModelAssembler.toModel(personFromDB);

        return cacheController.configureCachingForGETSingleRequests(
                request,
                personEntityModel
        );
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
    public ResponseEntity<EntityModel<Person>> updatePerson(@PathVariable long id, @RequestBody Person updatedPerson) {
        Person personFromDB = personDAOImpl.readPersonById(id);

        Supplier<EntityModel<Person>> personEntityModelSupplier = () -> {
            Person newPerson = personDAOImpl.updatePerson(id, updatedPerson);
            return personModelAssembler.toModel(newPerson);
        };

        return cacheController.configureCachingForPUTRequests(
                request,
                personEntityModelSupplier,
                personFromDB
        );
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
        EntityModel<Location> locationEntityModel = locationModelAssembler.toModelOnPerson(locationFromDB, personId);

        return cacheController.configureCachingForGETSingleRequests(
                request,
                locationEntityModel
        );
    }

    @GetMapping(value = "/{personId}/location",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PagedModel<EntityModel<Location>>> getAllLocationsOfPerson
            (@PathVariable long personId,
             @RequestParam(name = "showAll", required = false, defaultValue = "false") boolean showAll,
             @RequestParam(name = "page", defaultValue = PagingAndSortingConfig.PAGE_STRING) final int page,
             @RequestParam(name = "size", defaultValue = PagingAndSortingConfig.SIZE_STRING) final int size,
             @RequestParam(name = "sort", defaultValue = PagingAndSortingConfig.SORT) final String sort) {

        PagingAndSortingContext pagingAndSortingContext = new PagingAndSortingContext(page, size, sort, Location.class);
        if (showAll == false) {
            Page<Location> locationsOfPerson = personDAOImpl.readAllLocationOfPerson(personId, pagingAndSortingContext);
            return ResponseEntity.ok()
                    .cacheControl(cacheController.publicCache30Seconds())
                    .varyBy(HttpHeaders.ACCEPT)
                    .body(locationModelAssembler
                            .toPagedModelOnPerson(locationsOfPerson, showAll, personId, pagingAndSortingContext));
        }

        Page<Location> locationsFromDB = personDAOImpl.readLinkedAndUnlinkedLocationsOfPerson(personId,
                pagingAndSortingContext);

        return ResponseEntity.ok()
                .varyBy(HttpHeaders.ACCEPT)
                .cacheControl(cacheController.publicCache30Seconds())
                .body(
                        locationModelAssembler.toPagedModelOnPerson(locationsFromDB, showAll, personId, pagingAndSortingContext)
                );
    }

    @PostMapping(value = "/{personId}/location",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
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
    public ResponseEntity<CollectionModel<EntityModel<Location>>> addLocationsToPerson
            (@PathVariable long personId, @RequestBody List<Location> locations) {
        locations.forEach(l -> l.setId(0));

        List<Location> createdLocations = personDAOImpl.addAllLocationsToPerson(personId, locations);

        return ResponseEntity.status(HttpStatus.MULTI_STATUS)
                .body(locationModelAssembler.toCollectionModelOnPerson(createdLocations, false, personId));
    }

    @PatchMapping(value = "/{personId}/location/{locationId}")
    public ResponseEntity<EntityModel<Location>> linkLocationToPerson(@PathVariable long personId,
                                                                      @PathVariable long locationId) {
        Location linkedLocation = personDAOImpl.linkLocationToPerson(personId, locationId);

        return ResponseEntity.accepted()
                .body(locationModelAssembler.toModel(linkedLocation));
    }

    @PutMapping("{personId}/location/{locationId}")
    public ResponseEntity<?> unLinkLocationFromPerson(@PathVariable long personId, @PathVariable long locationId) {
        Location unlickedLocation = personDAOImpl.unlinkLocationFromPerson(personId, locationId);

        return ResponseEntity.accepted()
                .body(locationModelAssembler.toModelOnPerson(unlickedLocation, personId));
    }

    @PutMapping("/{personId}/location")
    public ResponseEntity<CollectionModel<UnlinkResponse>> unlinkLocationsOfPerson(
            @PathVariable final long personId,
            @RequestParam(name = "ids", defaultValue = "") final List<Long> locationIds) {

        if (locationIds.isEmpty()) {
            throw new UnsupportedUnlinkOperation("No ids for locations to unlink from Person with ID: "
                    + personId + " are provided");
        }

        List<UnlinkResponse> responses = new ArrayList<>();

        for (Long id : locationIds) {
            try {
                personDAOImpl.unlinkLocationFromPerson(personId, id);
                var unlinkedResponse = new UnlinkResponse(
                        HttpStatus.OK.value() + " " + HttpStatus.OK.getReasonPhrase(),
                        null,
                        linkTo(methodOn(PersonController.class).unLinkLocationFromPerson(personId, id))
                                .toUri().toASCIIString());
                responses.add(unlinkedResponse);
            } catch (Exception e) {
                var unlinkedResponse = new UnlinkResponse(
                        HttpStatus.BAD_REQUEST.value() + " " + HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        e.getMessage(),
                        linkTo(methodOn(PersonController.class).unLinkLocationFromPerson(personId, id))
                                .toUri().toASCIIString());
                responses.add(unlinkedResponse);
            }
        }

        CollectionModel<UnlinkResponse> model = CollectionModel.of(responses);
        model.add(linkTo(methodOn(PersonController.class)
                .getAllLocationsOfPerson(personId, false, PagingAndSortingConfig.PAGE,
                        PagingAndSortingConfig.SIZE, PagingAndSortingConfig.SORT))
                .withRel("locationOfPerson").withType(MediaType.APPLICATION_JSON_VALUE));

        return ResponseEntity.status(HttpStatus.MULTI_STATUS)
                .body(model);
    }
}