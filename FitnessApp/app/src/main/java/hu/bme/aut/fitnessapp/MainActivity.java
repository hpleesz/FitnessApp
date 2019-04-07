package hu.bme.aut.fitnessapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import hu.bme.aut.fitnessapp.broadcast_receiver.BootReceiver;
import hu.bme.aut.fitnessapp.broadcast_receiver.NotificationReceiver;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentItem;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentListDatabase;
import hu.bme.aut.fitnessapp.data.exercise.ExerciseAdapter;
import hu.bme.aut.fitnessapp.data.exercise.ExerciseItem;
import hu.bme.aut.fitnessapp.data.exercise.ExerciseListDatabase;
import hu.bme.aut.fitnessapp.data.location.LocationItem;
import hu.bme.aut.fitnessapp.data.location.LocationListDatabase;
import hu.bme.aut.fitnessapp.data.weight.WeightAdapter;
import hu.bme.aut.fitnessapp.data.weight.WeightItem;
import hu.bme.aut.fitnessapp.data.weight.WeightListDatabase;
import hu.bme.aut.fitnessapp.fragments.ChooseLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewLocationItemDialogFragment;

public class MainActivity extends NavigationActivity implements ChooseLocationItemDialogFragment.ChooseLocationItemDialogListener{

    public static final String FIRST = "first sign in";

    private ExerciseListDatabase database;
    private LocationListDatabase locationdatabase;
    private EquipmentListDatabase equipmentdatabase;

