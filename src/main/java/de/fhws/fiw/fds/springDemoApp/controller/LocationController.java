package de.fhws.fiw.fds.springDemoApp.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import de.fhws.fiw.fds.springDemoApp.caching.CacheController;
import de.fhws.fiw.fds.springDemoApp.dao.LocationDAO;
import de.fhws.fiw.fds.springDemoApp.entity.Location;
import de.fhws.fiw.fds.springDemoApp.hateoas.LocationModelAssembler;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingConfig;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingContext;
import de.fhws.fiw.fds.springDemoApp.util.HyperLinks;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    final private LocationDAO locationDAO;

    final private LocationModelAssembler assembler;

    final private CacheController cacheController;

    final private HttpServletRequest request;

    @Autowired
    public LocationController(LocationDAO locationDAO, LocationModelAssembler assembler,
                              CacheController cacheController, HttpServletRequest request) {
        this.locationDAO = locationDAO;
        this.assembler = assembler;
        this.cacheController = cacheController;
        this.request = request;
    }

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PagedModel<EntityModel<Location>>> getAllLocations(
            @RequestParam(name = "visitedon", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitedon,
            @RequestParam(name = "page", defaultValue = PagingAndSortingConfig.PAGE_STRING) final int page,
            @RequestParam(name = "size", defaultValue = PagingAndSortingConfig.SIZE_STRING) final int size,
            @RequestParam(name = "sort", defaultValue = PagingAndSortingConfig.SORT) final String sort) {
        var pagingAndSortingContext = new PagingAndSortingContext(page, size, sort, Location.class);

        Page<Location> locationsFromDB = locationDAO.readAllLocationsByVisitedOn(visitedon, pagingAndSortingContext);

        return ResponseEntity.ok()
                .varyBy(HttpHeaders.ACCEPT)
                .cacheControl(cacheController.publicCache30Seconds())
                .body(assembler.toPagedModel(locationsFromDB, pagingAndSortingContext, visitedon));
    }

    @GetMapping(value = "/{locationId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<EntityModel<Location>> getLocationById(@PathVariable long locationId) {
        Location locationFromDb = locationDAO.readLocationById(locationId);
        EntityModel<Location> locationEntityModel = assembler.toModel(locationFromDb);

        return cacheController.configureCachingForGETSingleRequests(request, locationEntityModel);
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
    public ResponseEntity<EntityModel<Location>> updateLocation(@PathVariable long locationId,
                                                                @RequestBody Location updatedLocation) {
        Location locationFromDB = locationDAO.readLocationById(locationId);

        Supplier<EntityModel<Location>> locationEntityModelSupplier = () -> {
            Location newLocation = locationDAO.updateLocation(locationId, updatedLocation);
            return assembler.toModel(newLocation);
        };


        return cacheController.configureCachingForPUTRequests(request, locationEntityModelSupplier, locationFromDB);
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
