package hu.bme.aut.fitnessapp.models.user_models.measurement_models;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.fitnessapp.entities.Measurement;
import hu.bme.aut.fitnessapp.models.database_models.LoadBodyParts;
import hu.bme.aut.fitnessapp.models.database_models.LoadMeasurements;

public class NewMeasurementItemModel implements LoadBodyParts.BodyPartsLoadedListener, LoadMeasurements.NewMeasurementsLoadedListener{

    private List<String> bodyParts;
    private List<ArrayList<Measurement>> entries;

    private String alreadyExists;
    private String date;

    private LoadBodyParts loadBodyParts;
    private LoadMeasurements loadMeasurements;

    public NewMeasurementItemModel() {
        alreadyExists = "";
        entries = new ArrayList<>();
    }

    public void loadBodyParts() {
        loadBodyParts = new LoadBodyParts(this);
        loadBodyParts.loadBodyParts();
    }

    @Override
    public void onBodyPartsLoaded(ArrayList<String> bodyParts) {
        this.bodyParts = bodyParts;
        loadData();
        for(int i = 0; i < bodyParts.size(); i++) {
            entries.add(new ArrayList<Measurement>());
        }
    }

    private void loadData() {
        loadMeasurements = new LoadMeasurements();
        loadMeasurements.setNewMeasurementsLoadedListener(this);
        loadMeasurements.loadNewMeasurements(bodyParts, entries);

    }

    @Override
    public void onNewMeasurementsLoaded(List<ArrayList<Measurement>> measurements) {
        entries = measurements;
    }

    public Map<String, Double> getMeasurementItems(List<String> measurements, String date) {
        this.date = date;
        HashMap<String, Double> newEntries = new HashMap<>();

        for (int i = 0; i < bodyParts.size(); i++) {
            double value = 0;
            try {
                value = Double.parseDouble(measurements.get(i));
            } catch (NumberFormatException f) {
                value = -1;
            }

            if (value != -1)
                newEntries.put(bodyParts.get(i), value);

        }
        return newEntries;
    }

    public boolean alreadyExists(Map<String, Double> items) {
        boolean exists = false;

        for (Map.Entry<String, Double> entry : items.entrySet()) {
            String key = entry.getKey();
            int idx = bodyParts.indexOf(key);
            for(Measurement measurement : entries.get(idx)) {
                if(measurement.getDate().equals(date)) {
                    if (!alreadyExists.equals(""))
                        alreadyExists = alreadyExists + ", " + key;
                    else
                        alreadyExists = key;
                    exists = true;
                }
            }

        }
        return exists;
    }

    public void setBodyParts(List<String> bodyParts) {
        this.bodyParts = bodyParts;
    }

    public void setEntries(ArrayList<ArrayList<Measurement>> entries) {
        this.entries = entries;
    }

    public void setAlreadyExists(String alreadyExists) {
        this.alreadyExists = alreadyExists;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAlreadyExists() {
        return alreadyExists;
    }

    public void removeListeners() {
        if(loadBodyParts != null) loadBodyParts.removeListeners();
        if(loadMeasurements != null) loadMeasurements.removeListeners();
    }
}
