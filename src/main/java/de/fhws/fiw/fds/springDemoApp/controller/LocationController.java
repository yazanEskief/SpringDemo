package de.fhws.fiw.fds.springDemoApp.controller;

import de.fhws.fiw.fds.springDemoApp.dao.LocationDAO;
import de.fhws.fiw.fds.springDemoApp.entity.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private LocationDAO locationDAO;

    @Autowired
    public LocationController(LocationDAO locationDAO) {
        this.locationDAO = locationDAO;
    }

    @GetMapping("")
    public List<Location> getAllLocations(@RequestParam(name = "visitedon", required = false)
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitedon) {
        if (visitedon == null) {
            return locationDAO.readAllLocations();
        }

        return locationDAO.readAllLocationsByVisitedOn(visitedon);
    }

    @GetMapping("/{locationId}")
    public Location getLocationById(@PathVariable long locationId) {
        return locationDAO.readLocationById(locationId);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveLocation(@RequestBody Location location) {
        location.setId(0);

        locationDAO.persistLocation(location);
    }

    @PutMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLocation(@PathVariable long locationId, @RequestBody Location updatedLocation) {
        updatedLocation.setId(locationId);

        locationDAO.updateLocation(locationId, updatedLocation);
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable long locationId) {
        locationDAO.deleteLocation(locationId);
    }

}
