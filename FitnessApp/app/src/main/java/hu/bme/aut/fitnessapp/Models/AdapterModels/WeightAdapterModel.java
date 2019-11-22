package hu.bme.aut.fitnessapp.Models.AdapterModels;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Measurement;

public class WeightAdapterModel {

    private ArrayList<Measurement> items;

    public WeightAdapterModel(ArrayList<Measurement> items) {
        this.items = items;
    }

    public ArrayList<Measurement> getItems() {
        return items;
    }
}
