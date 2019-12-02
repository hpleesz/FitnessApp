package hu.bme.aut.fitnessapp.Models.AdapterModels;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Controllers.Adapters.EquipmentAdapter;
import hu.bme.aut.fitnessapp.Entities.Equipment;

public class EquipmentAdapterModel {

    private ArrayList<Equipment> items;
    private ArrayList<Integer> clicked;

    public EquipmentAdapterModel(ArrayList<Equipment> items) {
        this.items = items;
        clicked = new ArrayList<>();
        for(int i = 0; i < items.size(); i++) {
            clicked.add(0);
        }
    }

    public ArrayList<Integer> getCheckedEquipmentList() {
        ArrayList<Integer> selected = new ArrayList<>();
        for(int i = 0; i < clicked.size(); i++){
            if(clicked.get(i) == 1){
                selected.add(items.get(i).id);
            }
        }
        return selected;
    }

    public ArrayList<Equipment> getItems() {
        return items;
    }

    public ArrayList<Integer> getCheckedItems() {
        return clicked;
    }

    public void check(int pos) {
        clicked.set(pos, 1);
    }

    public void uncheck(int pos) {
        clicked.set(pos, 0);
    }

    public void setCheckedEquipmentList(ArrayList<Integer> equipments) {
        for(int i = 0; i < items.size(); i++){
            for(int j = 0; j < equipments.size(); j++) {
                if (items.get(i).id == (equipments.get(j)))
                    check(i);
            }
        }
    }
}
