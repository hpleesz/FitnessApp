package hu.bme.aut.fitnessapp.Models.User.Water;

//import android.support.v4.app.DialogFragment;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadWater;

public class EditWaterModel implements LoadWater.WaterLoadedListener{

    public void setWater_saved(double water_saved) {
        this.water_saved = water_saved;
    }

    private double water_saved;

    public interface WaterLoadListener {
        void onWaterLoaded(String water);
    }

    private EditWaterModel.WaterLoadListener waterLoadListener;

    public EditWaterModel(DialogFragment activity) {
        waterLoadListener = (EditWaterModel.WaterLoadListener)activity;
        //loadWaterEntry();
    }

    public void loadWaterEntry() {
        LoadWater loadWater = new LoadWater(this);
        loadWater.loadWaterToday();
        /*
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Water").child(userId);

        Query lastWaterQuery = databaseReference.orderByKey().limitToLast(1);
        lastWaterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = "";
                for(DataSnapshot item: dataSnapshot.getChildren()) {
                    key = item.getKey();
                }

                //////////////////////////////

                try {
                    Map<String, Double> water_entries = (Map) dataSnapshot.getValue();
                    water_saved = water_entries.get(key);

                }
                catch(Exception e) {
                    Map<String, Long> water_entries = (Map) dataSnapshot.getValue();
                    water_saved = (double)water_entries.get(key);
                }

                waterLoadListener.onWaterLoaded(Double.toString(water_saved));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
         */
    }

    @Override
    public void onWaterLoaded(double water) {
        water_saved = water;
        waterLoadListener.onWaterLoaded(Double.toString(water_saved));
    }

    public double getWater(String text) {
        double water = 0;
        try {
            water = Double.parseDouble(text);
        } catch (NumberFormatException f) {
            water = water_saved;
        }
        return water;
    }

}
