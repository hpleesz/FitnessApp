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
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.fitnessapp.data.measurement.GraphPagerAdapter;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementAdapter;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementDatabase;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementItem;
import hu.bme.aut.fitnessapp.data.weight.WeightAdapter;
import hu.bme.aut.fitnessapp.fragments.MeasurementsGraphFragment;
import hu.bme.aut.fitnessapp.fragments.NewLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewMeasurementItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.PeriodSelectDialogFragment;
import hu.bme.aut.fitnessapp.models.Weight;

public class MeasurementsGraphActivity extends NavigationActivity implements NewMeasurementItemDialogFragment.NewMeasurementDialogListener, MeasurementAdapter.MeasurementItemDeletedListener {

    private MeasurementDatabase database;
    private ViewPager viewPager;
    public static final String MEASUREMENTS = "Measurements";

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_measurements_graph, null, false);

        mDrawerLayout.addView(contentView, 0);
        navigationView.getMenu().getItem(1).setChecked(true);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new GraphPagerAdapter(getSupportFragmentManager()));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewMeasurementItemDialogFragment().show(getSupportFragmentManager(), NewMeasurementItemDialogFragment.TAG);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Measurements").child(userId);
    }

    @Override
    public void onMeasurementItemsCreated(HashMap<String, Double> new_entries, String date) {
        for(Map.Entry<String, Double> entry : new_entries.entrySet()) {
            databaseReference.child(entry.getKey()).child(date).setValue(entry.getValue());
        }
    }


    @Override
    public void onItemDeleted(Weight item, String body_part) {
        databaseReference.child(body_part).child(item.date).removeValue();
    }
}
