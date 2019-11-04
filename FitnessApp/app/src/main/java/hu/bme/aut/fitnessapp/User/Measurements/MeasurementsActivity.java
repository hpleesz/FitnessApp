package hu.bme.aut.fitnessapp.User.Measurements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.User.NavigationActivity;
import hu.bme.aut.fitnessapp.Models.User;

public class MeasurementsActivity extends NavigationActivity {

    private Map<String, Double> list;
    private List<String> body_parts;

    private DatabaseReference databaseReference;
    private String userId;

    private User user;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        loadUser();


    }

    public void loadBodyPartsDatabase() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                body_parts = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String body_part = (String) dataSnapshot1.getValue();
                    body_parts.add(body_part);
                }
                loadData();
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
    }

    public void loadData() {
        ValueEventListener eventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new HashMap<>();

                for (String body_part : body_parts) {
                    DataSnapshot dataSnapshot1 = dataSnapshot.child(body_part);
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        try {
                            Map<String, Double> entries = (Map) dataSnapshot1.getValue();

                            //double weight_value = (double)dataSnapshot1.getValue();
                            String key = dataSnapshot2.getKey();
                            double weight_value = entries.get(key);
                            list.put(body_part, weight_value);
                        }
                        catch (Exception e) {
                            Map<String, Long> entries = (Map) dataSnapshot1.getValue();

                            String key = dataSnapshot2.getKey();
                            double weight_value = (double)entries.get(key);
                            list.put(body_part, weight_value);
                        }
                    }
                }

                setCurrentMeasurements();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Measurements").child(userId).addValueEventListener(eventListener2);
    }

    public void loadUser() {
        ValueEventListener eventListener3 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                View contentView;
                //boolean gender = true;
                if (user.gender == 0) {
                    contentView = inflater.inflate(R.layout.activity_measurements_male_2, null, false);

                } else {
                    contentView = inflater.inflate(R.layout.activity_measurements_female_2, null, false);

                }

                mDrawerLayout.addView(contentView, 0);
                navigationView.getMenu().getItem(1).setChecked(true);

                loadBodyPartsDatabase();

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setImageDrawable(getDrawable(R.drawable.graph_white));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MeasurementsActivity.this, MeasurementsGraphActivity.class);
                        startActivity(intent);
                    }
                });

                //loadDatabase();
                //setCurrentMeasurements();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Users").child(userId).addValueEventListener(eventListener3);

    }

    public void setCurrentMeasurements() {
        ArrayList<TextView> textViews = new ArrayList<>();
        TextView shoulderTextView = (TextView) findViewById(R.id.shoulderTextView);
        textViews.add(shoulderTextView);
        TextView chestTextView = (TextView) findViewById(R.id.chestTextView);
        textViews.add(chestTextView);
        TextView waistTextView = (TextView) findViewById(R.id.waistTextView);
        textViews.add(waistTextView);
        TextView hipsTextView = (TextView) findViewById(R.id.hipsTextView);
        textViews.add(hipsTextView);
        TextView rightUpperArmTextView = (TextView) findViewById(R.id.rightUpperArmTextView);
        textViews.add(rightUpperArmTextView);
        TextView leftUpperArmTextView = (TextView) findViewById(R.id.leftUpperArmTextView);
        textViews.add(leftUpperArmTextView);
        TextView rightForearmTextView = (TextView) findViewById(R.id.rightForearmTextView);
        textViews.add(rightForearmTextView);
        TextView leftForearmTextView = (TextView) findViewById(R.id.leftForearmTextView);
        textViews.add(leftForearmTextView);
        TextView rightThighTextView = (TextView) findViewById(R.id.rightThighTextView);
        textViews.add(rightThighTextView);
        TextView leftThighTextView = (TextView) findViewById(R.id.leftThighTextView);
        textViews.add(leftThighTextView);
        TextView rightCalfTextView = (TextView) findViewById(R.id.rightCalfTextView);
        textViews.add(rightCalfTextView);
        TextView leftCalfTextView = (TextView) findViewById(R.id.leftCalfTextView);
        textViews.add(leftCalfTextView);

        for(int i = 0; i < body_parts.size(); i++) {
           Double text = list.get(body_parts.get(i));
           if(text != null) {
               textViews.get(i).setText(text + " " + "cm");
           }
           else {
               textViews.get(i).setText("-- cm");
           }
        }

    }

}
