package hu.bme.aut.fitnessapp.Models.DatabaseLoad;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.fitnessapp.Entities.Measurement;

public class LoadMeasurements extends DatabaseConnection {

    public interface MeasurementsByBodyPartLoadedListener {
        void onMeasurementsByBodyPartLoaded(ArrayList<Measurement> measurements);
    }
    public interface LastMeasurementsLoadedListener {
        void onLastMeasurementsLoaded(HashMap<String, Double> measurements);
    }
    public interface NewMeasurementsLoadedListener {
        void onNewMeasurementsLoaded(ArrayList<ArrayList<Measurement>> measurements);
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

    public void loadLastMeasurements(final ArrayList<String> body_parts) {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Double> measurements = new HashMap<>();

                for (String body_part : body_parts) {
                    DataSnapshot dataSnapshot1 = dataSnapshot.child(body_part);
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        try {
                            Map<String, Double> entries = (Map) dataSnapshot1.getValue();

                            //double weight_value = (double)dataSnapshot1.getValue();
                            String key = dataSnapshot2.getKey();
                            double weight_value = entries.get(key);
                            measurements.put(body_part, weight_value);
                        }
                        catch (Exception e) {
                            Map<String, Long> entries = (Map) dataSnapshot1.getValue();

                            String key = dataSnapshot2.getKey();
                            double weight_value = (double)entries.get(key);
                            measurements.put(body_part, weight_value);
                        }
                    }
                }

                lastMeasurementsLoadedListener.onLastMeasurementsLoaded(measurements);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        getDatabaseReference().child("Measurements").child(getUserId()).addValueEventListener(eventListener);
    }

    public void loadMeasurementsByBodyPart(String bodyPart) {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Measurement> itemlist = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    try {
                        Map<String, Double> entries = (Map) dataSnapshot.getValue();

                        //double weight_value = (double)dataSnapshot1.getValue();
                        String key = dataSnapshot1.getKey();
                        double weight_value = entries.get(key);
                        Measurement measurement = new Measurement(key, weight_value);
                        itemlist.add(measurement);
                    }
                    catch (Exception e) {
                        Map<String, Long> entries = (Map) dataSnapshot.getValue();

                        String key = dataSnapshot1.getKey();
                        double weight_value = (double)entries.get(key);
                        Measurement measurement = new Measurement(key, weight_value);
                        itemlist.add(measurement);
                    }

                }

                measurementsByBodyPartLoadedListener.onMeasurementsByBodyPartLoaded(itemlist);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        getDatabaseReference().child("Measurements").child(getUserId()).child(bodyPart).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

    public void loadNewMeasurements(final ArrayList<String> body_parts, final ArrayList<ArrayList<Measurement>> entries) {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //body parts
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    String body_part = dataSnapshot1.getKey();
                    int idx = body_parts.indexOf(body_part);
                    //entries
                    for(DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()) {
                        try {
                            Map<String, Double> water_entries = (Map) dataSnapshot1.getValue();

                            String key = dataSnapshot2.getKey();
                            double weight_value = water_entries.get(key);
                            Measurement measurement = new Measurement(key, weight_value);
                            entries.get(idx).add(measurement);
                            //list.add(measurement);
                        } catch (Exception e) {
                            Map<String, Long> water_entries = (Map) dataSnapshot1.getValue();

                            String key = dataSnapshot2.getKey();
                            double weight_value = (double) water_entries.get(key);
                            Measurement measurement = new Measurement(key, weight_value);
                            entries.get(idx).add(measurement);
                            //list.add(measurement);
                        }
                    }

                    newMeasurementsLoadedListener.onNewMeasurementsLoaded(entries);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        getDatabaseReference().child("Measurements").child(getUserId()).addValueEventListener(eventListener);

    }
}
