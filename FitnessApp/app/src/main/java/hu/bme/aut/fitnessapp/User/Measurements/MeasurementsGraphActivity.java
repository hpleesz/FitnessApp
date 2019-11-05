package hu.bme.aut.fitnessapp.User.Measurements;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import hu.bme.aut.fitnessapp.InternetCheckActivity;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Adapters.GraphPagerAdapter;
import hu.bme.aut.fitnessapp.Adapters.MeasurementAdapter;
import hu.bme.aut.fitnessapp.Models.Measurement;

public class MeasurementsGraphActivity extends InternetCheckActivity implements NewMeasurementItemDialogFragment.NewMeasurementDialogListener, MeasurementAdapter.MeasurementItemDeletedListener {

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements_graph);

        setToolbar();
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new GraphPagerAdapter(getSupportFragmentManager()));

        setFloatingActionButton();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Measurements").child(userId);
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
        for(Map.Entry<String, Double> entry : new_entries.entrySet()) {
            databaseReference.child(entry.getKey()).child(date).setValue(entry.getValue());
        }
    }


    @Override
    public void onItemDeleted(Measurement item, String body_part) {
        databaseReference.child(body_part).child(item.date).removeValue();
    }
}
