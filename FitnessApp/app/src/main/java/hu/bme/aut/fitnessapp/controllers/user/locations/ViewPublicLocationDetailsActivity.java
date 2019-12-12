package hu.bme.aut.fitnessapp.controllers.user.locations;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.models.user_models.location_models.ViewPublicLocationDetailsModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.entities.PublicLocation;

public class ViewPublicLocationDetailsActivity extends InternetCheckActivity implements ViewPublicLocationDetailsModel.DisplayReadyListener{

    private ArrayList<TextView> textViews;
    private TextView equipmentTV;
    private TextView descriptionTV;
    private TextView addressTV;

    private ViewPublicLocationDetailsModel viewPublicLocationDetailsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_public_location_details);

        setToolbar();
        setFloatingActionButtonListener();
        initializeTextViews();

        Intent i = getIntent();
        publicLocation = (PublicLocation) i.getSerializableExtra("location");
    }

    private PublicLocation publicLocation;

    @Override
    public void onStart() {
        super.onStart();
        viewPublicLocationDetailsModel = new ViewPublicLocationDetailsModel(this, publicLocation);
        viewPublicLocationDetailsModel.loadEquipment();
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

    public void setFloatingActionButtonListener() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_map);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewPublicLocationDetailsActivity.this, MapActivity.class);
                intent.putExtra("location", viewPublicLocationDetailsModel.getPublicLocation());
                startActivity(intent);
            }
        });
    }


    public void setTitle(String name) {
        getSupportActionBar().setTitle(name);
    }



    public void setTextViews(String description, String fullAddress, String equipmentText, List<String> hourText) {
        descriptionTV.setText(description);
        addressTV.setText(fullAddress);
        equipmentTV.setText(equipmentText);

        for(int i = 0; i < hourText.size(); i++) {
            textViews.get(i).setText(hourText.get(i));
        }

    }

    public void initializeTextViews() {
        descriptionTV = findViewById(R.id.locationDescription);
        addressTV = findViewById(R.id.locationAddress);
        equipmentTV = findViewById(R.id.locationEquipment);

        textViews = new ArrayList<>();
        TextView mondayTV = findViewById(R.id.locationMonday);
        textViews.add(mondayTV);
        TextView tuesdayTV = findViewById(R.id.locationTuesday);
        textViews.add(tuesdayTV);
        TextView wednesdayTV = findViewById(R.id.locationWednesday);
        textViews.add(wednesdayTV);
        TextView thursdayTV = findViewById(R.id.locationThursday);
        textViews.add(thursdayTV);
        TextView fridayTV = findViewById(R.id.locationFriday);
        textViews.add(fridayTV);
        TextView saturdayTV = findViewById(R.id.locationSaturday);
        textViews.add(saturdayTV);
        TextView sundayTV = findViewById(R.id.locationSunday);
        textViews.add(sundayTV);
    }

    @Override
    public void onTitleReady(String title) {
        setTitle(title);
    }

    @Override
    public void onDetailsReady(String desc, String address, String equipment, ArrayList<String> hours) {
        setTextViews(desc, address, equipment, hours);
    }

    @Override
    public void onStop() {
        super.onStop();
        viewPublicLocationDetailsModel.removeListeners();
    }
}
