package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Location;
import de.fhws.fiw.fds.springDemoApp.entity.Person;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PersonDAOImpl implements PersonDAO, PersonLocationDAO {
    private EntityManager entityManager;

    public PersonDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void persistPerson(Person person) {
        entityManager.persist(person);
    }

    @Override
    @Transactional
    public void updatePerson(long personId, Person person) {
        Person personFromDB = readPersonById(personId);

        personFromDB.updatePerson(person);

        entityManager.merge(personFromDB);
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
        } else throw new RuntimeException("Person with ID: " + personId + " couldn't be found");
    }

    @Override
    public List<Person> readAllPeople() {
        List<Person> allPeople = entityManager.createQuery("FROM Person", Person.class)
                .getResultList();
        return allPeople;
    }

    @Override
    public List<Person> realAllPeopleByFirstNameOrLastname(String firstName, String lastName) {
        List<Person> peopleByFirstOrLastName = entityManager.createQuery("""
                        FROM Person p
                        WHERE p.firstName LIKE :firstName OR p.lastName LIKE :lastName
                        """, Person.class)
                .setParameter("firstName", "%" + firstName + "%")
                .setParameter("lastName", "%" + lastName + "%")
                .getResultList();
        return peopleByFirstOrLastName;
    }

    @Override
    public List<Location> readAllLocationOfPerson(long personId) {
        List<Location> locationsOfPerson = entityManager.createQuery("FROM Location WHERE person.id = :personId")
                .setParameter("personId", personId)
                .getResultList();
        return locationsOfPerson;
    }

    @Override
    public Location readSingleLocationOfPerson(long personId, long locationId) {
        Location singleLocationOfPerson = entityManager.createQuery("FROM Location WHERE person.id = :personId AND id = :locationId"
                        , Location.class)
                .setParameter("personId", personId)
                .setParameter("locationId", locationId)
                .getSingleResult();
        if(singleLocationOfPerson == null) {
            throw new RuntimeException("Location with id: " + locationId + " for person with id: " + personId + " not found");
        }
        return singleLocationOfPerson;
    }

    @Override
    @Transactional
    public void addLocationToPerson(long personId, Location location) {
        Person personFromDB = readPersonById(personId);

        personFromDB.addLocation(location);

        location.setPerson(personFromDB);

        entityManager.merge(personFromDB);
    }

    @Override
    @Transactional
    public void addAllLocationsToPerson(long personId, List<Location> locations) {
        Person personFromDB = readPersonById(personId);

        personFromDB.addAllLocations(locations);

        locations.forEach(l -> l.setPerson(personFromDB));

        entityManager.merge(personFromDB);
    }

    @Override
    @Transactional
    public void updateLocationOfPerson(long personId, long locationId, Location location) {
        Location locationFromDB = readSingleLocationOfPerson(personId, locationId);

        locationFromDB.updateLocation(location);

        entityManager.merge(locationFromDB);
    }

    @Override
    @Transactional
    public void deleteLocationOfPerson(long personId, long locationId) {
        Location locationFromDB = readSingleLocationOfPerson(personId, locationId);

        entityManager.remove(locationFromDB);
    }
}
