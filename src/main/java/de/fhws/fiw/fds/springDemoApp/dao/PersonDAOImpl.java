package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Location;
import de.fhws.fiw.fds.springDemoApp.entity.Person;
import de.fhws.fiw.fds.springDemoApp.exception.EntityNotFoundException;
import de.fhws.fiw.fds.springDemoApp.exception.LinkLocationToPersonNotAllowedException;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingContext;
import de.fhws.fiw.fds.springDemoApp.util.Operation;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PersonDAOImpl implements PersonDAO, PersonLocationDAO {
    private EntityManager entityManager;

    private LocationDAO locationDAO;

    public PersonDAOImpl(EntityManager entityManager, LocationDAO locationDAO) {
        this.entityManager = entityManager;
        this.locationDAO = locationDAO;
    }

    @Override
    @Transactional
    public Person persistPerson(Person person) {
        entityManager.persist(person);
        return person;
    }

    @Override
    public long getPersonCount() {
        return (long) entityManager.createQuery("SELECT COUNT(s) FROM Person s").getSingleResult();
    }

    @Override
    @Transactional
    public Person updatePerson(long personId, Person person) {
        Person personFromDB = readPersonById(personId);

        personFromDB.updatePerson(person);

        entityManager.merge(personFromDB);
        return personFromDB;
    }

    @Override
    @Transactional
    public void deletePerson(long personId) {
        Person personFromDB = readPersonById(personId);
        entityManager.remove(personFromDB);
    }

    @Override
    @Transactional
    public void deleteAllPeople() {
        entityManager.createQuery("DELETE FROM Person").executeUpdate();
    }

    @Override
    public Person readPersonById(long personId) {
        Person personFromDB = entityManager.find(Person.class, personId);
        if (personFromDB != null) {
            return personFromDB;
        } else throw new EntityNotFoundException("Person with ID: " + personId + " couldn't be found");
    }

    @Override
    public List<Person> readAllPeople() {
        List<Person> allPeople = entityManager.createQuery("FROM Person", Person.class)
                .getResultList();
        return allPeople;
    }

    @Override
    public Page<Person> realAllPeopleByFirstNameLastname(final String firstName,
                                                         final String lastName,
                                                         final Operation operation,
                                                         final PagingAndSortingContext context) {
        long total = getPersonCount();
        int offset = context.calculateOffset(total);

        List<Person> peopleByFirstOrLastName = entityManager.createQuery(
                        "FROM Person WHERE firstName LIKE :firstName " + operation.toString() + " lastName LIKE :lastName" +
                                " ORDER BY " + context.getSortForDBAccess(),
                        Person.class)
                .setParameter("firstName", "%" + firstName + "%")
                .setParameter("lastName", "%" + lastName + "%")
                .setFirstResult(offset)
                .setMaxResults(context.getSize())
                .getResultList();

        Pageable pageable = context.getPageable();
        return new PageImpl<>(peopleByFirstOrLastName, pageable, total);
    }

    @Override
    public long getLocationOfPersonCount(long personId) {
        return (long) entityManager.createQuery("SELECT COUNT(l) FROM Location l WHERE l.person.id = :personId")
                .setParameter("personId", personId)
                .getSingleResult();
    }

    @Override
    public Page<Location> readAllLocationOfPerson(long personId, PagingAndSortingContext pagingAndSortingContext) {
        readPersonById(personId);
        long total = getLocationOfPersonCount(personId);
        int offset = pagingAndSortingContext.calculateOffset(total);

        List<Location> locationsOfPerson =
                entityManager.createQuery("FROM Location WHERE person.id = :personId" +
                                        " ORDER BY " + pagingAndSortingContext.getSortForDBAccess()
                                , Location.class)
                        .setParameter("personId", personId)
                        .setFirstResult(offset)
                        .setMaxResults(pagingAndSortingContext.getSize())
                        .getResultList();

        Pageable pageable = pagingAndSortingContext.getPageable();
        return new PageImpl<>(locationsOfPerson, pageable, total);
    }

    @Override
    public Location readSingleLocationOfPerson(long personId, long locationId) {
        Location singleLocationOfPerson =
                entityManager.createQuery("FROM Location WHERE person.id = :personId AND id = :locationId",
                                Location.class)
                        .setParameter("personId", personId)
                        .setParameter("locationId", locationId)
                        .getResultStream().findFirst().orElseThrow(() -> {
                            throw new EntityNotFoundException("Location with ID: " + locationId + " is not found for Person" +
                                    " with ID: " + personId);
                        });

        return singleLocationOfPerson;
    }

    @Override
    public Page<Location> readLinkedAndUnlinkedLocationsOfPerson(long personId,
                                                                 PagingAndSortingContext pagingAndSortingContext) {
        readPersonById(personId);

        long total = (long) entityManager.createQuery("SELECT COUNT(*) FROM Location WHERE person.id = :personId OR " +
                        "person.id IS NULL")
                .setParameter("personId", personId)
                .getSingleResult();

        int offset = pagingAndSortingContext.calculateOffset(total);

        List<Location> locations = entityManager.createQuery("FROM Location l WHERE l.person.id = :personId OR " +
                        "l.person.id IS NULL " +
                        "ORDER BY " + pagingAndSortingContext.getSortForDBAccess(), Location.class)
                .setParameter("personId", personId)
                .setFirstResult(offset)
                .setMaxResults(pagingAndSortingContext.getSize())
                .getResultList();

        Pageable pageable = pagingAndSortingContext.getPageable();
        return new PageImpl<>(locations, pageable, total);
    }

    @Override
    @Transactional
    public Location addLocationToPerson(long personId, Location location) {
        Person personFromDB = readPersonById(personId);

        personFromDB.addLocation(location);

        location.setPerson(personFromDB);

        entityManager.merge(personFromDB);

        return location;
    }

    @Override
    @Transactional
    public List<Location> addAllLocationsToPerson(long personId, List<Location> locations) {
        Person personFromDB = readPersonById(personId);

        personFromDB.addAllLocations(locations);

        locations.forEach(l -> l.setPerson(personFromDB));

        entityManager.merge(personFromDB);

        return locations;
    }

    @Override
    @Transactional
    public Location linkLocationToPerson(long personId, long locationId) {
        Location locationFromDB = locationDAO.readLocationById(locationId);
        Person personFromDB = readPersonById(personId);

        if (locationFromDB.getPerson() != null && locationFromDB.getPerson().getId() != personFromDB.getId()) {
            throw new LinkLocationToPersonNotAllowedException("Location with ID: " + locationId + " is already linked " +
                    "to another Person with ID: " + locationFromDB.getPerson().getId());
        }

        personFromDB.addLocation(locationFromDB);
        locationFromDB.setPerson(personFromDB);

        entityManager.merge(personFromDB);
        entityManager.merge(locationFromDB);

        return locationFromDB;
    }

    @Override
    @Transactional
    public Location unlinkLocationFromPerson(long personId, long locationId) {
        try {
            Location locationFromDB = readSingleLocationOfPerson(personId, locationId);
            Person personFromDB = readPersonById(personId);
            locationFromDB.setPerson(null);
            personFromDB.getLocations().removeIf(l -> l.getId() == locationId);

            entityManager.merge(personFromDB);
            entityManager.merge(locationFromDB);

            return locationFromDB;
        } catch (Exception e) {
            throw new LinkLocationToPersonNotAllowedException(e.getMessage());
        }
    }
}
