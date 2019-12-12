package hu.bme.aut.fitnessapp.models.adapter_models;

import java.util.List;

import hu.bme.aut.fitnessapp.entities.Measurement;

public class WeightAdapterModel {

    private List<Measurement> items;

    public WeightAdapterModel(List<Measurement> items) {
        this.items = items;
    }

    public List<Measurement> getItems() {
        return items;
    }
}
