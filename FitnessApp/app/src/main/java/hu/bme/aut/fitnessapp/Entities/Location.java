package hu.bme.aut.fitnessapp.Entities;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;

@IgnoreExtraProperties
public class Location extends Place implements Serializable {
    public int id;
    public String name;

    public Location() {}

    public Location(int id, String name, ArrayList<Integer> equipment) {
        this.id = id;
        this.name = name;
        this.equipment = equipment;
    }
}
