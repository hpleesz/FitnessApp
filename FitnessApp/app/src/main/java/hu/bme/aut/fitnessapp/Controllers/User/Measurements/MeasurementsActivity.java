package hu.bme.aut.fitnessapp.Controllers.User.Measurements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.Models.UserModels.MeasurementModels.MeasurementsModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Controllers.User.NavigationActivity;

public class MeasurementsActivity extends NavigationActivity implements MeasurementsModel.GenderListener, MeasurementsModel.CurrentMeasurementsListener{

    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        MeasurementsModel measurementsModel = new MeasurementsModel(this);
        measurementsModel.loadUser();


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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        TextView shoulderTextView = (TextView) findViewById(R.id.shoulderTextView);
        textViews.add(shoulderTextView);
        TextView chestTextView = (TextView) findViewById(R.id.chestTextView);
        textViews.add(chestTextView);
        TextView waistTextView = (TextView) findViewById(R.id.waistTextView);
        textViews.add(waistTextView);
        TextView hipsTextView = (TextView) findViewById(R.id.hipsTextView);
        textViews.add(hipsTextView);
        TextView rightUpperArmTextView = (TextView) findViewById(R.id.rightUpperArmTextView);
        textViews.add(rightUpperArmTextView);
        TextView leftUpperArmTextView = (TextView) findViewById(R.id.leftUpperArmTextView);
        textViews.add(leftUpperArmTextView);
        TextView rightForearmTextView = (TextView) findViewById(R.id.rightForearmTextView);
        textViews.add(rightForearmTextView);
        TextView leftForearmTextView = (TextView) findViewById(R.id.leftForearmTextView);
        textViews.add(leftForearmTextView);
        TextView rightThighTextView = (TextView) findViewById(R.id.rightThighTextView);
        textViews.add(rightThighTextView);
        TextView leftThighTextView = (TextView) findViewById(R.id.leftThighTextView);
        textViews.add(leftThighTextView);
        TextView rightCalfTextView = (TextView) findViewById(R.id.rightCalfTextView);
        textViews.add(rightCalfTextView);
        TextView leftCalfTextView = (TextView) findViewById(R.id.leftCalfTextView);
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
}
