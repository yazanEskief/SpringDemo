package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Location;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

public interface PersonLocationDAO {

    List<Location> readAllLocationOfPerson(long personId);

    Location readSingleLocationOfPerson(long personId, long locationId);

    List<Location> readLinkedAndUnlinkedLocationsOfPerson(long personId);

    Location addLocationToPerson(long personId, Location location);

    List<Location> addAllLocationsToPerson(long personId, List<Location> locations);

    @Transactional
    default void addAllLocationsToPerson(long personId, Location... locations) {
        addAllLocationsToPerson(personId, Arrays.asList(locations));
    }

    Location linkLocationToPerson(long personId, long locationId);

    Location unlinkLocationFromPerson(long personId, long locationId);

    @Transactional
    default void unlinkLocationsOfPerson(long personId, Long... locationsIds) {
        unlinkLocationsOfPerson(personId, Arrays.asList(locationsIds));
    }

    @Transactional
    default void unlinkLocationsOfPerson(long personId, List<Long> locationsIds) {
        locationsIds.forEach(locationId -> unlinkLocationFromPerson(personId, locationId));
    }
}
