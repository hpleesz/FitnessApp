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

import hu.bme.aut.fitnessapp.data.location.PublicLocationAdapter;
import hu.bme.aut.fitnessapp.data.warmup.WarmUpItem;
import hu.bme.aut.fitnessapp.fragments.EditLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.data.location.LocationAdapter;
import hu.bme.aut.fitnessapp.models.Location;
import hu.bme.aut.fitnessapp.models.PublicLocation;

public class LocationActivity extends NavigationActivity implements NewLocationItemDialogFragment.NewLocationItemDialogListener, LocationAdapter.LocationItemDeletedListener, LocationAdapter.LocationItemSelectedListener, EditLocationItemDialogFragment.EditLocationItemDialogListener, PublicLocationAdapter.LocationItemDeletedListener, PublicLocationAdapter.LocationItemSelectedListener {

    private LocationAdapter adapter;
    private PublicLocationAdapter publicAdapter;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    private ArrayList<Location> itemlist;
    private ArrayList<PublicLocation> public_itemlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_location, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(3).setChecked(true);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Locations").child(userId);

        loadList();
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

        public_itemlist = new ArrayList<>();
        RecyclerView recyclerViewPublic = findViewById(R.id.PublicLocationRecyclerView);
        publicAdapter = new PublicLocationAdapter(this, this, public_itemlist);
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
        databaseReference.addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

    @Override
    public void onLocationItemCreated(final Location newItem) {
        int id = 0;
        if(!itemlist.isEmpty()) id = itemlist.get(itemlist.size()-1).id +1;
        databaseReference.child(Integer.toString(id)).child("Name").setValue(newItem.name);
        for(int i = 0; i < newItem.equipment.size(); i++) {
            databaseReference.child(Integer.toString(id)).child("Equipment").child(Integer.toString(i)).setValue(newItem.equipment.get(i));
        }

    }

    @Override
    public void onLocationItemUpdated(final Location newItem) {
        databaseReference.child(Integer.toString(newItem.id)).child("Name").setValue(newItem.name);
        databaseReference.child(Integer.toString(newItem.id)).child("Equipment").removeValue();
        for(int i = 0; i < newItem.equipment.size(); i++) {
            databaseReference.child(Integer.toString(newItem.id)).child("Equipment").child(Integer.toString(i)).setValue(newItem.equipment.get(i));
        }

    }

    @Override
    public void onItemDeleted(final Location item) {
        databaseReference.child(Integer.toString(item.id)).removeValue();
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

    }

    @Override
    public void onItemSelected(PublicLocation item, int position) {

    }
}
