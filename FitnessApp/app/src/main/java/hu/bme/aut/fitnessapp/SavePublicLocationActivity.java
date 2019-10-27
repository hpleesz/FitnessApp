package hu.bme.aut.fitnessapp;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import hu.bme.aut.fitnessapp.data.equipment.EquipmentAdapter;
import hu.bme.aut.fitnessapp.data.location.LocationAdapter;
import hu.bme.aut.fitnessapp.data.warmup.WarmUpItem;
import hu.bme.aut.fitnessapp.fragments.EditLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.PublicLocationSearchMatchDialogFragment;
import hu.bme.aut.fitnessapp.models.Equipment;
import hu.bme.aut.fitnessapp.models.Location;
import hu.bme.aut.fitnessapp.models.PublicLocation;

public class SavePublicLocationActivity extends AppCompatActivity implements EquipmentAdapter.OnCheckBoxClicked, PublicLocationSearchMatchDialogFragment.ChooseLocationItemDialogListener {

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private ArrayList<Equipment> equipmentList;

    private EquipmentAdapter adapter;

    private EditText monStart;
    private EditText tuesStart;
    private EditText wedStart;
    private EditText thursStart;
    private EditText friStart;
    private EditText satStart;
    private EditText sunStart;

    private EditText monEnd;
    private EditText tuesEnd;
    private EditText wedEnd;
    private EditText thursEnd;
    private EditText friEnd;
    private EditText satEnd;
    private EditText sunEnd;

    private EditText name;
    private EditText description;
    private EditText country;
    private EditText city;
    private EditText zip;
    private EditText address;

    private ArrayList<EditText> openHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load the layout
        setContentView(R.layout.activity_new_public_location);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        initializeEditTexts();
        setDatePickers();


        //assignLayoutElements();
        //setFloatingActionButton();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Location", getLocationItem());
                PublicLocationSearchMatchDialogFragment fragment = new PublicLocationSearchMatchDialogFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), PublicLocationSearchMatchDialogFragment.TAG);

            }
        });

        loadEquipment();
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
                initRecyclerView();
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



    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.EquipmentRecyclerView);
        adapter = new EquipmentAdapter(this, equipmentList);
        //loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onChecked(int pos) {
        adapter.onChecked(pos);
    }

    @Override
    public void onUnchecked(int pos) {
        adapter.onUnchecked(pos);
    }

    public void initializeEditTexts() {
        openHours = new ArrayList<>();
        monStart = findViewById(R.id.MonStart);
        monEnd = findViewById(R.id.MonEnd);
        tuesStart = findViewById(R.id.TuesStart);
        tuesEnd = findViewById(R.id.TuesEnd);
        wedStart = findViewById(R.id.WedStart);
        wedEnd = findViewById(R.id.WedEnd);
        thursStart = findViewById(R.id.ThursStart);
        thursEnd = findViewById(R.id.ThursEnd);
        friStart = findViewById(R.id.FriStart);
        friEnd = findViewById(R.id.FriEnd);
        satStart = findViewById(R.id.SatStart);
        satEnd = findViewById(R.id.SatEnd);
        sunStart = findViewById(R.id.SunStart);
        sunEnd = findViewById(R.id.SunEnd);

        openHours.add(monStart);
        openHours.add(monEnd);
        openHours.add(tuesStart);
        openHours.add(tuesEnd);
        openHours.add(wedStart);
        openHours.add(wedEnd);
        openHours.add(thursStart);
        openHours.add(thursEnd);
        openHours.add(friStart);
        openHours.add(friEnd);
        openHours.add(satStart);
        openHours.add(satEnd);
        openHours.add(sunStart);
        openHours.add(sunEnd);

        name = findViewById(R.id.LocationNameEditText);
        description = findViewById(R.id.LocationDescriptionEditText);
        country = findViewById(R.id.LocationCountryEditText);
        city = findViewById(R.id.LocationCityEditText);
        zip = findViewById(R.id.LocationZipEditText);
        address = findViewById(R.id.LocationAddressEditText);
    }

    public void setDatePickers() {
        for(final EditText ET : openHours) {
            ET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(SavePublicLocationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                            String hour = Integer.toString(hourOfDay);
                            String min = Integer.toString(minutes);

                            if(hourOfDay < 10) hour = "0" + hour;
                            if(minutes < 10) min = "0" + min;
                            ET.setText(hour + ":" + min);
                        }
                    }, 0, 0, true);
                    timePickerDialog.show();

                }
            });
        }
    }


    private PublicLocation getLocationItem() {
        ArrayList<String[]> openClose = new ArrayList<>();
        for(int i = 0; i < openHours.size(); i = i + 2){
            String[] hours = new String[2];
            hours[0] = openHours.get(i).getText().toString();
            hours[1] = openHours.get(i+1).getText().toString();
            openClose.add(hours);
        }
        PublicLocation location = new PublicLocation(Calendar.getInstance().getTimeInMillis(), name.getText().toString(), adapter.getCheckedEquipmentList(), openClose, description.getText().toString(),
                zip.getText().toString(), country.getText().toString(), city.getText().toString(), address.getText().toString(), "");

        return location;
    }

    @Override
    public void onLocationItemChosen(PublicLocation location) {
        Intent intent = new Intent(SavePublicLocationActivity.this, LocationActivity.class);
        intent.putExtra("Location", location);
        startActivity(intent);
    }
}
