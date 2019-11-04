package hu.bme.aut.fitnessapp.User.Workout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.User.NavigationActivity;
import hu.bme.aut.fitnessapp.Models.Equipment;
import hu.bme.aut.fitnessapp.Models.Exercise;
import hu.bme.aut.fitnessapp.Models.Location;
import hu.bme.aut.fitnessapp.Models.Place;
import hu.bme.aut.fitnessapp.Models.PublicLocation;
import hu.bme.aut.fitnessapp.Models.WorkoutDetails;

public class MainActivity extends NavigationActivity implements ChooseLocationItemDialogFragment.ChooseLocationItemDialogListener, ChooseLocationItemDialogFragment.ChooseOwnLocationItemDialogListener {

    private ProgressBar progressBar;
    private TextView location;
    private Button workoutButton;

    public static final String WORKOUT = "workout settings";
    int location_id;
    private ArrayList<Exercise> chosenExercises;
    private ArrayList<Exercise> exercisesForLocation;
    private ArrayList<Integer> equipment_ids;
    private ArrayList<Equipment> equipmentList;
    private ArrayList<String> lower_body_parts;
    private ArrayList<String> upper_body_parts;

    //public static String[] lower_body_parts = {"Quads", "Glutes", "Legs", "Adductor", "Abductor", "Hamstrings", "Calves"};
    //public static String[] upper_body_parts = {"Abs", "Back", "Shoulders", "Chest", "Triceps", "Obliques", "Arms", "Biceps", "Lats", "Forearms"};

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String userId;

    private PublicLocation publicLocation;
    private ArrayList<Exercise> exerciseList;
    private WorkoutDetails workoutDetails;

    private boolean chosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(0).setChecked(true);

        chosen = false;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        chosenExercises = new ArrayList<>();
        location = findViewById(R.id.chooseLocationTextView);
        workoutButton = (Button) findViewById(R.id.workoutButton);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        loadWorkoutDetails();
        loadEquipment();
        loadLowerBodyParts();
        loadUpperBodyParts();
        loadExercises();

