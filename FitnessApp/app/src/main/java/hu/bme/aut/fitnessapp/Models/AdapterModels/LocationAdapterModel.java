package hu.bme.aut.fitnessapp.Models.AdapterModels;

import java.util.List;

import hu.bme.aut.fitnessapp.Entities.Location;

public class LocationAdapterModel {

    private List<Location> items;

    public LocationAdapterModel(List<Location> items) {
        this.items = items;
    }

    public List<Location> getItems() {
        return items;
    }
}
