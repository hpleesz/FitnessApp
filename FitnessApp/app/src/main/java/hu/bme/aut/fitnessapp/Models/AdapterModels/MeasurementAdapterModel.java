package hu.bme.aut.fitnessapp.Models.AdapterModels;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Measurement;

public class MeasurementAdapterModel {

    private ArrayList<Measurement> items;
    private String body_part;

    public MeasurementAdapterModel(ArrayList<Measurement> items, String body_part) {
        this.items = items;
        this.body_part = body_part;
    }

    public ArrayList<Measurement> getItems() {
        return items;
    }

    public String getBody_part() {
        return body_part;
    }
}
