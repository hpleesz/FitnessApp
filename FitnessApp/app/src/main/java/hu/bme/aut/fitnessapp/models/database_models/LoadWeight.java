package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import hu.bme.aut.fitnessapp.entities.Measurement;

public class LoadWeight extends DatabaseConnection {

    private static final String WEIGHT = "Weight";

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

    private ValueEventListener eventListener;

    public void loadWeight() {
        eventListener = new ValueEventListener() {
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
                        double weightValue = (double) entries.get(key);
                        Measurement measurement = new Measurement(key, weightValue);
                        itemlist.add(measurement);
                    }

                }

                listLoadedListener.onWeightLoaded(itemlist);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }

        };
        getDatabaseReference().child(WEIGHT).child(getUserId()).addValueEventListener(eventListener);

    }

    public void loadCurrentWeight() {
        Query lastQuery = getDatabaseReference().child(WEIGHT).child(getUserId()).orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = "";
                for(DataSnapshot item: dataSnapshot.getChildren()) {
                    key = item.getKey();
                }

                double currentWeight;

                try {
                    Map<String, Double> weight = (Map) dataSnapshot.getValue();
                    currentWeight = weight.get(key);

                }
                catch(Exception e) {
                    Map<String, Long> weight = (Map) dataSnapshot.getValue();
                    currentWeight = (double)weight.get(key);
                }

                currentWeightLoadedListener.onCurrentWeightLoaded(currentWeight);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }
        });
    }

    public void addNewItem(long date, double value) {
        getDatabaseReference().child(WEIGHT).child(getUserId()).child(Long.toString(date)).setValue(value);
    }

    public  void removeItem(long date) {
        getDatabaseReference().child(WEIGHT).child(getUserId()).child(Long.toString(date)).removeValue();
    }

    public void removeListeners() {
        if(eventListener != null) getDatabaseReference().child(WEIGHT).child(getUserId()).removeEventListener(eventListener);
    }
}
