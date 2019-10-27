package hu.bme.aut.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.fitnessapp.data.location.UserPublicLocationAdapter;
import hu.bme.aut.fitnessapp.data.warmup.WarmUpItem;
import hu.bme.aut.fitnessapp.fragments.EditLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.data.location.LocationAdapter;
import hu.bme.aut.fitnessapp.models.Location;
import hu.bme.aut.fitnessapp.models.PublicLocation;
import hu.bme.aut.fitnessapp.models.UserPublicLocation;

public class LocationActivity extends NavigationActivity implements NewLocationItemDialogFragment.NewLocationItemDialogListener, LocationAdapter.LocationItemDeletedListener, LocationAdapter.LocationItemSelectedListener, EditLocationItemDialogFragment.EditLocationItemDialogListener, UserPublicLocationAdapter.LocationItemDeletedListener, UserPublicLocationAdapter.LocationItemSelectedListener {

    private LocationAdapter adapter;
    private UserPublicLocationAdapter publicAdapter;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    private ArrayList<Location> itemlist;
    private ArrayList<PublicLocation> public_itemlist;

    private PublicLocation newPublicLocation;

    private ArrayList<UserPublicLocation> publicIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_location, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(3).setChecked(true);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Intent i = getIntent();
        newPublicLocation = (PublicLocation) i.getSerializableExtra("Location");

