package hu.bme.aut.fitnessapp.controllers.user.measurements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.models.user_models.measurement_models.MeasurementsModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.user.NavigationActivity;

public class MeasurementsActivity extends NavigationActivity implements MeasurementsModel.GenderListener, MeasurementsModel.CurrentMeasurementsListener{

    private LayoutInflater inflater;
    private MeasurementsModel measurementsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        measurementsModel = new MeasurementsModel(this);
        measurementsModel.loadUser();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void setDrawerLayout(int gender) {
        View contentView;
        if (gender == 0) {
            contentView = inflater.inflate(R.layout.activity_measurements_male_2, null, false);

        } else {
            contentView = inflater.inflate(R.layout.activity_measurements_female_2, null, false);

        }
        mDrawerLayout.addView(contentView, 0);
        navigationView.getMenu().getItem(1).setChecked(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageDrawable(getDrawable(R.drawable.graph_white));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeasurementsActivity.this, MeasurementsGraphActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setCurrentMeasurements(List<String> list) {
        ArrayList<TextView> textViews = new ArrayList<>();
        TextView shoulderTextView = findViewById(R.id.shoulderTextView);
        textViews.add(shoulderTextView);
        TextView chestTextView = findViewById(R.id.chestTextView);
        textViews.add(chestTextView);
        TextView waistTextView = findViewById(R.id.waistTextView);
        textViews.add(waistTextView);
        TextView hipsTextView = findViewById(R.id.hipsTextView);
        textViews.add(hipsTextView);
        TextView rightUpperArmTextView = findViewById(R.id.rightUpperArmTextView);
        textViews.add(rightUpperArmTextView);
        TextView leftUpperArmTextView = findViewById(R.id.leftUpperArmTextView);
        textViews.add(leftUpperArmTextView);
        TextView rightForearmTextView = findViewById(R.id.rightForearmTextView);
        textViews.add(rightForearmTextView);
        TextView leftForearmTextView = findViewById(R.id.leftForearmTextView);
        textViews.add(leftForearmTextView);
        TextView rightThighTextView = findViewById(R.id.rightThighTextView);
        textViews.add(rightThighTextView);
        TextView leftThighTextView = findViewById(R.id.leftThighTextView);
        textViews.add(leftThighTextView);
        TextView rightCalfTextView = findViewById(R.id.rightCalfTextView);
        textViews.add(rightCalfTextView);
        TextView leftCalfTextView = findViewById(R.id.leftCalfTextView);
        textViews.add(leftCalfTextView);

        for(int i = 0; i < list.size(); i++) {
            textViews.get(i).setText(list.get(i));

        }

    }

    @Override
    public void onMeasurementsLoaded(ArrayList<String> measurements) {
        setCurrentMeasurements(measurements);
    }

    @Override
    public void onGenderLoaded(int gender) {
        setDrawerLayout(gender);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        measurementsModel.removeListeners();
    }
}
