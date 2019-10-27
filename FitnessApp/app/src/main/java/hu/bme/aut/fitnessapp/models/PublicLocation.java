package hu.bme.aut.fitnessapp.models;

import java.io.Serializable;
import java.util.ArrayList;

public class PublicLocation extends Place implements Serializable {
    public long id;
    public String name;
    public ArrayList<String[]> open_hours;
    public String description;
    public String zip;
    public String country;
    public String city;
    public String address;
    public String creator;

    public PublicLocation() {}

    public PublicLocation(long id, String name, ArrayList<Integer> equipment, ArrayList<String[]> open_hours, String description,
                          String zip, String country, String city, String address, String creator) {
        this.id = id;
        this.name = name;
        this.equipment = equipment;
        this.open_hours = open_hours;
        this.description = description;
        this.zip = zip;
        this.country = country;
        this.city = city;
        this.address = address;
        this.creator = creator;
    }
}
