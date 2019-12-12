package hu.bme.aut.fitnessapp.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PublicLocation extends Place implements Serializable {
    private long id;
    private String name;
    private List<String[]> openHours;
    private String description;
    private String zip;
    private String country;
    private String city;
    private String address;
    private String creator;

    public PublicLocation() {}

    public PublicLocation(long id, String name, List<Integer> equipment, List<String[]> openHours, String description,
                          String zip, String country, String city, String address, String creator) {
        this.id = id;
        this.name = name;
        setEquipment(equipment);
        this.openHours = openHours;
        this.description = description;
        this.zip = zip;
        this.country = country;
        this.city = city;
        this.address = address;
        this.creator = creator;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String[]> getOpenHours() {
        return openHours;
    }

    public void setOpenHours(List<String[]> openHours) {
        this.openHours = openHours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
