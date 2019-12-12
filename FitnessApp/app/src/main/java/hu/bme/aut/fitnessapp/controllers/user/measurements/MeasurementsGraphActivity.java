package hu.bme.aut.fitnessapp.controllers.user.measurements;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;

import hu.bme.aut.fitnessapp.controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.models.user_models.measurement_models.MeasurementsGraphModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.adapters.GraphPagerAdapter;
import hu.bme.aut.fitnessapp.controllers.adapters.MeasurementAdapter;
import hu.bme.aut.fitnessapp.entities.Measurement;

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
    }

    @Override
    public void onStart() {
        super.onStart();
        measurementsGraphModel = new MeasurementsGraphModel();
    }

    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_back);
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
    public void onMeasurementItemsCreated(Map<String, Double> newEntries, String date) {
        measurementsGraphModel.createMeasurementItems(newEntries, date);
    }


    @Override
    public void onItemDeleted(Measurement item, String bodyPart) {
        measurementsGraphModel.deleteItem(item, bodyPart);
    }

    @Override
    public void onStop() {
        super.onStop();
        measurementsGraphModel.removeListeners();
    }
}
