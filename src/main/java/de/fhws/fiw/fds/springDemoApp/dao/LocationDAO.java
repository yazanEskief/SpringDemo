package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Location;

import java.time.LocalDate;
import java.util.List;

public interface LocationDAO {

    Location persistLocation(Location location);

    Location updateLocation(long locationId, Location location);

    void deleteLocation(long id);

    void deleteAllLocations();

    List<Location> readAllLocations();

    Location readLocationById(long locationId);

    List<Location> readAllLocationsByVisitedOn(LocalDate visitedOn);
}
