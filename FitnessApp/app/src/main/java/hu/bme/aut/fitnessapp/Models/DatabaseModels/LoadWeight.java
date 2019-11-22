package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import hu.bme.aut.fitnessapp.Entities.Measurement;

public class LoadWeight extends DatabaseConnection {

    public interface WeightLoadedListener {
        void onWeightLoaded(ArrayList<Measurement> weight);
    }

    public interface CurrentWeightLoadedListener {
        void onCurrentWeightLoaded(double weight);
    }

    private LoadWeight.WeightLoadedListener listLoadedListener;
    private LoadWeight.CurrentWeightLoadedListener currentWeightLoadedListener;

    public LoadWeight() {
        super();
    }

    public void setListLoadedListener(Object object) {
        listLoadedListener = (LoadWeight.WeightLoadedListener)object;
    }

    public void setCurrentWeightLoadedListener(Object object) {
        currentWeightLoadedListener = (LoadWeight.CurrentWeightLoadedListener)object;
    }

    public void loadWeight() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Measurement> itemlist = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    try {
                        Map<String, Double> entries = (Map) dataSnapshot.getValue();

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
                    //checkProgress();

                }

                listLoadedListener.onWeightLoaded(itemlist);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        getDatabaseReference().child("Weight").child(getUserId()).addValueEventListener(eventListener);

    }

    public void loadCurrentWeight() {
        Query lastQuery = getDatabaseReference().child("Weight").child(getUserId()).orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = "";
                for(DataSnapshot item: dataSnapshot.getChildren()) {
                    key = item.getKey();
                }
                //Map<String, Double> weight = (Map) dataSnapshot.getValue();
                //current_weight = weight.get(key);
                double current_weight;

                try {
                    Map<String, Double> weight = (Map) dataSnapshot.getValue();
                    current_weight = weight.get(key);

                }
                catch(Exception e) {
                    Map<String, Long> weight = (Map) dataSnapshot.getValue();
                    current_weight = (double)weight.get(key);
                }

                currentWeightLoadedListener.onCurrentWeightLoaded(current_weight);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    public void addNewItem(long date, double value) {
        getDatabaseReference().child("Weight").child(getUserId()).child(Long.toString(date)).setValue(value);
    }

    public  void removeItem(long date) {
        getDatabaseReference().child("Weight").child(getUserId()).child(Long.toString(date)).removeValue();
    }

}
