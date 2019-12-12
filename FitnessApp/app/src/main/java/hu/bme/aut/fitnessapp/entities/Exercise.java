package hu.bme.aut.fitnessapp.entities;

import java.io.Serializable;

public class Exercise implements Serializable {
    private Integer id;
    private Integer equipment1;
    private Integer equipment2;
    private String[] muscles;
    private String name;
    private Integer repTime;

    public Exercise() {}

    public Exercise(Integer id, Integer equ1, Integer equ2, String[] muscles, String name, Integer repTime) {
        this.id = id;
        equipment1 = equ1;
        equipment2 = equ2;
        this.muscles = muscles;
        this.name = name;
        this.repTime = repTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEquipment1() {
        return equipment1;
    }

    public void setEquipment1(Integer equipment1) {
        this.equipment1 = equipment1;
    }

    public Integer getEquipment2() {
        return equipment2;
    }

    public void setEquipment2(Integer equipment2) {
        this.equipment2 = equipment2;
    }

    public String[] getMuscles() {
        return muscles;
    }

    public void setMuscles(String[] muscles) {
        this.muscles = muscles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRepTime() {
        return repTime;
    }

    public void setRepTime(Integer repTime) {
        this.repTime = repTime;
    }
}
