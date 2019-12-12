package hu.bme.aut.fitnessapp.entities;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class Location extends Place implements Serializable {

    private int id;
    private String name;

    public Location() {}

    public Location(int id, String name, List<Integer> equipment) {
        this.id = id;
        this.name = name;
        setEquipment(equipment);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
