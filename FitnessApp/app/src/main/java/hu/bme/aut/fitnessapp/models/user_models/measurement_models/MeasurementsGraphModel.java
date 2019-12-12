package hu.bme.aut.fitnessapp.models.user_models.measurement_models;

import java.util.Map;

import hu.bme.aut.fitnessapp.entities.Measurement;
import hu.bme.aut.fitnessapp.models.database_models.LoadMeasurements;

public class MeasurementsGraphModel {
    private LoadMeasurements loadMeasurements;

    public MeasurementsGraphModel() {
        loadMeasurements = new LoadMeasurements();
    }

    public void createMeasurementItems(Map<String, Double> newEntries, String date) {
        for(Map.Entry<String, Double> entry : newEntries.entrySet()) {
            loadMeasurements.addNewItem(entry.getKey(), date, entry.getValue());
        }
    }

    public void deleteItem(Measurement item, String bodyPart) {
        loadMeasurements.removeItem(bodyPart, item);
    }

    public void removeListeners() {
        if(loadMeasurements != null) loadMeasurements.removeListeners();
    }
}
