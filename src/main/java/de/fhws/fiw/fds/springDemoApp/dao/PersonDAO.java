package de.fhws.fiw.fds.springDemoApp.dao;

import de.fhws.fiw.fds.springDemoApp.entity.Person;
import de.fhws.fiw.fds.springDemoApp.sortingAndPagination.PagingAndSortingContext;
import de.fhws.fiw.fds.springDemoApp.util.Operation;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PersonDAO {

    Person persistPerson(Person person);

    long getPersonCount();

    Person updatePerson(long personId, Person person);

    void deletePerson(long personId);

    void deleteAllPeople();

    Person readPersonById(long personId);

    List<Person> readAllPeople();

    Page<Person> realAllPeopleByFirstNameLastname(final String firstName,
                                                  final String lastName,
                                                  final Operation operation,
                                                  final PagingAndSortingContext pagingContext);

}
