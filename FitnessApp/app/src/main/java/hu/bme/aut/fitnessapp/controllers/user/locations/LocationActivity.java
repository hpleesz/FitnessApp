package hu.bme.aut.fitnessapp.controllers.user.locations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.controllers.adapters.PublicLocationAdapter;
import hu.bme.aut.fitnessapp.models.user_models.location_models.LocationModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.user.NavigationActivity;
import hu.bme.aut.fitnessapp.controllers.adapters.LocationAdapter;
import hu.bme.aut.fitnessapp.entities.Location;
import hu.bme.aut.fitnessapp.entities.PublicLocation;

public class LocationActivity extends NavigationActivity implements NewLocationItemDialogFragment.NewLocationItemDialogListener, LocationAdapter.LocationItemDeletedListener, LocationAdapter.LocationItemSelectedListener, EditLocationItemDialogFragment.EditLocationItemDialogListener, PublicLocationAdapter.LocationItemDeletedListener, PublicLocationAdapter.LocationItemSelectedListener,
        LocationModel.LocationsLoaded, LocationModel.PublicLocationsLoaded
{

    private LocationModel locationModel;
    private PublicLocation publicLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_location, null, false);
        mDrawerLayout.addView(contentView, 0);
        navigationView.getMenu().getItem(3).setChecked(true);

        publicLocation = getLocationIntent();
    }


    @Override
    public void onStart() {
        super.onStart();
        locationModel = new LocationModel(this, publicLocation);
        locationModel.loadLocations();
        locationModel.loadListPublic();

        setFloatingActionButton();
    }

    public PublicLocation getLocationIntent() {
        Intent i = getIntent();
        return (PublicLocation) i.getSerializableExtra("Location");
    }

    public void setFloatingActionButton() {
        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewLocationItemDialogFragment().show(getSupportFragmentManager(), NewLocationItemDialogFragment.TAG);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LocationActivity.this, SavePublicLocationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initRecyclerView(List<Location> itemList) {
        RecyclerView recyclerView = findViewById(R.id.LocationRecyclerView);
        LocationAdapter adapter = new LocationAdapter(this, this, itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void initRecyclerView2(List<PublicLocation> publicItemList) {
        RecyclerView recyclerViewPublic = findViewById(R.id.PublicLocationRecyclerView);
        PublicLocationAdapter publicAdapter = new PublicLocationAdapter(this, this, publicItemList);
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
    }

    @Override
    public void onLocationsLoaded(ArrayList<Location> itemlist) {
        initRecyclerView(itemlist);
    }

    @Override
    public void onPublicLocationsLoaded(ArrayList<PublicLocation> publicLocations) {
        initRecyclerView2(publicLocations);
    }

    @Override
    public void onStop() {
        super.onStop();
        locationModel.removeListeners();
    }

}
