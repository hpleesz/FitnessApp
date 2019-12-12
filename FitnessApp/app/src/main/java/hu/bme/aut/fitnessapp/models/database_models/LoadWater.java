package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Map;

public class LoadWater extends DatabaseConnection {

    private static final String WATER = "Water";

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
        Query lastWaterQuery = getDatabaseReference().child(WATER).child(getUserId()).orderByKey().limitToLast(1);
        lastWaterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = "";
                for(DataSnapshot item: dataSnapshot.getChildren()) {
                    key = item.getKey();
                }

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                calendar.set(year, month, day, 0,0,0);
                long today = calendar.getTimeInMillis() / 1000;

                double water;

                if(!key.equals("") && Long.parseLong(key) == today) {
                    try {
                        Map<String, Double> waterEntries = (Map) dataSnapshot.getValue();
                        water = waterEntries.get(Long.toString(today));

                    }
                    catch(Exception e) {
                        Map<String, Long> waterEntries = (Map) dataSnapshot.getValue();
                        water = (double)waterEntries.get(Long.toString(today));
                    }
                }
                else {
                    water = 0;
                    getDatabaseReference().child(WATER).child(getUserId()).child(Long.toString(today)).setValue(water);
                }

                listLoadedListener.onWaterLoaded(water);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }
        });
    }

    public void addNewItem(long date, double value) {
        getDatabaseReference().child(WATER).child(getUserId()).child(Long.toString(date)).setValue(value);
    }
}
