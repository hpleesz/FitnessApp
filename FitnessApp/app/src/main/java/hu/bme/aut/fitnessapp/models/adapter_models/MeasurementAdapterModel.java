package hu.bme.aut.fitnessapp.models.adapter_models;

import java.util.List;

import hu.bme.aut.fitnessapp.entities.Measurement;

public class MeasurementAdapterModel {

    private List<Measurement> items;
    private String bodyPart;

    public MeasurementAdapterModel(List<Measurement> items, String bodyPart) {
        this.items = items;
        this.bodyPart = bodyPart;
    }

    public List<Measurement> getItems() {
        return items;
    }

    public String getBodyPart() {
        return bodyPart;
    }
}
