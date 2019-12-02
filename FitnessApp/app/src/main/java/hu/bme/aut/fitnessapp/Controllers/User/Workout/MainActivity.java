package hu.bme.aut.fitnessapp.Controllers.User.Workout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Models.UserModels.WorkoutModels.MainModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Controllers.User.NavigationActivity;
import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Exercise;
import hu.bme.aut.fitnessapp.Entities.Location;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Entities.WorkoutDetails;

public class MainActivity extends NavigationActivity implements ChooseLocationItemDialogFragment.ChooseLocationItemDialogListener, ChooseLocationItemDialogFragment.ChooseOwnLocationItemDialogListener,
MainModel.DataReadyListener, MainModel.LocationChosenListener, MainModel.ContinueWorkoutListener{

    private ProgressBar progressBar;
    private TextView location;
    private Button workoutButton;

    public static final String WORKOUT = "workout settings";
    int location_id;

    private ArrayList<Exercise> chosenExercises;
    private ArrayList<Exercise> exercisesForLocation;
    private ArrayList<Integer> equipment_ids;



    private ArrayList<Equipment> equipmentList;
    private ArrayList<Exercise> exerciseList;
    private WorkoutDetails workoutDetails;
    private ArrayList<String> lower_body_parts;
    private ArrayList<String> upper_body_parts;
    private boolean chosen;

    private PublicLocation publicLocation;

    private MainModel mainModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(0).setChecked(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        location = findViewById(R.id.chooseLocationTextView);
        workoutButton = (Button) findViewById(R.id.workoutButton);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainModel = new MainModel(this);
        //mainModel.initFirebase();
        mainModel.loadEquipment();
        mainModel.loadLowerBodyParts();
        mainModel.loadUpperBodyParts();
        mainModel.loadExercises();
    }

    public void setButtonsOnClickListeners() {
        workoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                //location_id = sharedPreferences.getInt("Location", 0);
                //if (location_id > 0) {
                if(mainModel.isChosen()) {

                    Intent exercisesIntent = new Intent(MainActivity.this, ExerciseListActivity.class);
                    exercisesIntent.putExtra("list", mainModel.getChosenExercises());
                    //exercisesIntent.putExtra("equipment", mainModel.getEquipmentList());
                    startActivity(exercisesIntent);
                    
                }

                else {
                    Toast toast = Toast.makeText(getApplication().getApplicationContext(), R.string.choose_location_toast, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        Button warmupButton = (Button) findViewById(R.id.warmupButton);
        warmupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent warmupIntent = new Intent(MainActivity.this, WarmUpActivity.class);
                warmupIntent.putExtra("type", (String) mainModel.getWorkoutDetails().type);
                startActivity(warmupIntent);
            }
        });

        Button stretchButton = (Button) findViewById(R.id.stretchButton);
        stretchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stretchIntent = new Intent(MainActivity.this, StretchActivity.class);
                //stretchIntent.putExtra("list", (ArrayList<StretchItem>) stretchList);
                startActivity(stretchIntent);
            }
        });
    }

    public void setChooseLocationOnClickListener() {
        TextView chooseLocationTV = (TextView) findViewById(R.id.chooseLocationTextView);
        chooseLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChooseLocationItemDialogFragment().show(getSupportFragmentManager(), ChooseLocationItemDialogFragment.TAG);
            }
        });
    }


    /*
    public void setLocationText() {
        String locationName = sharedPreferences.getString("Location Name", getString(R.string.choose_location));
        location.setText(locationName);
        if (sharedPreferences.getInt("Location", 0) != 0) {
            getSavedExercises();
        }
    }


    public void getSavedExercises() {
        new AsyncTask<Void, Void, List<ExerciseItem>>() {

            @Override
            protected List<ExerciseItem> doInBackground(Void... voids) {
                int numberOfExercises = sharedPreferences.getInt("Number of exercises", 6);
                for (int i = 0; i < numberOfExercises; i++) {
                    ExerciseItem item = database.exerciseItemDao().getExerciseWithID(sharedPreferences.getLong("Exercise " + i, 1));
                    chosenExercises.add(item);
                }
                return chosenExercises;
            }
        }.execute();
    }

     */



    @Override
    public void onLocationItemChosen(Location item) {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        showProgressBar();

        mainModel.locationChosen(item);

    }

    @Override
    public void onLocationItemChosen(PublicLocation loc) {
        mainModel.setLocationListener(loc);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        showProgressBar();
    }

    public void setLocationName(String name) {
        location.setText(name);
    }

    public void showWorkout() {
        Intent exercisesIntent = new Intent(MainActivity.this, ExerciseInfoActivity.class);
        exercisesIntent.putExtra("exercises", mainModel.getChosenExercises());
        //exercisesIntent.putExtra("equipment", mainModel.getEquipmentList());
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










    public ArrayList<Integer> getEquipment_ids() {
        return equipment_ids;
    }

    public void setEquipment_ids(ArrayList<Integer> equipment_ids) {
        this.equipment_ids = equipment_ids;
    }

    public ArrayList<Exercise> getExerciseList() {
        return exerciseList;
    }

    public void setExerciseList(ArrayList<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    public ArrayList<Exercise> getExercisesForChosenLocation() {
        return exercisesForLocation;
    }

    public void setExercisesForLocation(ArrayList<Exercise> exercisesForLocation) {
        this.exercisesForLocation = exercisesForLocation;
    }

    public void setChosenExercises(ArrayList<Exercise> chosenExercises) {
        this.chosenExercises = chosenExercises;
    }

    public ArrayList<Exercise> getChosenExercises() {
        return chosenExercises;
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
        showWorkout();
    }

    @Override
    public void onStop() {
        super.onStop();
        mainModel.removeListeners();
    }
}