package hu.bme.aut.fitnessapp.Models;

import java.io.Serializable;

public class Equipment implements Serializable {
    public int id;
    public String name;

    public Equipment(){}

    public Equipment(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
