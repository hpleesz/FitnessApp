package hu.bme.aut.fitnessapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.data.measurement.GraphPagerAdapter;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementAdapter;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementDatabase;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementItem;
import hu.bme.aut.fitnessapp.fragments.MeasurementsGraphFragment;
import hu.bme.aut.fitnessapp.fragments.NewLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewMeasurementItemDialogFragment;

public class MeasurementsGraphActivity extends NavigationActivity implements NewMeasurementItemDialogFragment.NewMeasurementDialogListener, MeasurementAdapter.MeasurementItemDeletedListener{

    private MeasurementDatabase database;
    private ViewPager viewPager;
    public static final String MEASUREMENTS = "Measurements";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_measurements_graph, null, false);

        mDrawerLayout.addView(contentView, 0);
        navigationView.getMenu().getItem(1).setChecked(true);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new GraphPagerAdapter(getSupportFragmentManager()));

        database = Room.databaseBuilder(
                getApplicationContext(),
                MeasurementDatabase.class,
                "measurements"
        ).build();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewMeasurementItemDialogFragment().show(getSupportFragmentManager(), NewMeasurementItemDialogFragment.TAG);
            }
        });
    }

    @Override
    public void onMeasurementItemsCreated(final ArrayList<MeasurementItem> newItems) {
        new AsyncTask<Void, Void, ArrayList<MeasurementItem>>() {

            @Override
            protected ArrayList<MeasurementItem> doInBackground(Void... voids) {
                for (int i = 0; i < newItems.size(); i++) {
                    newItems.get(i).measurement_id = database.measurementItemDao().insert(newItems.get(i));
                }
                return newItems;
            }

            @Override
            protected void onPostExecute(ArrayList<MeasurementItem> measurementItems) {
                setCurrentValues();
            }

        }.execute();
    }

    @Override
    public void onItemDeleted(final MeasurementItem item) {
        new AsyncTask<Void, Void, MeasurementItem>() {

            @Override
            protected MeasurementItem doInBackground(Void... voids) {
                database.measurementItemDao().deleteItem(item);
                return item;
            }

            @Override
            protected void onPostExecute(MeasurementItem measurementItems) {
                setCurrentValues();
            }

        }.execute();
    }

    public void setCurrentValues(){

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                SharedPreferences sharedPreferences = getSharedPreferences(MEASUREMENTS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                for(int i = 0; i < NewMeasurementItemDialogFragment.body_parts.length; i++){
                    List<MeasurementItem> items = database.measurementItemDao().getMeasurementsWithBodyPart(NewMeasurementItemDialogFragment.body_parts[i]);
                    String value = "--";
                    if(items.size() != 0) {
                        int max = 0;
                        for(int j = 1; j < items.size(); j++){
                            if(items.get(j).measurement_calculated > items.get(max).measurement_calculated) max = j;
                        }
                        value = Double.toString(items.get(max).measurement_value);
                    }
                    editor.putString(NewMeasurementItemDialogFragment.body_parts[i], value);
                }
                editor.apply();
                return true;
            }

        }.execute();

    }
}
