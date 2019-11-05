package hu.bme.aut.fitnessapp.User.Locations;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.InternetCheckActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Models.Equipment;
import hu.bme.aut.fitnessapp.Models.PublicLocation;

public class ViewPublicLocationDetailsActivity extends InternetCheckActivity {

    private PublicLocation publicLocation;
    private ArrayList<Equipment> equipmentList;
    private DatabaseReference databaseReference;
    private ArrayList<TextView> textViews;
    private TextView equipmentTV;
    private TextView descriptionTV;
    private TextView addressTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_public_location_details);

        setToolbar();

        Intent i = getIntent();
        publicLocation = (PublicLocation) i.getSerializableExtra("location");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        setFloatingActionButtonListener();
        initializeTextViews();
        loadEquipment();
    }

    public void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setFloatingActionButtonListener() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_map);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewPublicLocationDetailsActivity.this, MapActivity.class);
                intent.putExtra("location", publicLocation);
                startActivity(intent);
            }
        });
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

                String description = dataSnapshot.child("Description").getValue(String.class);
                descriptionTV.setText(description);

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
                    }

                ArrayList<Integer> display_equipments = equipment;
                if(display_equipments.contains(4) && display_equipments.contains(5)) display_equipments.remove(Integer.valueOf(4));
                if(display_equipments.contains(6) && display_equipments.contains(7)) display_equipments.remove(Integer.valueOf(6));
                if(display_equipments.size() == 1) {
                    equipment_text = equipmentList.get(display_equipments.get(0)-1).name;
                }
                else {
                    for (int equ : display_equipments) {
                        if(equ != 1) {
                            if (equipment_text.equals(""))
                                equipment_text = equipmentList.get(equ - 1).name;
                            else
                                equipment_text = equipment_text + "\n" + equipmentList.get(equ - 1).name;
                        }
                    }
                }
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

    public void initializeTextViews() {
        descriptionTV = findViewById(R.id.locationDescription);
        addressTV = findViewById(R.id.locationAddress);
        equipmentTV = findViewById(R.id.locationEquipment);

        textViews = new ArrayList<>();
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
    }

}
