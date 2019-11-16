package hu.bme.aut.fitnessapp.Models.User.Workout;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import hu.bme.aut.fitnessapp.Controllers.User.Workout.MainActivity;
import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Exercise;
import hu.bme.aut.fitnessapp.Entities.Location;
import hu.bme.aut.fitnessapp.Entities.Place;
import hu.bme.aut.fitnessapp.Entities.PublicLocation;
import hu.bme.aut.fitnessapp.Entities.WorkoutDetails;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadBodyParts;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadEquipment;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadExercises;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadMuscles;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadPublicLocations;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadWorkoutDetails;

public class MainModel implements LoadEquipment.EquipmentLoadedListener, LoadMuscles.MusclesLoadedListener, LoadExercises.ExercisesLoadedListener, LoadWorkoutDetails.WorkoutDetailsLoadedListener, LoadPublicLocations.PublicLocationsByIDLoadedListener{

    private Context activity;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String userId;

    private PublicLocation publicLocation;

    private ArrayList<Exercise> chosenExercises;

    public void setChosenExercises(ArrayList<Exercise> chosenExercises) {
        this.chosenExercises = chosenExercises;
    }

    public void setExercisesForLocation(ArrayList<Exercise> exercisesForLocation) {
        this.exercisesForLocation = exercisesForLocation;
    }

    private ArrayList<Exercise> exercisesForLocation;

    public ArrayList<Integer> getEquipment_ids() {
        return equipment_ids;
    }

    public void setEquipment_ids(ArrayList<Integer> equipment_ids) {
        this.equipment_ids = equipment_ids;
    }

    public void setExerciseList(ArrayList<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    private ArrayList<Integer> equipment_ids;



    private ArrayList<Equipment> equipmentList;
    private ArrayList<Exercise> exerciseList;

    public WorkoutDetails getWorkoutDetails() {
        return workoutDetails;
    }

    private WorkoutDetails workoutDetails;
    private ArrayList<String> lower_body_parts;
    private ArrayList<String> upper_body_parts;
    private boolean chosen;

    public interface DataReadyListener {
        void onButtonsReady();
    }

    public interface LocationChosenListener {
        void onLocationChosen(String name);
    }
    public interface ContinueWorkoutListener {
        void onContinueWorkout();
    }
    private MainModel.DataReadyListener dataReadyListener;
    private MainModel.ContinueWorkoutListener continueWorkoutListener;
    private MainModel.LocationChosenListener locationChosenListener;

    public MainModel(Context activity) {
        dataReadyListener = (MainModel.DataReadyListener)activity;
        continueWorkoutListener = (MainModel.ContinueWorkoutListener)activity;
        locationChosenListener = (MainModel.LocationChosenListener)activity;

        this.activity = activity;


        chosenExercises = new ArrayList<>();
        chosen = false;

        //loadWorkoutDetails();
    }

    public void initFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
    }

    public void loadEquipment() {
        LoadEquipment loadEquipment = new LoadEquipment(this);
        loadEquipment.loadEquipment();
        /*
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

         */
    }

    @Override
    public void onEquipmentLoaded(ArrayList<Equipment> equipment) {
        equipmentList = equipment;
    }

    public void loadLowerBodyParts() {
        LoadMuscles loadMuscles = new LoadMuscles(this);
        loadMuscles.loadLowerBodyParts();
        /*

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

         */

    }

    public void loadUpperBodyParts() {
        LoadMuscles loadMuscles = new LoadMuscles(this);
        loadMuscles.loadUpperBodyParts();

    /*
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

     */

    }

    @Override
    public void onUpperBodyLoaded(ArrayList<String> muscles) {
        upper_body_parts = muscles;
    }

    @Override
    public void onLowerBodyLoaded(ArrayList<String> muscles) {
        lower_body_parts = muscles;
    }

    public void loadExercises() {
        LoadExercises loadExercises = new LoadExercises(this);
        loadExercises.loadExercises();
/*
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
                //continueWorkout();
                loadWorkoutDetails();

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

 */

    }

    @Override
    public void onExercisesLoaded(ArrayList<Exercise> exercises) {
        exerciseList = exercises;
        loadWorkoutDetails();
    }


    private void loadWorkoutDetails() {
        LoadWorkoutDetails loadWorkoutDetails = new LoadWorkoutDetails(this);
        loadWorkoutDetails.loadWorkoutDetails();

        /*
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

                dataReadyListener.onButtonsReady();
                //((MainActivity)activity).setButtonsOnClickListeners();
                //((MainActivity)activity).setChooseLocationOnClickListener();

                continueWorkout();

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

         */

    }


    @Override
    public void onWorkoutDetailsLoaded(WorkoutDetails workoutDetails) {
        this.workoutDetails = workoutDetails;
        dataReadyListener.onButtonsReady();
        continueWorkout();

    }

    public void continueWorkout() {
        if (workoutDetails.in_progress) {

            for(int id : workoutDetails.exercises) {
                chosenExercises.add(exerciseList.get(id-1));
            }
            continueWorkoutListener.onContinueWorkout();
            //((MainActivity)activity).showWorkout();
        }
    }

    public void locationChosen(Location item) {
        chosen = true;
        locationChosenListener.onLocationChosen(item.name);
        //((MainActivity)activity).setLocationName(item.name);

        getAvailableEquipment(item);
        getExercisesForLocation();
        makeWorkoutFromSelectedExercises();
    }

    public void setLocationListener(PublicLocation loc) {
        chosen = true;
        LoadPublicLocations loadPublicLocations = new LoadPublicLocations();
        loadPublicLocations.setListLoadedByIDListener(this);
        loadPublicLocations.loadPublicLocationByID(Long.toString(loc.id));

        /*
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
                locationChosenListener.onLocationChosen(publicLocation.name);
                //((MainActivity)activity).setLocationName(publicLocation.name);

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

         */

    }

    @Override
    public void onPublicLocationsByIDLoaded(PublicLocation publicLocation) {
        this.publicLocation = publicLocation;
        locationChosenListener.onLocationChosen(publicLocation.name);

        getAvailableEquipment(publicLocation);
        getExercisesForLocation();
        makeWorkoutFromSelectedExercises();
    }


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
        ((MainActivity)activity).hideProgressBar();

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

    public ArrayList<Exercise> getChosenExercises() {
        return chosenExercises;
    }

    public boolean isChosen() {
        return chosen;
    }
    public ArrayList<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public ArrayList<Exercise> getExercisesForChosenLocation() {
        return exercisesForLocation;
    }

}
