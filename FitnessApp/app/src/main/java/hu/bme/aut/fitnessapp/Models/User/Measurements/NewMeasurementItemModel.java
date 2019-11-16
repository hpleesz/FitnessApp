package hu.bme.aut.fitnessapp.Models.User.Measurements;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.fitnessapp.Entities.Measurement;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadBodyParts;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadMeasurements;

public class NewMeasurementItemModel implements LoadBodyParts.BodyPartsLoadedListener, LoadMeasurements.NewMeasurementsLoadedListener{
    public ArrayList<String> body_parts;

    private ArrayList<ArrayList<Measurement>> entries;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    public String getAlreadyExists() {
        return alreadyExists;
    }

    private String alreadyExists;
    private String date;


    public NewMeasurementItemModel() {
        alreadyExists = "";
        entries = new ArrayList<>();

        //loadBodyParts();
    }

    public void loadBodyParts() {
        LoadBodyParts loadBodyParts = new LoadBodyParts(this);
        loadBodyParts.loadBodyParts();
        /*
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                body_parts = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String body_part = (String) dataSnapshot1.getValue();
                    body_parts.add(body_part);
                }
                loadData();
                for(int i = 0; i < body_parts.size(); i++) {
                    entries.add(new ArrayList<Measurement>());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Body_Parts").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;

         */
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
        LoadMeasurements loadMeasurements = new LoadMeasurements();
        loadMeasurements.setNewMeasurementsLoadedListener(this);
        loadMeasurements.loadNewMeasurements(body_parts, entries);

        /*
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Measurements").child(userId).addValueEventListener(eventListener);
         */
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

}
