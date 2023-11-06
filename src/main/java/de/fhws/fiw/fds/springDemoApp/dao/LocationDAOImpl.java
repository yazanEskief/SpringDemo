package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Location;
import de.fhws.fiw.fds.springDemoApp.exception.LocationNotFoundException;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingContext;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    public long getLocationsCount(LocalDate visitedOn) {
        String query = visitedOn == null ? "SELECT COUNT(l) FROM Location l" :
                "SELECT COUNT(*) FROM Location  WHERE visitedOn = :visitedOn";
        var typedQuery = entityManager.createQuery(query);

        if(visitedOn != null) {
            typedQuery.setParameter("visitedOn", visitedOn);
        }

        return (long)typedQuery.getSingleResult();
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
    public Page<Location> readAllLocationsByVisitedOn(LocalDate visitedOn,
                                                      PagingAndSortingContext pagingAndSortingContext) {
        long total = getLocationsCount(visitedOn);
        int offset = pagingAndSortingContext.calculateOffset(total);

        String query = visitedOn != null ? "FROM Location WHERE visitedOn = :visitedOn ORDER BY " +
                pagingAndSortingContext.getSortForDBAccess()
                : "FROM Location ORDER BY " + pagingAndSortingContext.getSortForDBAccess();
        var typedQuery = entityManager.createQuery(query, Location.class)
                .setFirstResult(offset)
                .setMaxResults(pagingAndSortingContext.getSize());

        if(visitedOn != null) {
            typedQuery = typedQuery.setParameter("visitedOn", visitedOn);
        }

        List<Location> locationFromDB = typedQuery.getResultList();

        return new PageImpl<>(locationFromDB, pagingAndSortingContext.getPageable(), total);
    }
}
