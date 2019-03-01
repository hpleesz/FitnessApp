package hu.bme.aut.fitnessapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import hu.bme.aut.fitnessapp.fragments.EditLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.data.location.LocationAdapter;
import hu.bme.aut.fitnessapp.data.location.LocationItem;
import hu.bme.aut.fitnessapp.data.location.LocationListDatabase;

public class LocationActivity extends NavigationActivity implements NewLocationItemDialogFragment.NewLocationItemDialogListener, LocationAdapter.LocationItemDeletedListener, LocationAdapter.LocationItemSelectedListener, EditLocationItemDialogFragment.EditLocationItemDialogListener{

    private RecyclerView recyclerView;
    private LocationAdapter adapter;
    private LocationListDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_location, null, false);
        mDrawerLayout.addView(contentView, 0);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new NewLocationItemDialogFragment().show(getSupportFragmentManager(), NewLocationItemDialogFragment.TAG);
            }
        });

        navigationView.getMenu().getItem(3).setChecked(true);

        database = Room.databaseBuilder(
                getApplicationContext(),
                LocationListDatabase.class,
                "locations"
        ).build();

        initRecyclerView();

    }


        private void initRecyclerView() {
            recyclerView = findViewById(R.id.LocationRecyclerView);
            adapter = new LocationAdapter(this, this);
            loadItemsInBackground();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }

        private void loadItemsInBackground() {
            new AsyncTask<Void, Void, List<LocationItem>>() {

                @Override
                protected List<LocationItem> doInBackground(Void... voids) {
                    return database.locationItemDao().getAll();
                }

                @Override
                protected void onPostExecute(List<LocationItem> locationItems) {
                    adapter.update(locationItems);
                }
            }.execute();
        }

        @Override
        public void onLocationItemCreated(final LocationItem newItem) {
            new AsyncTask<Void, Void, LocationItem>() {

                @Override
                protected LocationItem doInBackground(Void... voids) {
                    newItem.location_id = database.locationItemDao().insert(newItem);
                    return newItem;
                }

                @Override
                protected void onPostExecute(LocationItem locationItem) {
                    adapter.addItem(locationItem);
                }
            }.execute();
        }

    @Override
    public void onLocationItemUpdated(final LocationItem newItem) {
        new AsyncTask<Void, Void, LocationItem>() {

            @Override
            protected LocationItem doInBackground(Void... voids) {
                database.locationItemDao().update(newItem);
                return newItem;
            }

            @Override
            protected void onPostExecute(LocationItem locationItem) {
                adapter.update(locationItem);
            }
        }.execute();
    }

        @Override
        public void onItemDeleted(final LocationItem item) {
            new AsyncTask<Void, Void, LocationItem>() {

                @Override
                protected LocationItem doInBackground(Void... voids) {
                    database.locationItemDao().deleteItem(item);
                    return item;
                }

                @Override
                protected void onPostExecute(LocationItem locationItem) {
                    adapter.deleteItem(locationItem);
                }
            }.execute();
        }

        @Override
        public void onItemSelected(final LocationItem item, int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("Position", position);
            bundle.putSerializable("Item", item);
            EditLocationItemDialogFragment fragment = new EditLocationItemDialogFragment();
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(), EditLocationItemDialogFragment.TAG);

        }


    }

