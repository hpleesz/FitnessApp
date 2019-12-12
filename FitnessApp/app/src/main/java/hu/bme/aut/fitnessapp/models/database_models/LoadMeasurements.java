package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.fitnessapp.entities.Measurement;

public class LoadMeasurements extends DatabaseConnection {

    private static final String MEASUREMENTS = "Measurements";

    public interface MeasurementsByBodyPartLoadedListener {
        void onMeasurementsByBodyPartLoaded(ArrayList<Measurement> measurements);
    }
    public interface LastMeasurementsLoadedListener {
        void onLastMeasurementsLoaded(HashMap<String, Double> measurements);
    }
    public interface NewMeasurementsLoadedListener {
        void onNewMeasurementsLoaded(List<ArrayList<Measurement>> measurements);
    }

    private LoadMeasurements.MeasurementsByBodyPartLoadedListener measurementsByBodyPartLoadedListener;
    private LoadMeasurements.LastMeasurementsLoadedListener lastMeasurementsLoadedListener;
    private LoadMeasurements.NewMeasurementsLoadedListener newMeasurementsLoadedListener;

    public LoadMeasurements() {
        super();
    }

    public void setMeasurementsByBodyPartLoadedListener(Object object) {
        measurementsByBodyPartLoadedListener = (LoadMeasurements.MeasurementsByBodyPartLoadedListener)object;
    }

    public void setLastMeasurementsLoadedListener(Object object) {
        lastMeasurementsLoadedListener = (LoadMeasurements.LastMeasurementsLoadedListener)object;
    }

    public void setNewMeasurementsLoadedListener(Object object) {
        newMeasurementsLoadedListener = (LoadMeasurements.NewMeasurementsLoadedListener)object;
    }

    private ValueEventListener loadEventListener;
    private ValueEventListener loadByBodyPartEventListener;
    private ValueEventListener newEventListener;

    private String bodyPart;

    public void loadLastMeasurements(final List<String> bodyParts) {
        loadEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Double> measurements = new HashMap<>();

                for (String part : bodyParts) {
                    DataSnapshot dataSnapshot1 = dataSnapshot.child(part);
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        try {
                            Map<String, Double> entries = (Map) dataSnapshot1.getValue();

                            String key = dataSnapshot2.getKey();
                            double weightValue = entries.get(key);
                            measurements.put(part, weightValue);
                        }
                        catch (Exception e) {
                            Map<String, Long> entries = (Map) dataSnapshot1.getValue();

                            String key = dataSnapshot2.getKey();
                            double weightValue = (double)entries.get(key);
                            measurements.put(part, weightValue);
                        }
                    }
                }

                lastMeasurementsLoadedListener.onLastMeasurementsLoaded(measurements);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }

        };
        getDatabaseReference().child(MEASUREMENTS).child(getUserId()).addValueEventListener(loadEventListener);
    }

    public void loadMeasurementsByBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
        loadByBodyPartEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Measurement> itemlist = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    try {
                        Map<String, Double> entries = (Map) dataSnapshot.getValue();

                        String key = dataSnapshot1.getKey();
                        double weightValue = entries.get(key);
                        Measurement measurement = new Measurement(key, weightValue);
                        itemlist.add(measurement);
                    }
                    catch (Exception e) {
                        Map<String, Long> entries = (Map) dataSnapshot.getValue();

                        String key = dataSnapshot1.getKey();
                        double weightValue = (double)entries.get(key);
                        Measurement measurement = new Measurement(key, weightValue);
                        itemlist.add(measurement);
                    }

                }

                measurementsByBodyPartLoadedListener.onMeasurementsByBodyPartLoaded(itemlist);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }

        };
        getDatabaseReference().child(MEASUREMENTS).child(getUserId()).child(bodyPart).addValueEventListener(loadByBodyPartEventListener);
    }

    public void loadNewMeasurements(final List<String> bodyParts, final List<ArrayList<Measurement>> entries) {
        newEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    String bodyPart = dataSnapshot1.getKey();
                    int idx = bodyParts.indexOf(bodyPart);
                    for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()) {
                        try {
                            Map<String, Double> measurements = (Map) dataSnapshot1.getValue();

                            String key = dataSnapshot2.getKey();
                            double weightValue = measurements.get(key);
                            Measurement measurement = new Measurement(key, weightValue);
                            entries.get(idx).add(measurement);
                        } catch (Exception e) {
                            Map<String, Long> measurements = (Map) dataSnapshot1.getValue();

                            String key = dataSnapshot2.getKey();
                            double weightValue = (double) measurements.get(key);
                            Measurement measurement = new Measurement(key, weightValue);
                            entries.get(idx).add(measurement);
                        }
                    }

                    newMeasurementsLoadedListener.onNewMeasurementsLoaded(entries);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }

        };
        getDatabaseReference().child(MEASUREMENTS).child(getUserId()).addValueEventListener(newEventListener);
    }

    public void addNewItem(String key, String date, double value) {
        getDatabaseReference().child(MEASUREMENTS).child(getUserId()).child(key).child(date).setValue(value);
    }

    public void removeItem(String bodyPart, Measurement item) {
        getDatabaseReference().child(MEASUREMENTS).child(getUserId()).child(bodyPart).child(item.getDate()).removeValue();

    }

    public void removeListeners() {
        if(loadByBodyPartEventListener != null)getDatabaseReference().child(MEASUREMENTS).child(getUserId()).child(bodyPart).removeEventListener(loadByBodyPartEventListener);
        if(newEventListener != null)getDatabaseReference().child(MEASUREMENTS).child(getUserId()).removeEventListener(newEventListener);
        if(loadEventListener != null)getDatabaseReference().child(MEASUREMENTS).child(getUserId()).removeEventListener(loadEventListener);

    }
}
