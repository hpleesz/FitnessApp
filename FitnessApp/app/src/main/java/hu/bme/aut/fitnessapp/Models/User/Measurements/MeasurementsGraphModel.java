package hu.bme.aut.fitnessapp.Models.User.Measurements;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.fitnessapp.Entities.Measurement;

public class MeasurementsGraphModel {
    private DatabaseReference databaseReference;

    public MeasurementsGraphModel() {
    }

    public void initFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Measurements").child(userId);
    }

    public void createMeasurementItems(HashMap<String, Double> new_entries, String date) {
        for(Map.Entry<String, Double> entry : new_entries.entrySet()) {
            databaseReference.child(entry.getKey()).child(date).setValue(entry.getValue());
        }
    }

    public void deleteItem(Measurement item, String body_part) {
        databaseReference.child(body_part).child(item.date).removeValue();

    }
}
