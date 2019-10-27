package hu.bme.aut.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import hu.bme.aut.fitnessapp.models.Equipment;
import hu.bme.aut.fitnessapp.models.PublicLocation;

public class ViewPublicLocationDetails extends AppCompatActivity {

    private PublicLocation publicLocation;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private ArrayList<Equipment> equipmentList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load the layout
        setContentView(R.layout.activity_view_public_location_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent i = getIntent();
        publicLocation = (PublicLocation) i.getSerializableExtra("location");


        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_map);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewPublicLocationDetails.this, MapActivity.class);
                intent.putExtra("location", publicLocation);
                startActivity(intent);
            }
        });

        loadEquipment();
        //loadLocation();
    }

    private void loadEquipment() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                equipmentList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    int id = Integer.parseInt(dataSnapshot1.getKey());
                    String name = (String) dataSnapshot1.getValue();
                    Equipment equipment = new Equipment(id, name);
                    equipmentList.add(equipment);
                }
                loadLocation();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Equipment").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }


    private void loadLocation() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("Name").getValue(String.class);
                getSupportActionBar().setTitle(name);

                TextView descriptionTV = findViewById(R.id.locationDescription);
                String description = dataSnapshot.child("Description").getValue(String.class);
                descriptionTV.setText(description);

                TextView addressTV = findViewById(R.id.locationAddress);

                String zip = dataSnapshot.child("Zip").getValue(String.class);
                String country = dataSnapshot.child("Country").getValue(String.class);
                String city = dataSnapshot.child("City").getValue(String.class);
                String address = dataSnapshot.child("Address").getValue(String.class);

                String full_address = country + "\n" + zip + ", " + city + "\n" + address;
                addressTV.setText(full_address);

                String creator = dataSnapshot.child("Creator").getValue(String.class);

                ArrayList<Integer> equipment = new ArrayList<>();

                String equipment_text = "";
                for (DataSnapshot dataSnapshot2 : dataSnapshot.child("Equipment").getChildren()) {
                    int idx = dataSnapshot2.getValue(Integer.class);
                    equipment.add(idx);
                    if(equipment_text.equals("")) equipment_text = equipmentList.get(idx-1).name;
                    else equipment_text = equipment_text + "\n" + equipmentList.get(idx-1).name;
                }
                TextView equipmentTV = findViewById(R.id.locationEquipment);
                equipmentTV.setText(equipment_text);

                ArrayList<String[]> hours = new ArrayList<>();

                for (DataSnapshot dataSnapshot2 : dataSnapshot.child("Open_Hours").getChildren()) {
                    String[] open_close = new String[2];

                    for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                        int idx = Integer.parseInt(dataSnapshot3.getKey());
                        String hour = dataSnapshot3.getValue(String.class);

                        open_close[idx] = hour;
                    }
                    hours.add(open_close);
                }

                ArrayList<TextView> textViews = new ArrayList<>();
                TextView mondayTV = findViewById(R.id.locationMonday);
                textViews.add(mondayTV);
                TextView tuesdayTV = findViewById(R.id.locationTuesday);
                textViews.add(tuesdayTV);
                TextView wednesdayTV = findViewById(R.id.locationWednesday);
                textViews.add(wednesdayTV);
                TextView thursdayTV = findViewById(R.id.locationThursday);
                textViews.add(thursdayTV);
                TextView fridayTV = findViewById(R.id.locationFriday);
                textViews.add(fridayTV);
                TextView saturdayTV = findViewById(R.id.locationSaturday);
                textViews.add(saturdayTV);
                TextView sundayTV = findViewById(R.id.locationSunday);
                textViews.add(sundayTV);

                for(int i = 0; i < hours.size(); i ++) {
                    if(hours.get(i)[0].equals("")) {
                        textViews.get(i).setText("Closed");
                    }
                    else {
                        String text = hours.get(i)[0] + " - " + hours.get(i)[1];
                        textViews.get(i).setText(text);
                    }
                }


                publicLocation = new PublicLocation(publicLocation.id, name, equipment, hours, description, zip, country, city, address, creator);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Public_Locations").child(Long.toString(publicLocation.id)).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }


}
