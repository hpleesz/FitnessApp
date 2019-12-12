package hu.bme.aut.fitnessapp.models.adapter_models;

import java.util.List;

import hu.bme.aut.fitnessapp.entities.Location;

public class LocationAdapterModel {

    private List<Location> items;

    public LocationAdapterModel(List<Location> items) {
        this.items = items;
    }

    public List<Location> getItems() {
        return items;
    }
}
