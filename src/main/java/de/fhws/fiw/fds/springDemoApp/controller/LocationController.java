package de.fhws.fiw.fds.springDemoApp.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import de.fhws.fiw.fds.springDemoApp.dao.LocationDAO;
import de.fhws.fiw.fds.springDemoApp.entity.Location;
import de.fhws.fiw.fds.springDemoApp.hateoas.LocationModelAssembler;
import de.fhws.fiw.fds.springDemoApp.util.HyperLinks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private LocationDAO locationDAO;

    private LocationModelAssembler assembler;

    @Autowired
    public LocationController(LocationDAO locationDAO, LocationModelAssembler assembler) {
        this.locationDAO = locationDAO;
        this.assembler = assembler;
    }

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CollectionModel<EntityModel<Location>>> getAllLocations(@RequestParam(name = "visitedon", required = false)
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitedon) {
        if (visitedon == null) {
            List<Location> locationsFromDB = locationDAO.readAllLocations();

            return ResponseEntity.ok(assembler.toCollectionModel(locationsFromDB));
        }

        List<Location> locationsFromDB = locationDAO.readAllLocationsByVisitedOn(visitedon);

        return ResponseEntity.ok(assembler.toCollectionModel(locationsFromDB));
    }

    @GetMapping(value = "/{locationId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<EntityModel<Location>> getLocationById(@PathVariable long locationId) {
        Location locationFromDb = locationDAO.readLocationById(locationId);
        return ResponseEntity.ok(assembler.toModel(locationFromDb));
    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> saveLocation(@RequestBody Location location) {
        location.setId(0);

        Location persistedlocation = locationDAO.persistLocation(location);

        return ResponseEntity.created(
                linkTo(methodOn(LocationController.class).getLocationById(persistedlocation.getId())).toUri()
        ).build();
    }

    @PutMapping(value = "/{locationId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<EntityModel<Location>> updateLocation(@PathVariable long locationId, @RequestBody Location updatedLocation) {
        updatedLocation.setId(locationId);

        Location newLocation = locationDAO.updateLocation(locationId, updatedLocation);

        return ResponseEntity.ok()
                .body(assembler.toModel(newLocation));
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<?> deleteLocation(@PathVariable long locationId) {
        locationDAO.deleteLocation(locationId);

        return ResponseEntity.noContent()
                .header("Link", HyperLinks.createHyperLink(
                        linkTo(LocationController.class).toUri().toASCIIString(),
                        "locations",
                        MediaType.APPLICATION_JSON_VALUE
                )).build();
    }

}
