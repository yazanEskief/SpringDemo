package de.fhws.fiw.fds.springDemoApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import de.fhws.fiw.fds.springDemoApp.caching.EtagGenerator;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "person")
@JsonRootName("person")
public class Person extends AbstractEntity {

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

    public Person(long id, String firstName, String lastName, LocalDate birthDate, String emailAddress, LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.emailAddress = emailAddress;
        this.updatedAt = updatedAt;
    }

    @Override
    public String getEtag(EtagGenerator etagGenerator) {
        try {
            Person clone = (Person) this.clone();
            clone.setLocations(null);
            return etagGenerator.generateEtag(clone);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
        if (locations == null) {
            locations = new ArrayList<>();
        }

        locations.add(location);
    }

    public void addAllLocations(Location... locations) {
        Arrays.asList(locations).forEach(this::addLocation);
    }

    public void addAllLocations(final List<Location> locations) {
        if (this.locations == null) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (getId() != person.getId()) return false;
        if (getFirstName() != null ? !getFirstName().equals(person.getFirstName()) : person.getFirstName() != null)
            return false;
        if (getLastName() != null ? !getLastName().equals(person.getLastName()) : person.getLastName() != null)
            return false;
        if (getBirthDate() != null ? !getBirthDate().equals(person.getBirthDate()) : person.getBirthDate() != null)
            return false;
        if (getEmailAddress() != null ? !getEmailAddress().equals(person.getEmailAddress()) : person.getEmailAddress() != null)
            return false;
        if (!getCreatedAt().equals(person.getCreatedAt())) return false;
        return getUpdatedAt() != null ? getUpdatedAt().equals(person.getUpdatedAt()) : person.getUpdatedAt() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getFirstName() != null ? getFirstName().hashCode() : 0);
        result = 31 * result + (getLastName() != null ? getLastName().hashCode() : 0);
        result = 31 * result + (getBirthDate() != null ? getBirthDate().hashCode() : 0);
        result = 31 * result + (getEmailAddress() != null ? getEmailAddress().hashCode() : 0);
        result = 31 * result + getCreatedAt().hashCode();
        result = 31 * result + (getUpdatedAt() != null ? getUpdatedAt().hashCode() : 0);
        return result;
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
