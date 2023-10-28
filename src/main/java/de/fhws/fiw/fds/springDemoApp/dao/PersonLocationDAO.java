package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Location;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

public interface PersonLocationDAO {

    List<Location> readAllLocationOfPerson(long personId);

    Location readSingleLocationOfPerson(long personId, long locationId);

    void addLocationToPerson(long personId, Location location);

    void addAllLocationsToPerson(long personId, List<Location> locations);

    @Transactional
    default void addAllLocationsToPerson(long personId, Location... locations) {
        addAllLocationsToPerson(personId, Arrays.asList(locations));
    }

    void updateLocationOfPerson(long personId, long locationId, Location location);

    void deleteLocationOfPerson(long personId, long locationId);

    @Transactional
    default void deleteAllLocationsOfPerson(long personId, Long... locationsIds) {
        deleteAllLocationsOfPerson(personId, Arrays.asList(locationsIds));
    }

    @Transactional
    default void deleteAllLocationsOfPerson(long personId, List<Long> locationsIds) {
        locationsIds.forEach(locationId -> deleteLocationOfPerson(personId, locationId));
    }
}
