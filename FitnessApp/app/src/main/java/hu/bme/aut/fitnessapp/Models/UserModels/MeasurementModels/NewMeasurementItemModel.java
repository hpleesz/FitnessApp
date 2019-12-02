package hu.bme.aut.fitnessapp.Models.UserModels.MeasurementModels;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.fitnessapp.Entities.Measurement;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadBodyParts;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadMeasurements;

public class NewMeasurementItemModel implements LoadBodyParts.BodyPartsLoadedListener, LoadMeasurements.NewMeasurementsLoadedListener{

    private ArrayList<String> body_parts;
    private ArrayList<ArrayList<Measurement>> entries;

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
        body_parts = bodyParts;
        loadData();
        for(int i = 0; i < body_parts.size(); i++) {
            entries.add(new ArrayList<Measurement>());
        }
    }

    private void loadData() {
        loadMeasurements = new LoadMeasurements();
        loadMeasurements.setNewMeasurementsLoadedListener(this);
        loadMeasurements.loadNewMeasurements(body_parts, entries);

    }

    @Override
    public void onNewMeasurementsLoaded(ArrayList<ArrayList<Measurement>> measurements) {
        entries = measurements;
    }

    public HashMap<String, Double> getMeasurementItems(ArrayList<String> measurements, String date) {
        this.date = date;
        HashMap<String, Double> new_entries = new HashMap<>();

        for (int i = 0; i < body_parts.size(); i++) {
            double value = 0;
            try {
                value = Double.parseDouble(measurements.get(i));
            } catch (NumberFormatException f) {
                value = -1;
            }

            if (value != -1)
                new_entries.put(body_parts.get(i), value);

        }
        return new_entries;
    }

    public boolean alreadyExists(HashMap<String, Double> items) {
        boolean exists = false;

        for (Map.Entry<String, Double> entry : items.entrySet()) {
            String key = entry.getKey();
            int idx = body_parts.indexOf(key);
            for(Measurement measurement : entries.get(idx)) {
                if(measurement.date.equals(date)) {
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

    public void setBody_parts(ArrayList<String> body_parts) {
        this.body_parts = body_parts;
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
