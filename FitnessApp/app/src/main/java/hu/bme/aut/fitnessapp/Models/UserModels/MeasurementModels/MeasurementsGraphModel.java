package hu.bme.aut.fitnessapp.Models.UserModels.MeasurementModels;

import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.fitnessapp.Entities.Measurement;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadMeasurements;

public class MeasurementsGraphModel {
    private LoadMeasurements loadMeasurements;

    public MeasurementsGraphModel() {
        loadMeasurements = new LoadMeasurements();
    }

    public void createMeasurementItems(HashMap<String, Double> new_entries, String date) {
        for(Map.Entry<String, Double> entry : new_entries.entrySet()) {
            loadMeasurements.addNewItem(entry.getKey(), date, entry.getValue());
        }
    }

    public void deleteItem(Measurement item, String body_part) {
        loadMeasurements.removeItem(body_part, item);
    }
}
