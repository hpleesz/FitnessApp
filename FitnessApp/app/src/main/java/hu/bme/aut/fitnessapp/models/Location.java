package hu.bme.aut.fitnessapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Location {
    public String name;
    public List<Integer> equipment;

    public Location() {}

    public Location(String name, List<Integer> equipment) {
        this.name = name;
        this.equipment = equipment;
    }
}
