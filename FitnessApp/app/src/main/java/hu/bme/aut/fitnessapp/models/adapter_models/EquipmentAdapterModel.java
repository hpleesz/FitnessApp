package hu.bme.aut.fitnessapp.models.adapter_models;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.entities.Equipment;

public class EquipmentAdapterModel {

    private List<Equipment> items;
    private ArrayList<Integer> clicked;

    public EquipmentAdapterModel(List<Equipment> items) {
        this.items = items;
        clicked = new ArrayList<>();
        for(int i = 0; i < items.size(); i++) {
            clicked.add(0);
        }
    }

    public List<Integer> getCheckedEquipmentList() {
        ArrayList<Integer> selected = new ArrayList<>();
        for(int i = 0; i < clicked.size(); i++){
            if(clicked.get(i) == 1){
                selected.add(items.get(i).getId());
            }
        }
        return selected;
    }

    public List<Equipment> getItems() {
        return items;
    }

    public List<Integer> getCheckedItems() {
        return clicked;
    }

    public void check(int pos) {
        clicked.set(pos, 1);
    }

    public void uncheck(int pos) {
        clicked.set(pos, 0);
    }

    public void setCheckedEquipmentList(List<Integer> equipments) {
        for(int i = 0; i < items.size(); i++){
            for(int j = 0; j < equipments.size(); j++) {
                if (items.get(i).getId() == (equipments.get(j)))
                    check(i);
            }
        }
    }
}
