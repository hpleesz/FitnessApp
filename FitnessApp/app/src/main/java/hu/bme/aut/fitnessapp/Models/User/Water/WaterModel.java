package hu.bme.aut.fitnessapp.Models.User.Water;

import android.content.Context;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Map;

import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadWater;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadWeight;

public class WaterModel implements LoadWeight.CurrentWeightLoadedListener, LoadWater.WaterLoadedListener {

    private float recommended;

    public double getDisplay() {
        return display;
    }

    public void setDisplay(double display) {
        this.display = display;
    }

    public void setWater2(double water2) {
        this.water2 = water2;
    }

    private double display;
    private double water2;

    public void setCurrent_weight(Double current_weight) {
        this.current_weight = current_weight;
    }

    private Double current_weight;

    TextView consumedWaterTV;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private long today;

    private Context activity;

    public interface WaterListener {
        void onConsumedLoaded(double water);
        void onRecommendedLoaded(double water);
    }

    private WaterModel.WaterListener waterListener;

    public WaterModel(Context activity) {
        waterListener = (WaterModel.WaterListener)activity;
        this.activity = activity;
    }

    public void initFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void calculateToday() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(calendar.YEAR);
        int month = calendar.get(calendar.MONTH);
        int day = calendar.get(calendar.DAY_OF_MONTH);

        calendar.set(year, month, day, 0,0,0);
        today = calendar.getTimeInMillis() / 1000;
    }
    public void calculateRecommendedText() {
        recommended = (float) (current_weight * 0.033 + 1);
        display = Math.round(recommended * 10d) / 10d;
    }


    public int calculatePercent() {
        return (int) ((water2 / display) * 100);
    }



    public void addWater(double newItem) {
        water2 = water2 + newItem;
        databaseReference.child("Water").child(userId).child(Long.toString(today)).setValue(round(water2,2));
        waterListener.onConsumedLoaded(round(water2,2));
        //((WaterActivity)activity).setConsumedWaterText(water2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void editWater(double newItem) {
        water2 = newItem;
        databaseReference.child("Water").child(userId).child(Long.toString(today)).setValue(round(water2,2));
        waterListener.onConsumedLoaded(round(water2,2));
        //((WaterActivity)activity).setConsumedWaterText(water2);

    }

    public void loadWeight() {
        calculateToday();
        LoadWeight loadWeight = new LoadWeight();
        loadWeight.setCurrentWeightLoadedListener(this);
        loadWeight.loadCurrentWeight();

        /*
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        Query lastQuery = databaseReference.child("Weight").child(userId).orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = "";
                for(DataSnapshot item: dataSnapshot.getChildren()) {
                    key = item.getKey();
                }
                //Map<String, Double> weight = (Map) dataSnapshot.getValue();
                //current_weight = weight.get(key);

                try {
                    Map<String, Double> weight = (Map) dataSnapshot.getValue();
                    current_weight = weight.get(key);

                }
                catch(Exception e) {
                    Map<String, Long> weight = (Map) dataSnapshot.getValue();
                    current_weight = (double)weight.get(key);
                }

                calculateRecommendedText();
                waterListener.onRecommendedLoaded(display);
                //((WaterActivity)activity).setRecommendedWaterText(display);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });



        Query lastWaterQuery = databaseReference.child("Water").child(userId).orderByKey().limitToLast(1);
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
                today = calendar.getTimeInMillis() / 1000;

                if(!key.equals("") && Long.parseLong(key) == today) {
                    try {
                        Map<String, Double> water_entries = (Map) dataSnapshot.getValue();
                        water2 = water_entries.get(Long.toString(today));

                    }
                    catch(Exception e) {
                        Map<String, Long> water_entries = (Map) dataSnapshot.getValue();
                        water2 = (double)water_entries.get(Long.toString(today));
                    }
                }
                else {
                    water2 = 0.0;
                    databaseReference.child("Water").child(userId).child(Long.toString(today)).setValue(water2);
                }
                waterListener.onConsumedLoaded(water2);
                //((WaterActivity)activity).setConsumedWaterText(water2);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/
    }

    @Override
    public void onCurrentWeightLoaded(double weight) {
        current_weight = weight;
        calculateRecommendedText();
        waterListener.onRecommendedLoaded(display);
        loadWater();
    }

    public void loadWater() {
        LoadWater loadWater = new LoadWater(this);
        loadWater.loadWaterToday();
    }

    @Override
    public void onWaterLoaded(double water) {
        water2 = water;
        waterListener.onConsumedLoaded(water2);

    }



}