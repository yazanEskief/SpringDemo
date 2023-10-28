package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Location;
import de.fhws.fiw.fds.springDemoApp.entity.Person;

import java.util.List;

public interface PersonDAO {

    void persistPerson(Person person);

    void updatePerson(long personId, Person person);

    void deletePerson(long personId);

    void deleteAllPeople();

    Person readPersonById(long personId);

    List<Person> readAllPeople();

    List<Person> realAllPeopleByFirstNameOrLastname(String firstName, String lastName);

}
