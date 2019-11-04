package hu.bme.aut.fitnessapp.User.Water;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Map;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.User.NavigationActivity;

public class WaterActivity extends NavigationActivity implements NewWaterDialogFragment.NewWaterDialogListener, EditWaterDialogFragment.EditWaterDialogListener {

    private float recommended;
    private double display;
    private double water2;
    private Double current_weight;

    TextView consumedWaterTV;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private long today;

    private SharedPreferences water_consumption;

    public static final String WATER = "water consumption";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_water, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(4).setChecked(true);

        setFloatingActionButton();
        setConsumedWaterClick();
        getData();
        //setRecommendedWaterText();
        //setConsumedWaterText();
    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewWaterDialogFragment().show(getSupportFragmentManager(), NewWaterDialogFragment.TAG);
            }
        });
    }

    public void setConsumedWaterClick() {
        consumedWaterTV = (TextView) findViewById(R.id.consumedWaterTextView);
        consumedWaterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditWaterDialogFragment().show(getSupportFragmentManager(), EditWaterDialogFragment.TAG);
            }
        });
    }

    public void setRecommendedWaterText() {

        //SharedPreferences user = getSharedPreferences(UserActivity.USER, MODE_PRIVATE);
        //float current_weight = user.getFloat("Current weight", 0);

        recommended = (float) (current_weight * 0.033 + 1);
        display = Math.round(recommended * 10d) / 10d;
        String text = display + " " + getString(R.string.litre);
        TextView recommendedWaterTV = (TextView) findViewById(R.id.recommendedWaterTextView);
        recommendedWaterTV.setText(text);

        //SharedPreferences.Editor water_editor = water_consumption.edit();
        //water_editor.putFloat("Recommended", recommended);
        //water_editor.apply();
    }

    public void setConsumedWaterText() {
        /*
        SharedPreferences.Editor editor = water_consumption.edit();
        water = water_consumption.getFloat("Consumed", 0);
        editor.apply();
         */

        String text = Double.toString(water2) + " " + getString(R.string.litre);
        consumedWaterTV.setText(text);
        setBottleImage();

    }

    public void setBottleImage() {
        ImageView bottle = (ImageView) findViewById(R.id.consumedWaterImage);
        int percent = (int) ((water2 / display) * 100);
        setPercentText(percent);
        drawRecommendedCompleted(percent);
        for (int i = 100; i >= 0; i = i - 10) {
            if (percent >= i) {
                String name = "bottle" + i;
                int id = getResources().getIdentifier(name, "drawable", getPackageName());
                bottle.setImageResource(id);
                break;
            }
        }
    }

    public void setPercentText(int percent) {
        TextView percentTextView = (TextView) findViewById(R.id.percentTextView);
        String percentString = percent + "%" + " complete";
        percentTextView.setText(percentString);
    }

    public void drawRecommendedCompleted(int percent) {
        ImageView fireworkImageView = (ImageView) findViewById(R.id.fireWorkImageView);
        ImageView celebrationImageView = (ImageView) findViewById(R.id.celebrationImageView);
        if (percent >= 100) {
            fireworkImageView.setVisibility(View.VISIBLE);
            celebrationImageView.setVisibility(View.VISIBLE);
        } else {
            fireworkImageView.setVisibility(View.INVISIBLE);
            celebrationImageView.setVisibility(View.INVISIBLE);
        }
    }

    public void onWaterAdded(double newItem) {
        /*SharedPreferences.Editor editor = water_consumption.edit();
        water = water + newItem;
        editor.putFloat("Consumed", water);
        editor.apply();
         */
        water2 = water2 + newItem;
        databaseReference.child("Water").child(userId).child(Long.toString(today)).setValue(water2);
        setConsumedWaterText();
    }

    public void onWaterEdited(double newItem) {
        /*SharedPreferences.Editor editor = water_consumption.edit();
        water = newItem;
        editor.putFloat("Consumed", water);
        editor.apply();
         */
        water2 = newItem;
        databaseReference.child("Water").child(userId).child(Long.toString(today)).setValue(water2);
        setConsumedWaterText();
    }

    public void getData() {

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

                setRecommendedWaterText();
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
                setConsumedWaterText();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}