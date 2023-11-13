package de.fhws.fiw.fds.springDemoApp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import de.fhws.fiw.fds.springDemoApp.caching.EtagGenerator;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "location")
@JsonRootName("location")
public class Location extends AbstractEntity {

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "visitedOn")
    private LocalDate visitedOn;

    @ManyToOne()
    @JoinColumn(name = "person_id")
    @JsonIgnore
    private Person person;

    public Location() {
    }

    @Override
    public String getEtag(EtagGenerator etagGenerator) {
        try {
            Location clone = (Location) clone();
            clone.setPerson(null);
            return etagGenerator.generateEtag(clone);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public LocalDate getVisitedOn() {
        return visitedOn;
    }

    public void setVisitedOn(LocalDate visitedOn) {
        this.visitedOn = visitedOn;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void updateLocation(final Location newLocation) {
        this.cityName = newLocation.cityName;
        this.latitude = newLocation.latitude;
        this.longitude = newLocation.longitude;
        this.visitedOn = newLocation.visitedOn;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", cityName='" + cityName + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", visitedOn=" + visitedOn +
                '}';
    }
}
