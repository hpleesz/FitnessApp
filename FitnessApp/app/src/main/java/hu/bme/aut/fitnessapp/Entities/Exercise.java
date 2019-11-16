package hu.bme.aut.fitnessapp.Entities;

import java.io.Serializable;

public class Exercise implements Serializable {
    public Integer id;
    public Integer equipment1;
    public Integer equipment2;
    public String[] muscles;
    public String name;
    public Integer rep_time;

    public Exercise() {}

    public Exercise(Integer id, Integer equ1, Integer equ2, String[] muscles, String name, Integer rep_time) {
        this.id = id;
        equipment1 = equ1;
        equipment2 = equ2;
        this.muscles = muscles;
        this.name = name;
        this.rep_time = rep_time;
    }
}
