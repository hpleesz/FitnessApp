package hu.bme.aut.fitnessapp.Controllers.User.Measurements;

import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.view.ViewPager;
//import android.support.v7.widget.Toolbar;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

import hu.bme.aut.fitnessapp.Controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.Models.User.Measurements.MeasurementsGraphModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Adapters.GraphPagerAdapter;
import hu.bme.aut.fitnessapp.Adapters.MeasurementAdapter;
import hu.bme.aut.fitnessapp.Entities.Measurement;

public class MeasurementsGraphActivity extends InternetCheckActivity implements NewMeasurementItemDialogFragment.NewMeasurementDialogListener, MeasurementAdapter.MeasurementItemDeletedListener {
    private MeasurementsGraphModel measurementsGraphModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements_graph);

        setToolbar();
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new GraphPagerAdapter(getSupportFragmentManager()));

        setFloatingActionButton();
        measurementsGraphModel = new MeasurementsGraphModel();
        measurementsGraphModel.initFirebase();

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
    public void setFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewMeasurementItemDialogFragment().show(getSupportFragmentManager(), NewMeasurementItemDialogFragment.TAG);
            }
        });
    }

    @Override
    public void onMeasurementItemsCreated(HashMap<String, Double> new_entries, String date) {
        measurementsGraphModel.createMeasurementItems(new_entries, date);
    }


    @Override
    public void onItemDeleted(Measurement item, String body_part) {
        measurementsGraphModel.deleteItem(item, body_part);
    }
}
