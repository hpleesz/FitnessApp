package hu.bme.aut.fitnessapp.models.adapter_models;

import java.util.List;

import hu.bme.aut.fitnessapp.entities.Equipment;
import hu.bme.aut.fitnessapp.entities.Exercise;

public class ExerciseAdapterModel{



    private List<Exercise> items;
    private List<Equipment> equipment;

    public ExerciseAdapterModel(List<Exercise> items, List<Equipment> equipment) {
        this.items = items;
        this.equipment = equipment;
    }

    public String makeEquipmentText(int position) {
        String equipments = "";

        for (int i = 0; i < equipment.size(); i++) {
            for (int j = 0; j < equipment.size(); j++) {
                if (items.get(position).getEquipment1() == equipment.get(i).getId() && items.get(position).getEquipment2() == equipment.get(j).getId()) {
                    if (i == 0) equipments = equipment.get(j).getName();
                    else if (j == 0) equipments = equipment.get(i).getName();
                    else
                        equipments = equipment.get(i).getName() + ", " + equipment.get(j).getName();

                }
            }
        }
        return equipments;
    }

    public String makeMusclesText(int position) {
        String muscles = items.get(position).getMuscles()[0];
        if (items.get(position).getMuscles().length > 1) {
            for (int i = 1; i < items.get(position).getMuscles().length; i++) {
                muscles = muscles + ", " + items.get(position).getMuscles()[i];
            }
        }
        return muscles;
    }

    public List<Exercise> getItems() {
        return items;
    }
}
