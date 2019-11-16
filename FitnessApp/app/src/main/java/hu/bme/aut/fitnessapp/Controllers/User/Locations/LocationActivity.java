package hu.bme.aut.fitnessapp.Controllers.User.Locations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
/*
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

 */
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Models.User.Locations.LocationModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Controllers.User.NavigationActivity;
import hu.bme.aut.fitnessapp.Adapters.UserPublicLocationAdapter;
import hu.bme.aut.fitnessapp.Adapters.LocationAdapter;
import hu.bme.aut.fitnessapp.Entities.Location;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;

public class LocationActivity extends NavigationActivity implements NewLocationItemDialogFragment.NewLocationItemDialogListener, LocationAdapter.LocationItemDeletedListener, LocationAdapter.LocationItemSelectedListener, EditLocationItemDialogFragment.EditLocationItemDialogListener, UserPublicLocationAdapter.LocationItemDeletedListener, UserPublicLocationAdapter.LocationItemSelectedListener,
        LocationModel.LocationsLoaded, LocationModel.PublicLocationsLoaded
{

    private LocationModel locationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_location, null, false);
        mDrawerLayout.addView(contentView, 0);
        navigationView.getMenu().getItem(3).setChecked(true);


        PublicLocation publicLocation = getLocationIntent();
        locationModel = new LocationModel(this, publicLocation);
        locationModel.initFirebase();
        locationModel.loadLocations();
        locationModel.loadListPublic();

        setFloatingActionButton();

    }

    public PublicLocation getLocationIntent() {
        Intent i = getIntent();
        return (PublicLocation) i.getSerializableExtra("Location");
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

    public void initRecyclerView(ArrayList<Location> itemlist) {
        RecyclerView recyclerView = findViewById(R.id.LocationRecyclerView);
        LocationAdapter adapter = new LocationAdapter(this, this, itemlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void initRecyclerView2(ArrayList<PublicLocation> public_itemlist) {
        RecyclerView recyclerViewPublic = findViewById(R.id.PublicLocationRecyclerView);
        UserPublicLocationAdapter publicAdapter = new UserPublicLocationAdapter(this, this, public_itemlist);
        recyclerViewPublic.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPublic.setAdapter(publicAdapter);
    }

    @Override
    public void onLocationItemCreated(final Location newItem) {
        locationModel.createLocationItem(newItem);
    }

    @Override
    public void onLocationItemUpdated(final Location newItem) {
        locationModel.updateLocationItem(newItem);
    }

    @Override
    public void onItemDeleted(final Location item) {
        locationModel.deleteLocationItem(item);
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
        locationModel.deletePublicLocationItem(item);
    }

    @Override
    public void onItemSelected(PublicLocation item, int position) {
        Intent intent = new Intent(LocationActivity.this, ViewPublicLocationDetailsActivity.class);
        intent.putExtra("location", item);
        startActivity(intent);

        //Intent exercisesIntent = new Intent(LocationActivity.this, MapActivity.class);
        //startActivity(exercisesIntent);
    }

    @Override
    public void onLocationsLoaded(ArrayList<Location> itemlist) {
        initRecyclerView(itemlist);
    }

    @Override
    public void onPublicLocationsLoaded(ArrayList<PublicLocation> publicLocations) {
        initRecyclerView2(publicLocations);
    }

}
