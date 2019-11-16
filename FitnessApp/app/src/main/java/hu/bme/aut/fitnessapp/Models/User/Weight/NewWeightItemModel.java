package hu.bme.aut.fitnessapp.Models.User.Weight;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hu.bme.aut.fitnessapp.Entities.Measurement;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadWater;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadWeight;

public class NewWeightItemModel implements LoadWeight.WeightLoadedListener{

    private List<Measurement> list;

    public NewWeightItemModel() {
    }


    public void loadWeight() {
        LoadWeight loadWeight = new LoadWeight();
        loadWeight.setListLoadedListener(this);
        loadWeight.loadWeight();
        /*
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Weight").child(userId);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    try {
                        Map<String, Double> water_entries = (Map) dataSnapshot.getValue();

                        String key = dataSnapshot1.getKey();
                        double weight_value = water_entries.get(key);
                        Measurement measurement = new Measurement(key, weight_value);
                        list.add(measurement);
                    }
                    catch (Exception e) {
                        Map<String, Long> water_entries = (Map) dataSnapshot.getValue();

                        String key = dataSnapshot1.getKey();
                        double weight_value = (double)water_entries.get(key);
                        Measurement measurement = new Measurement(key, weight_value);
                        list.add(measurement);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.addValueEventListener(eventListener);

         */
    }

    @Override
    public void onWeightLoaded(ArrayList<Measurement> weight) {
        list = weight;
    }

    public boolean alreadyExists(Measurement item) {
        for (int i = 0; i < list.size(); i++) {
            if (item.date.equals(list.get(i).date))
                return true;
        }
        return false;
    }

}