        loadList();
        loadList2();
        setFloatingActionButton();

    }

    public void setFloatingActionButton() {
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewLocationItemDialogFragment().show(getSupportFragmentManager(), NewLocationItemDialogFragment.TAG);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LocationActivity.this, SavePublicLocationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.LocationRecyclerView);
        adapter = new LocationAdapter(this, this, itemlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void initRecyclerView2() {
        RecyclerView recyclerViewPublic = findViewById(R.id.PublicLocationRecyclerView);
        publicAdapter = new UserPublicLocationAdapter(this, this, public_itemlist);
        recyclerViewPublic.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPublic.setAdapter(publicAdapter);
    }



    private void loadList() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemlist = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                        int id = Integer.parseInt(dataSnapshot1.getKey());
                        String name = dataSnapshot1.child("Name").getValue(String.class);
                        ArrayList<Integer> equipment = new ArrayList<>();

                        for(DataSnapshot dataSnapshot2: dataSnapshot1.child("Equipment").getChildren()) {
                            int idx = dataSnapshot2.getValue(Integer.class);
                            equipment.add(idx);
                        }

                        Location location = new Location(id, name, equipment);

                        itemlist.add(location);


                }
                initRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Locations").child(userId).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

    private void loadList2() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                publicIDs = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    UserPublicLocation loc = new UserPublicLocation();
                    loc.gym_id = dataSnapshot1.getValue(String.class);
                    loc.id = Integer.parseInt(dataSnapshot1.getKey());
                    publicIDs.add(loc);
                }

                loadGyms();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("User_Public_Locations").child(userId).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

    private  void loadGyms() {
        public_itemlist = new ArrayList<>();
        if(publicIDs.isEmpty()) {
            if(newPublicLocation != null) {
                addNewItem();
            }
            initRecyclerView2();
        }
        for(UserPublicLocation loc: publicIDs) {
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        long id = Long.parseLong(dataSnapshot.getKey());
                        String name = dataSnapshot.child("Name").getValue(String.class);
                        String description = dataSnapshot.child("Description").getValue(String.class);
                        String zip = dataSnapshot.child("Zip").getValue(String.class);
                        String country = dataSnapshot.child("Country").getValue(String.class);
                        String city = dataSnapshot.child("City").getValue(String.class);
                        String address = dataSnapshot.child("Address").getValue(String.class);

                        ArrayList<Integer> equipment = new ArrayList<>();

                        for(DataSnapshot dataSnapshot2: dataSnapshot.child("Equipment").getChildren()) {
                            int idx = dataSnapshot2.getValue(Integer.class);
                            equipment.add(idx);
                        }

                        ArrayList<String[]> hours = new ArrayList<>();

                        for(DataSnapshot dataSnapshot2: dataSnapshot.child("Open_Hours").getChildren()) {
                            String[] open_close = new String[2];

                            for (DataSnapshot dataSnapshot3: dataSnapshot2.getChildren()) {
                                int idx = Integer.parseInt(dataSnapshot3.getKey());
                                String hour = dataSnapshot3.getValue(String.class);

                                open_close[idx] = hour;
                            }
                            hours.add(open_close);
                        }

                        PublicLocation location = new PublicLocation(id, name, equipment, hours, description, zip, country, city, address, userId);

                        int idx = -1;
                        for(int i = 0; i < public_itemlist.size(); i++) {
                            if(public_itemlist.get(i).id == location.id) {
                                idx = i;
                                break;
                            }
                        }
                        //update
                        if(idx > -1) {
                            public_itemlist.set(idx, location);
                        }
                        //add
                        else {
                            public_itemlist.add(location);
                        }


                    initRecyclerView2();
                    if(newPublicLocation != null) {
                        addNewItem();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors.
                }

            };
            databaseReference.child("Public_Locations").child(loc.gym_id).addValueEventListener(eventListener);

        }

    }

    @Override
    public void onLocationItemCreated(final Location newItem) {
        int id = 0;
        if(!itemlist.isEmpty()) id = itemlist.get(itemlist.size()-1).id +1;
        databaseReference.child("Locations").child(userId).child(Integer.toString(id)).child("Name").setValue(newItem.name);
        for(int i = 0; i < newItem.equipment.size(); i++) {
            databaseReference.child("Locations").child(userId).child(Integer.toString(id)).child("Equipment").child(Integer.toString(i)).setValue(newItem.equipment.get(i));
        }

    }

    @Override
    public void onLocationItemUpdated(final Location newItem) {
        databaseReference.child("Locations").child(userId).child(Integer.toString(newItem.id)).child("Name").setValue(newItem.name);
        databaseReference.child("Locations").child(userId).child(Integer.toString(newItem.id)).child("Equipment").removeValue();
        for(int i = 0; i < newItem.equipment.size(); i++) {
            databaseReference.child("Locations").child(userId).child(Integer.toString(newItem.id)).child("Equipment").child(Integer.toString(i)).setValue(newItem.equipment.get(i));
        }

    }

    @Override
    public void onItemDeleted(final Location item) {
        databaseReference.child("Locations").child(userId).child(Integer.toString(item.id)).removeValue();
    }

    @Override
    public void onItemSelected(final Location item, int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("Position", position);
        bundle.putSerializable("Item", item);
        EditLocationItemDialogFragment fragment = new EditLocationItemDialogFragment();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), EditLocationItemDialogFragment.TAG);
    }


    @Override
    public void onItemDeleted(PublicLocation item) {
        String idx = "";
        for(UserPublicLocation loc : publicIDs) {
            if(Long.parseLong(loc.gym_id) == item.id) {
                idx = Long.toString(loc.id);
            }
        }
        databaseReference.child("User_Public_Locations").child(userId).child(idx).removeValue();
    }

    @Override
    public void onItemSelected(PublicLocation item, int position) {
        Intent intent = new Intent(LocationActivity.this, ViewPublicLocationDetails.class);
        intent.putExtra("location", item);
        startActivity(intent);

        //Intent exercisesIntent = new Intent(LocationActivity.this, MapActivity.class);
        //startActivity(exercisesIntent);
    }

    public void addNewItem() {
        PublicLocation publicLocation = newPublicLocation;
        newPublicLocation = null;

        for(UserPublicLocation loc : publicIDs) {
            if(Long.toString(publicLocation.id).equals(loc.gym_id)) return;
        }

        int id = 0;
        if(!publicIDs.isEmpty()) id = publicIDs.get(publicIDs.size()-1).id + 1;
        databaseReference.child("User_Public_Locations").child(userId).child(Integer.toString(id)).setValue(Long.toString(publicLocation.id));
    }
}
