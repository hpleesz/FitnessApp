package hu.bme.aut.fitnessapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.data.measurement.MeasurementDatabase;
import hu.bme.aut.fitnessapp.data.measurement.MeasurementItem;
import hu.bme.aut.fitnessapp.fragments.NewMeasurementItemDialogFragment;

public class MeasurementsActivity  extends NavigationActivity {

    private ImageView measurementsMale;
    private MeasurementDatabase database;
    private List<MeasurementItem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SharedPreferences sharedPreferences = getSharedPreferences(UserActivity.USER, MODE_PRIVATE);
        View contentView;
        if(sharedPreferences.getBoolean("Male", true)){
            contentView = inflater.inflate(R.layout.activity_measurements_male, null, false);

        }
        else {
            contentView = inflater.inflate(R.layout.activity_measurements_female, null, false);

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
        database = Room.databaseBuilder(
                getApplicationContext(),
                MeasurementDatabase.class,
                "measurements"
        ).build();

        loadDatabase();
        setCurrentMeasurements();

    }

    private void loadDatabase() {
        new AsyncTask<Void, Void, List<MeasurementItem>>() {

            @Override
            protected List<MeasurementItem> doInBackground(Void... voids) {
                list = database.measurementItemDao().getAll();
                return list;
            }
        }.execute();
    }

    public void setCurrentMeasurements() {
        ArrayList<TextView> textViews = new ArrayList<>();
        TextView shoulderTextView = (TextView)findViewById(R.id.shoulderTextView);
        textViews.add(shoulderTextView);
        TextView chestTextView = (TextView)findViewById(R.id.chestTextView);
        textViews.add(chestTextView);
        TextView waistTextView = (TextView)findViewById(R.id.waistTextView);
        textViews.add(waistTextView);
        TextView hipsTextView = (TextView)findViewById(R.id.hipsTextView);
        textViews.add(hipsTextView);
        TextView rightUpperArmTextView = (TextView)findViewById(R.id.rightUpperArmTextView);
        textViews.add(rightUpperArmTextView);
        TextView leftUpperArmTextView = (TextView)findViewById(R.id.leftUpperArmTextView);
        textViews.add(leftUpperArmTextView);
        TextView rightForearmTextView = (TextView)findViewById(R.id.rightForearmTextView);
        textViews.add(rightForearmTextView);
        TextView leftForearmTextView = (TextView)findViewById(R.id.leftForearmTextView);
        textViews.add(leftForearmTextView);
        TextView rightThighTextView = (TextView)findViewById(R.id.rightThighTextView);
        textViews.add(rightThighTextView);
        TextView leftThighTextView = (TextView)findViewById(R.id.leftThighTextView);
        textViews.add(leftThighTextView);
        TextView rightCalfTextView = (TextView)findViewById(R.id.rightCalfTextView);
        textViews.add(rightCalfTextView);
        TextView leftCalfTextView = (TextView)findViewById(R.id.leftCalfTextView);
        textViews.add(leftCalfTextView);

        SharedPreferences sharedPreferences = getSharedPreferences(MeasurementsGraphActivity.MEASUREMENTS, MODE_PRIVATE);
        for(int i = 0; i < NewMeasurementItemDialogFragment.body_parts.length; i++) {
            String text = sharedPreferences.getString(NewMeasurementItemDialogFragment.body_parts[i],"--");
            textViews.get(i).setText(text + " " + getString(R.string.cm));
        }
    }

}