    public static final String WORKOUT = "workout settings";
    private SharedPreferences sharedPreferences;
    int location_id;
    private ArrayList<ExerciseItem> chosenExercises;
    private ArrayList<ExerciseItem> exercisesForLocation;
    private ArrayList<Integer> equipment_ids;
    private List<LocationItem> locations;
    private ArrayList<EquipmentItem> equipmentArrayList;
    private String[] lower_body_parts = {"Quads", "Glutes", "Legs", "Adductor", "Abductor", "Hamstrings", "Calves"};
    private String[] upper_body_parts = {"Abs", "Back", "Shoulders", "Chest", "Triceps", "Obliques", "Arms", "Biceps", "Lats", "Forearms"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(0).setChecked(true);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        chosenExercises = new ArrayList<>();
        equipmentArrayList = new ArrayList<>();
        sharedPreferences = getSharedPreferences(WORKOUT, MODE_PRIVATE);

        initLocationDatabase();
        setButtonsOnClickListeners();
        checkFirstSignIn();
        setChooseLocationOnClickListener();
        continueWorkout();
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

    public void setButtonsOnClickListeners(){
        Button workoutButton = (Button) findViewById(R.id.workoutButton);
        workoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(location_id > 0) {
                    Intent exercisesIntent = new Intent(MainActivity.this, ExerciseListActivity.class);
                    exercisesIntent.putExtra("list", chosenExercises);
                    exercisesIntent.putExtra("equipment", equipmentArrayList);
                    startActivity(exercisesIntent);
                }
                else {
                    Toast toast = Toast.makeText(getApplication().getApplicationContext(), R.string.choose_location_toast, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public void checkFirstSignIn() {
        SharedPreferences first = getSharedPreferences(FIRST, MODE_PRIVATE);
        boolean isFirst = first.getBoolean("First", true);
        boolean loadDatabase = first.getBoolean("Load database", true);
        if(isFirst) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("Completed workout", true);
            editor.apply();
            Intent userIntent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(userIntent);
        }
        else {
            if(loadDatabase) {
                SharedPreferences.Editor editor = first.edit();
                editor.putBoolean("Load database", false);
                editor.apply();
                initializeDatabase();
            }
            else {
                database = Room.databaseBuilder(
                        getApplicationContext(),
                        ExerciseListDatabase.class,
                        "exercises"
                ).build();
            }
        }
    }

    public ArrayList<ExerciseItem> fillExerciseList() {
        Resources resources = getResources();
        String str;
        ArrayList<ExerciseItem> exerciseItems = new ArrayList<>();

        int resID = resources.getIdentifier("hu.bme.aut.fitnessapp:raw/" + "exercises", null, null);
        InputStream is = resources.openRawResource(resID);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((str = br.readLine()) != null) {
                String[] line = str.split("\t");
                ExerciseItem newItem = new ExerciseItem();

                newItem.exercise_name = line[0];

                newItem.equipment1 = Integer.parseInt(line[1]);
                newItem.equipment2 = Integer.parseInt(line[2]);
                String[] muscles = line[3].split(", ");
                ArrayList<String> musclesList = new ArrayList<>();
                for(int i = 0; i < muscles.length; i++){
                    musclesList.add(muscles[i]);
                }
                newItem.exercise_muscles = musclesList;
                newItem.reps_time = Integer.parseInt(line[4]);

                exerciseItems.add(newItem);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exerciseItems;
    }

    public void initLocationDatabase() {
        locationdatabase = Room.databaseBuilder(
                getApplicationContext(),
                LocationListDatabase.class,
                "locations"
        ).build();

        new AsyncTask<Void, Void, List<LocationItem>>() {

            @Override
            protected List<LocationItem> doInBackground(Void... voids) {
                locations = locationdatabase.locationItemDao().getAll();
                return locations;
            }
        }.execute();

        equipmentdatabase = Room.databaseBuilder(
                getApplicationContext(),
                EquipmentListDatabase.class,
                "equipments"
        ).build();
    }

    @Override
    public void onLocationItemChosen() {
        new AsyncTask<Void, Void, List<ExerciseItem>>() {

            @Override
            protected List<ExerciseItem> doInBackground(Void... voids) {
                if(sharedPreferences.getBoolean("Completed workout", true)){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("Completed workout", false);
                    editor.apply();
                    setWorkoutType();
                }
                loadEquipments();
                getAvailableEquipment();

                return exercisesForLocation;
            }
        }.execute();


    }

    public void setWorkoutType() {
        SharedPreferences user_shared_preferences = getSharedPreferences(UserActivity.USER, MODE_PRIVATE);
        boolean muscle = user_shared_preferences.getBoolean("Gain muscle", true);
        boolean weight = user_shared_preferences.getBoolean("Lose weight", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(muscle) {
            String type = sharedPreferences.getString("Workout type", "Lower body");
            if(weight) {
                switch(type) {
                    case "Lower body" :
                        editor.putString("Workout type", "Cardio 1");
                        break;
                    case "Cardio 1" :
                        editor.putString("Workout type", "Upper body");
                        break;
                    case "Upper body" :
                        editor.putString("Workout type", "Cardio 2");
                        break;
                    case "Cardio 2" :
                        editor.putString("Workout type", "Lower body");
                }
            }
            else {
                switch(type) {
                    case "Lower body":
                        editor.putString("Workout type", "Upper body");
                        break;
                    case "Upper body":
                        editor.putString("Workout type", "Lower body");
                        break;
                }
            }
        }
        else {
            editor.putString("Workout type", "Cardio 1");
        }
        editor.apply();
    }

    public void getAvailableEquipment() {
        new AsyncTask<Void, Void, List<EquipmentItem>>() {

            @Override
            protected List<EquipmentItem> doInBackground(Void... voids) {
                location_id =sharedPreferences.getInt("Location",1);
                LocationItem location = locationdatabase.locationItemDao().getLocationWithID(location_id);
                ArrayList<EquipmentItem> equipments = location.location_equipmentItems;
                equipment_ids = new ArrayList<>();
                for(
                        EquipmentItem item :equipments)

                {
                    equipment_ids.add(item.equipment_id);
                }
                if(equipment_ids.contains(5)&&!equipment_ids.contains(4))equipment_ids.add(4);
                if(equipment_ids.contains(7)&&!equipment_ids.contains(6))equipment_ids.add(6);
                if(!equipment_ids.contains(1))equipment_ids.add(1);
                return equipments;
            }

            @Override
            protected void onPostExecute(List<EquipmentItem> equipmentItems) {
                getExercisesForLocation();
            }

        }.execute();
    }

    public void getExercisesForLocation() {
        new AsyncTask<Void, Void, List<ExerciseItem>>() {
            @Override
            protected List<ExerciseItem> doInBackground(Void... voids) {
                exercisesForLocation = new ArrayList<>();
                //List<ExerciseItem> list2 = database.exerciseItemDao().getAll();
                for(int i = 0; i < equipment_ids.size(); i++){
                    for(int j = 0; j < equipment_ids.size(); j++){
                        List<ExerciseItem> list = database.exerciseItemDao().getExercisesWithEquipments(equipment_ids.get(i), equipment_ids.get(j));
                        exercisesForLocation.addAll(list);

                    }
                }
                return exercisesForLocation;
            }

            @Override
            protected void onPostExecute(List<ExerciseItem> exerciseItemList) {
                makeWorkout();
            }
        }.execute();
    }

    public void makeWorkout() {
        chosenExercises = new ArrayList<>();
        String workout_type = sharedPreferences.getString("Workout type", "");
        ArrayList<String> body_parts = new ArrayList<>();
        switch (workout_type){
            case "Upper body" :
                while (body_parts.size() < 10) {
                    body_parts.add(upper_body_parts[getRandomNumber(upper_body_parts.length)]);
                }
                selectExercises(body_parts, 10);
                break;

            case "Lower body" :
                while (body_parts.size() < 10) {
                    body_parts.add(lower_body_parts[getRandomNumber(lower_body_parts.length)]);
                }
                selectExercises(body_parts, 10);
                break;

            case "Cardio 1" :
            case "Cardio 2" :
                while (body_parts.size() < 3) {
                    body_parts.add(upper_body_parts[getRandomNumber(upper_body_parts.length)]);
                }
                while (body_parts.size() < 6) {
                    body_parts.add(lower_body_parts[getRandomNumber(lower_body_parts.length)]);
                }
                selectExercises(body_parts, 6);
                selectCardio();
                break;
        }

    }

    public int getRandomNumber(int max) {
        Random r = new Random();
        return r.nextInt(max);
    }

    public void selectExercises(ArrayList<String> body_parts, int limit) {
        for(int i = 0; i < limit; i++){
            ArrayList<ExerciseItem> exerciseItems = new ArrayList<>();
            String body_part = body_parts.get(i);
            for(int j = 0; j < exercisesForLocation.size(); j++){
                for(int k = 0; k < exercisesForLocation.get(j).exercise_muscles.size(); k++) {
                    String s = exercisesForLocation.get(j).exercise_muscles.get(k);
                    if (exercisesForLocation.get(j).exercise_muscles.get(k).contains(body_part))
                        exerciseItems.add(exercisesForLocation.get(j));
                }
            }
            if(exerciseItems.size() > 0) {
                int random = getRandomNumber(exerciseItems.size());
                ExerciseItem exercise = exerciseItems.get(random);
                if(!chosenExercises.contains(exercise)) {
                    chosenExercises.add(exercise);
                    if (exercise.exercise_name.contains("left")) {
                        ExerciseItem exerciseItem = exerciseItems.get(random + 1);
                        chosenExercises.add(exerciseItem);
                        i++;
                    }
                    if (exercise.exercise_name.contains("right")) {
                        ExerciseItem exerciseItem = exerciseItems.get(random - 1);
                        chosenExercises.add(exerciseItem);
                        i++;
                    }
                }
                else i--;
            }
            else {
                if(sharedPreferences.getString("Workout type", "").equals("Upper body")){
                    body_parts.set(i, upper_body_parts[getRandomNumber(upper_body_parts.length)]);
                    i--;
                }
                else {
                    body_parts.set(i, lower_body_parts[getRandomNumber(lower_body_parts.length)]);
                    i--;
                }
            }
        }

    }

    public void selectCardio() {
        ArrayList<ExerciseItem> cardio = new ArrayList<>();
        for(int i = exercisesForLocation.size()-1; i >= 0; i--){
            if(exercisesForLocation.get(i).exercise_muscles.get(0).contains("Cardiovascular System"))
                cardio.add(exercisesForLocation.get(i));
        }
        int random = getRandomNumber(cardio.size());
        chosenExercises.add(cardio.get(random));
    }

    public void continueWorkout() {
        boolean completed = sharedPreferences.getBoolean("Completed workout", false);
        if(!completed){
            new AsyncTask<Void, Void, List<ExerciseItem>>() {

                @Override
                protected List<ExerciseItem> doInBackground(Void... voids) {
                    int numberOfExercises = sharedPreferences.getInt("Number of exercises", 6);
                    for(int i = 0; i < numberOfExercises; i++) {
                        ExerciseItem item = database.exerciseItemDao().getExerciseWithID(sharedPreferences.getLong("Exercise " + i, 1));
                        chosenExercises.add(item);
                    }
                    return chosenExercises;
                }

                @Override
                protected void onPostExecute(List<ExerciseItem> exerciseItems) {
                    Intent exercisesIntent = new Intent(MainActivity.this, ExerciseInfoActivity.class);
                    exercisesIntent.putExtra("exercises", chosenExercises);
                    exercisesIntent.putExtra("equipment", equipmentArrayList);
                    startActivity(exercisesIntent);
                }

            }.execute();

        }
    }

    private void loadEquipments() {
        new AsyncTask<Void, Void, List<EquipmentItem>>() {

            @Override
            protected List<EquipmentItem> doInBackground(Void... voids) {
                List<EquipmentItem> equipmentItemList = equipmentdatabase.equipmentItemDao().getAll();
                //for(int i = 0; i < equipmentItemList.size(); i++){
                //equipmentArrayList.add(equipmentItemList.get(i));
                equipmentArrayList.addAll(equipmentItemList);
                //}
                return equipmentItemList;
            }
        }.execute();
    }

    public void initializeDatabase() {
        database = Room.databaseBuilder(
                getApplicationContext(),
                ExerciseListDatabase.class,
                "exercises"
        ).build();

        final ArrayList<ExerciseItem> list = fillExerciseList();

        new AsyncTask<Void, Void, List<ExerciseItem>>() {

            @Override
            protected List<ExerciseItem> doInBackground(Void... voids) {
                for(int i = 0; i < list.size(); i++){
                    database.exerciseItemDao().insert(list.get(i));
                }
                return list;
            }
        }.execute();

    }

}
