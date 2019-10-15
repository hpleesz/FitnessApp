package hu.bme.aut.fitnessapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Location implements Serializable {
    public int id;
    public String name;
    public ArrayList<Integer> equipment;

    public Location() {}

    public Location(int id, String name, ArrayList<Integer> equipment) {
        this.id = id;
        this.name = name;
        this.equipment = equipment;
    }
}
