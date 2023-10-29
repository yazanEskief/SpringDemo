package de.fhws.fiw.fds.springDemoApp.util;

import de.fhws.fiw.fds.springDemoApp.entity.Location;
import de.fhws.fiw.fds.springDemoApp.entity.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataFaker {

    public static Person createPerson() {
        Random random = new Random();

        String[] firstNames = new String[]{"Yazan", "Haya", "Maged", "Maha", "Leen", "Peter", "Manuel",
        "Daniela", "Marco", "Filippo", "Angela", "Khaled", "Frances"};
        String[] lastNames = new String[] {"Eskief", "Rodaro", "Gambirasi", "Braun", "Balash", "Ronaldo"};
        String[] emails = new String[] {"yahoo", "hotmail", "gamil"};

        int year = random.nextInt(1950, LocalDate.now().getYear() -18);
        int month = random.nextInt(1, 13);
        int day = random.nextInt(1, 29);
        int firstNameIndex = random.nextInt(0, firstNames.length);
        int lastNameIndex = random.nextInt(0, lastNames.length);
        int emailIndex = random.nextInt(0, emails.length);

        String firstName = firstNames[firstNameIndex];
        String lastName = lastNames[lastNameIndex];
        String email = firstName + "." + lastName + "@" + emails[emailIndex] + ".com";
        LocalDate birthdate = LocalDate.of(year, month, day);

        Person result = new Person();
        result.setFirstName(firstName);
        result.setLastName(lastName);
        result.setEmailAddress(email);
        result.setBirthDate(birthdate);

        return result;
    }

    public static Location createlocation(final Person person) {
        Random random = new Random();

        String[] cities = new String[] {"New York", "London", "Aleppo", "Stuttgart", "Damascus", "Munich",
        "Wuerzburg"};
        double latitude = random.nextDouble(-90.0, 91.0);
        double longitude = random.nextDouble(-90.0, 91.0);
        int cityIndex = random.nextInt(0, cities.length);

        int year = random.nextInt(person.getBirthDate().getYear() +10, LocalDate.now().getYear());
        int month = random.nextInt(1, 13);
        int day = random.nextInt(1, 29);

        LocalDate visitedOn = LocalDate.of(year, month, day);
        String cityName = cities[cityIndex];

        Location location = new Location();
        location.setCityName(cityName);
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        location.setVisitedOn(visitedOn);

        return location;
    }

    public static List<Person> generatePeopleWithLocations(int peopleNr, int locationForEachPerson) {
        List<Person> people = IntStream.range(0, peopleNr)
                .mapToObj(i -> DataFaker.createPerson())
                .collect(Collectors.toList());

        people.forEach(p -> {
            List<Location> locations = IntStream.range(0, locationForEachPerson)
                    .mapToObj(i -> DataFaker.createlocation(p))
                    .map(l -> {
                        l.setPerson(p);
                        return l;
                    })
                    .collect(Collectors.toList());
            p.setLocations(locations);
        });

        return people;
    }
}
