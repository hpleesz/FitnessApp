package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Map;

public class LoadWater extends DatabaseConnection {

    public interface WaterLoadedListener {
        void onWaterLoaded(double water);
    }

    private LoadWater.WaterLoadedListener listLoadedListener;

    public LoadWater() {
        super();
    }

    public void setListLoadedListener(Object object) {
        listLoadedListener = (LoadWater.WaterLoadedListener)object;

    }

    public void loadWaterToday() {
        Query lastWaterQuery = getDatabaseReference().child("Water").child(getUserId()).orderByKey().limitToLast(1);
        lastWaterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = "";
                for(DataSnapshot item: dataSnapshot.getChildren()) {
                    key = item.getKey();
                }

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(calendar.YEAR);
                int month = calendar.get(calendar.MONTH);
                int day = calendar.get(calendar.DAY_OF_MONTH);

                calendar.set(year, month, day, 0,0,0);
                long today = calendar.getTimeInMillis() / 1000;

                double water;

                if(!key.equals("") && Long.parseLong(key) == today) {
                    try {
                        Map<String, Double> water_entries = (Map) dataSnapshot.getValue();
                        water = water_entries.get(Long.toString(today));

                    }
                    catch(Exception e) {
                        Map<String, Long> water_entries = (Map) dataSnapshot.getValue();
                        water = (double)water_entries.get(Long.toString(today));
                    }
                }
                else {
                    water = 0;
                    getDatabaseReference().child("Water").child(getUserId()).child(Long.toString(today)).setValue(water);
                }

                listLoadedListener.onWaterLoaded(water);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }

    public void addNewItem(long date, double value) {
        getDatabaseReference().child("Water").child(getUserId()).child(Long.toString(date)).setValue(value);
    }
}
