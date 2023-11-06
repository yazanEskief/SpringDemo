package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Location;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingContext;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface LocationDAO {

    long getLocationsCount(LocalDate visitedOn);

    Location persistLocation(Location location);

    Location updateLocation(long locationId, Location location);

    void deleteLocation(long id);

    void deleteAllLocations();

    List<Location> readAllLocations();

    Location readLocationById(long locationId);

    Page<Location> readAllLocationsByVisitedOn(LocalDate visitedOn, PagingAndSortingContext pagingAndSortingContext);
}
