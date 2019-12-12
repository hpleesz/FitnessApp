package hu.bme.aut.fitnessapp.entities;

import java.io.Serializable;
import java.util.List;

public abstract class Place implements Serializable {

    private List<Integer> equipment;

    public List<Integer> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Integer> equipment) {
        this.equipment = equipment;
    }


}
