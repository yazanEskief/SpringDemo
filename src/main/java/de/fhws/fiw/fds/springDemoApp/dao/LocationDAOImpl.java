package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Location;
import de.fhws.fiw.fds.springDemoApp.exception.LocationNotFoundException;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public class LocationDAOImpl implements LocationDAO {

    private EntityManager entityManager;

    public LocationDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Location persistLocation(Location location) {
        entityManager.persist(location);

        return location;
    }

    @Override
    @Transactional
    public Location updateLocation(long locationId, Location location) {
        Location locationFromDB = readLocationById(locationId);

        locationFromDB.updateLocation(location);

        entityManager.merge(locationFromDB);

        return locationFromDB;
    }

    @Override
    @Transactional
    public void deleteLocation(long id) {
        Location locationFromDB = readLocationById(id);
        entityManager.remove(locationFromDB);
    }

    @Override
    @Transactional
    public void deleteAllLocations() {
        entityManager.createQuery("DELETE FROM Location ")
                .executeUpdate();
    }

    @Override
    public List<Location> readAllLocations() {
        List<Location> allLocations = entityManager.createQuery("FROM Location", Location.class).getResultList();
        return allLocations;
    }

    @Override
    public Location readLocationById(long locationId) {
        Location locationFromDB = entityManager.find(Location.class, locationId);
        if(locationFromDB == null) {
            throw new LocationNotFoundException("Location with ID: " + locationId + " couldn't be found");
        }
        return locationFromDB;
    }

    @Override
    public List<Location> readAllLocationsByVisitedOn(LocalDate visitedOn) {
        List<Location> locationFromDB = entityManager.createQuery("FROM Location WHERE visitedOn = :visitedOn", Location.class)
                .setParameter("visitedOn", visitedOn)
                .getResultList();
        return locationFromDB;
    }
}
