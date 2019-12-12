package hu.bme.aut.fitnessapp.controllers.user.workout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.entities.Exercise;
import hu.bme.aut.fitnessapp.models.user_models.workout_models.MainModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.user.NavigationActivity;
import hu.bme.aut.fitnessapp.entities.Location;
import hu.bme.aut.fitnessapp.entities.PublicLocation;

public class MainActivity extends NavigationActivity implements ChooseLocationItemDialogFragment.ChooseLocationItemDialogListener, ChooseLocationItemDialogFragment.ChooseOwnLocationItemDialogListener,
MainModel.DataReadyListener, MainModel.LocationChosenListener, MainModel.ContinueWorkoutListener{

    private ProgressBar progressBar;
    private TextView location;
    private Button workoutButton;

    private MainModel mainModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(0).setChecked(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.hide();

        location = findViewById(R.id.chooseLocationTextView);
        workoutButton = findViewById(R.id.workoutButton);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainModel = new MainModel(this);
        mainModel.loadEquipment();
        mainModel.loadLowerBodyParts();
        mainModel.loadUpperBodyParts();
        Log.d("loadexercises", "load");
        mainModel.loadExercises();
        location.setText(R.string.choose_location);

    }

    public void setButtonsOnClickListeners() {
        workoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainModel.isChosen()) {

                    Intent exercisesIntent = new Intent(MainActivity.this, ExerciseListActivity.class);
                    exercisesIntent.putExtra("list", (ArrayList<Exercise>)mainModel.getChosenExercises());
                    startActivity(exercisesIntent);
                    
                }

                else {
                    Toast toast = Toast.makeText(getApplication().getApplicationContext(), R.string.choose_location_toast, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        Button warmupButton = findViewById(R.id.warmupButton);
        warmupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent warmupIntent = new Intent(MainActivity.this, WarmUpActivity.class);
                warmupIntent.putExtra("type", mainModel.getWorkoutDetails().getType());
                startActivity(warmupIntent);
            }
        });

        Button stretchButton = findViewById(R.id.stretchButton);
        stretchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stretchIntent = new Intent(MainActivity.this, StretchActivity.class);
                startActivity(stretchIntent);
            }
        });
    }

    public void setChooseLocationOnClickListener() {
        TextView chooseLocationTV = findViewById(R.id.chooseLocationTextView);
        chooseLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChooseLocationItemDialogFragment().show(getSupportFragmentManager(), ChooseLocationItemDialogFragment.TAG);
            }
        });
    }


    @Override
    public void onLocationItemChosen(Location item) {
        progressBar = findViewById(R.id.progressBar);
        showProgressBar();

        mainModel.locationChosen(item);

    }

    @Override
    public void onLocationItemChosen(PublicLocation loc) {
        mainModel.setLocationListener(loc);

        progressBar = findViewById(R.id.progressBar);
        showProgressBar();
    }

    public void setLocationName(String name) {
        location.setText(name);
    }

    public void showWorkout() {
        Intent exercisesIntent = new Intent(MainActivity.this, ExerciseInfoActivity.class);
        exercisesIntent.putExtra("exercises", (ArrayList<Exercise>)mainModel.getChosenExercises());
        startActivity(exercisesIntent);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        workoutButton.setEnabled(true);
        workoutButton.setBackground(getResources().getDrawable(R.drawable.button_round));
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        workoutButton.setEnabled(false);
        workoutButton.setBackground(getResources().getDrawable(R.drawable.button_round_disabled));
    }

    @Override
    public void onButtonsReady() {
        setButtonsOnClickListeners();
        setChooseLocationOnClickListener();
    }

    @Override
    public void onLocationChosen(String name) {
        setLocationName(name);
    }

    @Override
    public void workoutSelected() {
        hideProgressBar();
    }

    @Override
    public void onContinueWorkout() {
        Log.d("continue", "cont");
        showWorkout();
    }

    @Override
    public void onStop() {
        super.onStop();
        mainModel.removeListeners();
    }
}