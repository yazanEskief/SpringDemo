package de.fhws.fiw.fds.springDemoApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "person")
@JsonRootName("person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "email_address")
    private String emailAddress;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    @JsonIgnore
    private List<Location> locations;

    public Person() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthdate) {
        this.birthDate = birthdate;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public void addLocation(Location location) {
        if(locations == null) {
            locations = new ArrayList<>();
        }

        locations.add(location);
    }

    public void addAllLocations(Location... locations) {
        Arrays.asList(locations).forEach(this::addLocation);
    }

    public void addAllLocations(final List<Location> locations) {
        if(this.locations == null) {
            this.locations = new ArrayList<>();
        }
        this.locations.addAll(locations);
    }

    public void updatePerson(Person newPerson) {
        this.firstName = newPerson.firstName;
        this.lastName = newPerson.lastName;
        this.birthDate = newPerson.birthDate;
        this.emailAddress = newPerson.emailAddress;
    }

    public void removeLocation(Location locationToRemove) {
        locations.removeIf(location -> location.getId() == locationToRemove.getId());
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthdate=" + birthDate +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