        //TODO: setLocationText();

    }

    private void loadLowerBodyParts() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lower_body_parts = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String name = (String) dataSnapshot1.getValue();
                    lower_body_parts.add(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Muscles").child("Lower").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }

    private void loadUpperBodyParts() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                upper_body_parts = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String name = (String) dataSnapshot1.getValue();
                    upper_body_parts.add(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Muscles").child("Upper").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }

    private void loadEquipment() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                equipmentList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    int id = Integer.parseInt(dataSnapshot1.getKey());
                    String name = (String) dataSnapshot1.getValue();
                    Equipment equipment = new Equipment(id, name);
                    equipmentList.add(equipment);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Equipment").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }

    private void loadExercises() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                exerciseList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String id = dataSnapshot1.getKey();
                    int equipment1 = dataSnapshot1.child("Equipment1").getValue(Integer.class);
                    int equipment2 = dataSnapshot1.child("Equipment2").getValue(Integer.class);
                    String muscles = dataSnapshot1.child("Muscles").getValue(String.class);
                    String name = dataSnapshot1.child("Name").getValue(String.class);
                    int reps_time = dataSnapshot1.child("Rep_Time").getValue(Integer.class);

                    String[] muscleArray = muscles.split(", ");

                    Exercise exercise = new Exercise(Integer.parseInt(id), equipment1,equipment2, muscleArray, name, reps_time);
                    exerciseList.add(exercise);
                }
                continueWorkout();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Exercises").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }

    private void loadWorkoutDetails() {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type = dataSnapshot.child("Type").getValue(String.class);
                boolean in_progress = dataSnapshot.child("In_Progress").getValue(Boolean.class);

                ArrayList<Integer> exercises = new ArrayList<>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.child("Exercises").getChildren()) {
                    int exercise_id = dataSnapshot1.getValue(Integer.class);
                    exercises.add(exercise_id);
                }
                workoutDetails = new WorkoutDetails(type, exercises, in_progress);
                setButtonsOnClickListeners();
                setChooseLocationOnClickListener();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Workout_Details").child(userId).addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener

    }

    public void setButtonsOnClickListeners() {
        workoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                //location_id = sharedPreferences.getInt("Location", 0);
                //if (location_id > 0) {
                if(chosen) {
                    Intent exercisesIntent = new Intent(MainActivity.this, ExerciseListActivity.class);
                    exercisesIntent.putExtra("list", chosenExercises);
                    exercisesIntent.putExtra("equipment", equipmentList);
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
                warmupIntent.putExtra("type", (String) workoutDetails.type);
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


    @Override
    public void onLocationItemChosen(PublicLocation loc) {
        setLocationListener(loc);
        chosen = true;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        workoutButton.setEnabled(false);
        workoutButton.setBackground(getResources().getDrawable(R.drawable.button_round_disabled));
    }

    public void setLocationListener(PublicLocation loc) {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long id = Long.parseLong(dataSnapshot.getKey());
                String name = dataSnapshot.child("Name").getValue(String.class);
                String description = dataSnapshot.child("Description").getValue(String.class);
                String zip = dataSnapshot.child("Zip").getValue(String.class);
                String country = dataSnapshot.child("Country").getValue(String.class);
                String city = dataSnapshot.child("City").getValue(String.class);
                String address = dataSnapshot.child("Address").getValue(String.class);

                ArrayList<Integer> equipment = new ArrayList<>();

                for(DataSnapshot dataSnapshot2: dataSnapshot.child("Equipment").getChildren()) {
                    int idx = dataSnapshot2.getValue(Integer.class);
                    equipment.add(idx);
                }

                ArrayList<String[]> hours = new ArrayList<>();

                for(DataSnapshot dataSnapshot2: dataSnapshot.child("Open_Hours").getChildren()) {
                    String[] open_close = new String[2];

                    for (DataSnapshot dataSnapshot3: dataSnapshot2.getChildren()) {
                        int idx = Integer.parseInt(dataSnapshot3.getKey());
                        String hour = dataSnapshot3.getValue(String.class);

                        open_close[idx] = hour;
                    }
                    hours.add(open_close);
                }

                publicLocation = new PublicLocation(id, name, equipment, hours, description, zip, country, city, address, userId);
                String locationName = publicLocation.name;
                location.setText(locationName);
                getAvailableEquipment(publicLocation);
                getExercisesForLocation();
                makeWorkoutFromSelectedExercises();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Public_Locations").child(Long.toString(loc.id)).addValueEventListener(eventListener);

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


    public void getAvailableEquipment(Place loc) {
        equipment_ids = loc.equipment;

        if (equipment_ids.contains(5) && !equipment_ids.contains(4))
            equipment_ids.add(4);
        if (equipment_ids.contains(7) && !equipment_ids.contains(6))
            equipment_ids.add(6);
        if (!equipment_ids.contains(1)) equipment_ids.add(1);

    }

    public void getExercisesForLocation() {
        exercisesForLocation = new ArrayList<>();
        for(Exercise exercise : exerciseList) {
            if(equipment_ids.contains(exercise.equipment1) && equipment_ids.contains(exercise.equipment2))
                exercisesForLocation.add(exercise);
        }
    }

    public void makeWorkoutFromSelectedExercises() {
        chosenExercises = new ArrayList<>();
        ArrayList<String> body_parts = new ArrayList<>();

        switch (workoutDetails.type) {
            case "Upper body":
                while (body_parts.size() < 10) {
                    //TODO get body parts
                    body_parts.add(upper_body_parts.get(getRandomNumber(upper_body_parts.size())));
                }
                selectExercises(body_parts, 10);
                break;

            case "Lower body":
                while (body_parts.size() < 10) {
                    body_parts.add(lower_body_parts.get(getRandomNumber(lower_body_parts.size())));
                }
                selectExercises(body_parts, 10);
                break;

            case "Cardio 1":
            case "Cardio 2":
                while (body_parts.size() < 4) {
                    body_parts.add(upper_body_parts.get(getRandomNumber(upper_body_parts.size())));
                }
                while (body_parts.size() < 8) {
                    body_parts.add(lower_body_parts.get(getRandomNumber(lower_body_parts.size())));
                }
                selectExercises(body_parts, 8);
                selectCardio();
                break;
        }

        databaseReference.child("Workout_Details").child(userId).child("Exercises").removeValue();
        for (int j = 0; j < chosenExercises.size(); j++) {
            databaseReference.child("Workout_Details").child(userId).child("Exercises").child(Integer.toString(j)).setValue(chosenExercises.get(j).id);
        }
        progressBar.setVisibility(View.GONE);
        workoutButton.setEnabled(true);
        workoutButton.setBackground(getResources().getDrawable(R.drawable.button_round));

    }

    public int getRandomNumber(int max) {
        Random r = new Random();
        return r.nextInt(max);
    }

    public void selectExercises(ArrayList<String> body_parts, int limit) {
        for (int i = 0; i < limit; i++) {
            ArrayList<Exercise> exerciseItems = new ArrayList<>();
            String body_part = body_parts.get(i);
            for (int j = 0; j < exercisesForLocation.size(); j++) {
                for (int k = 0; k < exercisesForLocation.get(j).muscles.length; k++) {
                    String s = exercisesForLocation.get(j).muscles[k];
                    if (exercisesForLocation.get(j).muscles[k].contains(body_part))
                        exerciseItems.add(exercisesForLocation.get(j));
                }
            }
            if (exerciseItems.size() > 0) {
                int random = getRandomNumber(exerciseItems.size());
                Exercise exercise = exerciseItems.get(random);
                if (!chosenExercises.contains(exercise)) {
                    chosenExercises.add(exercise);
                    if (exercise.name.contains("left")) {
                        Exercise exerciseItem = exerciseItems.get(random + 1);
                        chosenExercises.add(exerciseItem);
                        i++;
                    }
                    if (exercise.name.contains("right")) {
                        Exercise exerciseItem = exerciseItems.get(random - 1);
                        chosenExercises.add(exerciseItem);
                        i++;
                    }
                } else i--;
            } else {
                if (workoutDetails.type.equals("Upper body")) {
                    body_parts.set(i, upper_body_parts.get(getRandomNumber(upper_body_parts.size())));
                    i--;
                } else {
                    body_parts.set(i, lower_body_parts.get(getRandomNumber(lower_body_parts.size())));
                    i--;
                }
            }
        }
    }

    public void selectCardio() {
        ArrayList<Exercise> cardio = new ArrayList<>();
        for (int i = exercisesForLocation.size() - 1; i >= 0; i--) {
            if (exercisesForLocation.get(i).muscles[0].contains("Cardiovascular System"))
                cardio.add(exercisesForLocation.get(i));
        }
        int random = getRandomNumber(cardio.size());
        chosenExercises.add(cardio.get(random));
    }

    @Override
    public void onLocationItemChosen(Location item) {
        chosen = true;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        workoutButton.setEnabled(false);
        workoutButton.setBackground(getResources().getDrawable(R.drawable.button_round_disabled));

        String locationName = item.name;
        location.setText(locationName);
        getAvailableEquipment(item);
        getExercisesForLocation();
        makeWorkoutFromSelectedExercises();
    }


    public void continueWorkout() {
        if (workoutDetails.in_progress) {

            for(int id : workoutDetails.exercises) {
                chosenExercises.add(exerciseList.get(id-1));
            }

            Intent exercisesIntent = new Intent(MainActivity.this, ExerciseInfoActivity.class);
            exercisesIntent.putExtra("exercises", chosenExercises);
            exercisesIntent.putExtra("equipment", equipmentList);
            startActivity(exercisesIntent);

        }
    }

}