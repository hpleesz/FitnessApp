package hu.bme.aut.fitnessapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

import hu.bme.aut.fitnessapp.data.location.PublicLocationAdapter;
import hu.bme.aut.fitnessapp.data.warmup.WarmUpItem;
import hu.bme.aut.fitnessapp.fragments.EditLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.data.location.LocationAdapter;
import hu.bme.aut.fitnessapp.data.location.LocationItem;
import hu.bme.aut.fitnessapp.data.location.LocationListDatabase;
import hu.bme.aut.fitnessapp.fragments.NewPublicLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewWaterDialogFragment;
import hu.bme.aut.fitnessapp.models.Equipment;
import hu.bme.aut.fitnessapp.models.Location;
import hu.bme.aut.fitnessapp.models.PublicLocation;
import hu.bme.aut.fitnessapp.models.Weight;

public class GymMainActivity extends AppCompatActivity implements NewPublicLocationItemDialogFragment.NewPublicLocationItemDialogListener, PublicLocationAdapter.LocationItemDeletedListener, PublicLocationAdapter.LocationItemSelectedListener, EditLocationItemDialogFragment.EditLocationItemDialogListener {

    private PublicLocationAdapter adapter;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    private ArrayList<PublicLocation> itemlist;
    private PublicLocation publicLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        publicLocation = (PublicLocation) i.getSerializableExtra("new");

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Public_Locations");

        if(publicLocation != null) {
            addNewItem();
        }

        loadList();
        setFloatingActionButton();

    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new NewPublicLocationItemDialogFragment().show(getSupportFragmentManager(), NewPublicLocationItemDialogFragment.TAG);
                startActivity(new Intent(GymMainActivity.this, NewPublicLocationActivity.class));

            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.PublicLocationRecyclerView);
        adapter = new PublicLocationAdapter(this, this, itemlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadList() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemlist = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    long id = Long.parseLong(dataSnapshot1.getKey());
                    String name = dataSnapshot1.child("Name").getValue(String.class);
                    String description = dataSnapshot1.child("Description").getValue(String.class);
                    String zip = dataSnapshot1.child("Zip").getValue(String.class);
                    String country = dataSnapshot1.child("Country").getValue(String.class);
                    String city = dataSnapshot1.child("City").getValue(String.class);
                    String address = dataSnapshot1.child("Address").getValue(String.class);

                    ArrayList<Integer> equipment = new ArrayList<>();

                    for(DataSnapshot dataSnapshot2: dataSnapshot1.child("Equipment").getChildren()) {
                        int idx = dataSnapshot2.getValue(Integer.class);
                        equipment.add(idx);
                    }

                    ArrayList<String[]> hours = new ArrayList<>();

                    for(DataSnapshot dataSnapshot2: dataSnapshot1.child("Open_Hours").getChildren()) {
                        String[] open_close = new String[2];

                        for (DataSnapshot dataSnapshot3: dataSnapshot2.getChildren()) {
                            int idx = Integer.parseInt(dataSnapshot3.getKey());
                            String hour = dataSnapshot3.getValue(String.class);

                            open_close[idx] = hour;
                        }
                        hours.add(open_close);
                    }

                    PublicLocation location = new PublicLocation(id, name, equipment, hours, description, zip, country, city, address, userId);

                    itemlist.add(location);

                }

                initRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.orderByChild("Creator").equalTo(userId).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

    //TODO
    @Override
    public void onPublicLocationItemCreated(final PublicLocation newItem) {

        databaseReference.child(Long.toString(publicLocation.id)).child("Name").setValue(newItem.name);
        databaseReference.child(Long.toString(publicLocation.id)).child("Description").setValue(newItem.description);
        databaseReference.child(Long.toString(publicLocation.id)).child("Zip").setValue(newItem.zip);
        databaseReference.child(Long.toString(publicLocation.id)).child("Country").setValue(newItem.country);
        databaseReference.child(Long.toString(publicLocation.id)).child("City").setValue(newItem.city);
        databaseReference.child(Long.toString(publicLocation.id)).child("Address").setValue(newItem.address);
        databaseReference.child(Long.toString(publicLocation.id)).child("Creator").setValue(newItem.creator);

        for(int i = 0; i < newItem.equipment.size(); i++) {
            databaseReference.child(Long.toString(publicLocation.id)).child("Equipment").child(Integer.toString(i)).setValue(newItem.equipment.get(i));
        }

        for(int i = 0; i < newItem.open_hours.size(); i++) {
            databaseReference.child(Long.toString(publicLocation.id)).child("Equipment").child(Integer.toString(i)).child("0").setValue(newItem.open_hours.get(i)[0]);
            databaseReference.child(Long.toString(publicLocation.id)).child("Equipment").child(Integer.toString(i)).child("1").setValue(newItem.open_hours.get(i)[1]);
        }


    }

    //TODO
    @Override
    public void onLocationItemUpdated(final Location newItem) {
        databaseReference.child(Long.toString(newItem.id)).child("Name").setValue(newItem.name);
        databaseReference.child(Long.toString(newItem.id)).child("Equipment").removeValue();
        for(int i = 0; i < newItem.equipment.size(); i++) {
            databaseReference.child(Long.toString(newItem.id)).child("Equipment").child(Integer.toString(i)).setValue(newItem.equipment.get(i));
        }

    }

    //TODO
    @Override
    public void onItemDeleted(final PublicLocation item) {
        databaseReference.child(Long.toString(item.id)).removeValue();
    }

    //TODO
    @Override
    public void onItemSelected(final PublicLocation item, int position) {
    }

    public void addNewItem() {
            databaseReference.child(Long.toString(publicLocation.id)).child("Name").setValue(publicLocation.name);
            databaseReference.child(Long.toString(publicLocation.id)).child("Description").setValue(publicLocation.description);
            databaseReference.child(Long.toString(publicLocation.id)).child("Country").setValue(publicLocation.name);
            databaseReference.child(Long.toString(publicLocation.id)).child("City").setValue(publicLocation.name);
            databaseReference.child(Long.toString(publicLocation.id)).child("Address").setValue(publicLocation.name);
            databaseReference.child(Long.toString(publicLocation.id)).child("Zip").setValue(publicLocation.name);
            databaseReference.child(Long.toString(publicLocation.id)).child("Creator").setValue(userId);

            for(int i = 0; i < publicLocation.equipment.size(); i++) {
                databaseReference.child(Long.toString(publicLocation.id)).child("Equipment").child(Integer.toString(i)).setValue(publicLocation.equipment.get(i));
            }

            for(int i = 0; i < publicLocation.open_hours.size(); i++) {
                databaseReference.child(Long.toString(publicLocation.id)).child("Open_Hours").child(Integer.toString(i)).child("0").setValue(publicLocation.open_hours.get(i)[0]);
                databaseReference.child(Long.toString(publicLocation.id)).child("Open_Hours").child(Integer.toString(i)).child("1").setValue(publicLocation.open_hours.get(i)[1]);

            }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gym, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent= new Intent(GymMainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_logout);
        item.setVisible(true);
        return true;
    }
}


