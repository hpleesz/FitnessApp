package hu.bme.aut.fitnessapp.Models.AdapterModels;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Exercise;

public class ExerciseAdapterModel{



    private ArrayList<Exercise> items;
    private ArrayList<Equipment> equipment;

    public ExerciseAdapterModel(ArrayList<Exercise> items, ArrayList<Equipment> equipment) {
        this.items = items;
        this.equipment = equipment;
    }

    public String makeEquipmentText(int position) {
        String equipments = "";

        for (int i = 0; i < equipment.size(); i++) {
            for (int j = 0; j < equipment.size(); j++) {
                if (items.get(position).equipment1 == equipment.get(i).id && items.get(position).equipment2 == equipment.get(j).id) {
                    if (i == 0) equipments = equipment.get(j).name;
                    else if (j == 0) equipments = equipment.get(i).name;
                    else
                        equipments = equipment.get(i).name + ", " + equipment.get(j).name;

                }
            }
        }
        return equipments;
    }

    public String makeMusclesText(int position) {
        String muscles = items.get(position).muscles[0];
        if (items.get(position).muscles.length > 1) {
            for (int i = 1; i < items.get(position).muscles.length; i++) {
                muscles = muscles + ", " + items.get(position).muscles[i];
            }
        }
        return muscles;
    }

    public ArrayList<Exercise> getItems() {
        return items;
    }
}
