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
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
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
import hu.bme.aut.fitnessapp.data.stretch.StretchItem;
import hu.bme.aut.fitnessapp.data.stretch.StretchListDatabase;
import hu.bme.aut.fitnessapp.data.warmup.WarmUpItem;
import hu.bme.aut.fitnessapp.data.warmup.WarmUpListDatabase;
import hu.bme.aut.fitnessapp.data.weight.WeightAdapter;
import hu.bme.aut.fitnessapp.data.weight.WeightItem;
import hu.bme.aut.fitnessapp.data.weight.WeightListDatabase;
import hu.bme.aut.fitnessapp.fragments.ChooseLocationItemDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewLocationItemDialogFragment;

public class MainActivity extends NavigationActivity implements ChooseLocationItemDialogFragment.ChooseLocationItemDialogListener {

    public static final String FIRST = "first sign in";

    private ExerciseListDatabase database;
    private LocationListDatabase locationdatabase;
    private EquipmentListDatabase equipmentdatabase;
    private WarmUpListDatabase warmupdatabase;
    private StretchListDatabase stretchdatabase;
    private ProgressBar progressBar;
    private TextView location;
    private Button workoutButton;

    public static final String WORKOUT = "workout settings";
    private SharedPreferences sharedPreferences;
    int location_id;
    private ArrayList<ExerciseItem> chosenExercises;
    private ArrayList<ExerciseItem> exercisesForLocation;
    private ArrayList<Integer> equipment_ids;
    private List<LocationItem> locations;
    private ArrayList<EquipmentItem> equipmentArrayList;
    private List<WarmUpItem> warmUpList;
    private List<StretchItem> stretchList;
    public static String[] lower_body_parts = {"Quads", "Glutes", "Legs", "Adductor", "Abductor", "Hamstrings", "Calves"};
    public static String[] upper_body_parts = {"Abs", "Back", "Shoulders", "Chest", "Triceps", "Obliques", "Arms", "Biceps", "Lats", "Forearms"};

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
        location = findViewById(R.id.chooseLocationTextView);
        workoutButton = (Button) findViewById(R.id.workoutButton);

        loadDatabases();
        setLocationText();
        setButtonsOnClickListeners();
        setChooseLocationOnClickListener();
        checkFirstSignIn();
        continueWorkout();
    }

    public void loadDatabases() {
        //loadExercises();
        loadLocations();
        loadWarmUpDatabase();
        getWarmUpList();
        loadStretchDatabase();
        getStretchList();
    }

    public void setButtonsOnClickListeners() {
        workoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                location_id = sharedPreferences.getInt("Location", 0);
                if (location_id > 0) {
                    Intent exercisesIntent = new Intent(MainActivity.this, ExerciseListActivity.class);
                    exercisesIntent.putExtra("list", chosenExercises);
                    exercisesIntent.putExtra("equipment", equipmentArrayList);
                    startActivity(exercisesIntent);
                } else {
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
                warmupIntent.putExtra("list", (ArrayList<WarmUpItem>) warmUpList);
                startActivity(warmupIntent);
            }
        });

        Button stretchButton = (Button) findViewById(R.id.stretchButton);
        stretchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stretchIntent = new Intent(MainActivity.this, StretchActivity.class);
                stretchIntent.putExtra("list", (ArrayList<StretchItem>) stretchList);
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

    public void checkFirstSignIn() {
        SharedPreferences first = getSharedPreferences(FIRST, MODE_PRIVATE);
        boolean isFirst = first.getBoolean("First", true);
        if (isFirst) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("Completed workout", true);
            editor.apply();
            Intent userIntent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(userIntent);
        } else {
            loadExercises();
        }
    }

    @Override
    public void onLocationItemChosen() {
        String locationName = sharedPreferences.getString("Location Name", getString(R.string.choose_location));
        location.setText(locationName);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        workoutButton.setEnabled(false);
        workoutButton.setBackground(getResources().getDrawable(R.drawable.button_round_disabled));

        new AsyncTask<Void, Void, List<ExerciseItem>>() {

            @Override
            protected List<ExerciseItem> doInBackground(Void... voids) {
                loadEquipments();
                getAvailableEquipment();
                getExercisesForLocation();
                //makeWorkoutFromSelectedExercises();
                return exercisesForLocation;
            }
        }.execute();
    }

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


    public void getAvailableEquipment() {
        new AsyncTask<Void, Void, List<EquipmentItem>>() {

            @Override
            protected List<EquipmentItem> doInBackground(Void... voids) {
                location_id = sharedPreferences.getInt("Location", 1);
                LocationItem location = locationdatabase.locationItemDao().getLocationWithID(location_id);
                ArrayList<EquipmentItem> equipments = location.location_equipmentItems;
                equipment_ids = new ArrayList<>();
                for (EquipmentItem item : equipments) {
                    equipment_ids.add(item.equipment_id);
                }
                if (equipment_ids.contains(5) && !equipment_ids.contains(4)) equipment_ids.add(4);
                if (equipment_ids.contains(7) && !equipment_ids.contains(6)) equipment_ids.add(6);
                if (!equipment_ids.contains(1)) equipment_ids.add(1);
                //getExercisesForLocation();
                return equipments;
            }


            //@Override
            //protected void onPostExecute(List<EquipmentItem> equipmentItems) {
            //    getExercisesForLocation();
            //Toast toast = Toast.makeText(getApplication().getApplicationContext(), "kesz 0", Toast.LENGTH_LONG);
            //toast.show();
            //}


        }.execute();
    }

    public void getExercisesForLocation() {
        new AsyncTask<Void, Void, List<ExerciseItem>>() {
            @Override
            protected List<ExerciseItem> doInBackground(Void... voids) {
                exercisesForLocation = new ArrayList<>();
                for (int i = 0; i < equipment_ids.size(); i++) {
                    for (int j = 0; j < equipment_ids.size(); j++) {
                        List<ExerciseItem> list = database.exerciseItemDao().getExercisesWithEquipments(equipment_ids.get(i), equipment_ids.get(j));
                        exercisesForLocation.addAll(list);
                    }

                }
                makeWorkoutFromSelectedExercises();
                return exercisesForLocation;
            }

            @Override
            protected void onPostExecute(List<ExerciseItem> exerciseItemList) {
                progressBar.setVisibility(View.GONE);
                workoutButton.setEnabled(true);
                workoutButton.setBackground(getResources().getDrawable(R.drawable.button_round));


            }
        }.execute();
    }

    public void makeWorkoutFromSelectedExercises() {
        chosenExercises = new ArrayList<>();
        String workout_type = sharedPreferences.getString("Workout type", "Lower body");
        ArrayList<String> body_parts = new ArrayList<>();
        switch (workout_type) {
            case "Upper body":
                while (body_parts.size() < 10) {
                    body_parts.add(upper_body_parts[getRandomNumber(upper_body_parts.length)]);
                }
                selectExercises(body_parts, 10);
                break;

            case "Lower body":
                while (body_parts.size() < 10) {
                    body_parts.add(lower_body_parts[getRandomNumber(lower_body_parts.length)]);
                }
                selectExercises(body_parts, 10);
                break;

            case "Cardio 1":
            case "Cardio 2":
                while (body_parts.size() < 4) {
                    body_parts.add(upper_body_parts[getRandomNumber(upper_body_parts.length)]);
                }
                while (body_parts.size() < 8) {
                    body_parts.add(lower_body_parts[getRandomNumber(lower_body_parts.length)]);
                }
                selectExercises(body_parts, 8);
                selectCardio();
                break;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Number of exercises", chosenExercises.size());
        for (int j = 0; j < chosenExercises.size(); j++) {
            editor.putLong("Exercise " + j, chosenExercises.get(j).exercise_id);
        }
        editor.apply();
    }

    public int getRandomNumber(int max) {
        Random r = new Random();
        return r.nextInt(max);
    }

    public void selectExercises(ArrayList<String> body_parts, int limit) {
        for (int i = 0; i < limit; i++) {
            ArrayList<ExerciseItem> exerciseItems = new ArrayList<>();
            String body_part = body_parts.get(i);
            for (int j = 0; j < exercisesForLocation.size(); j++) {
                for (int k = 0; k < exercisesForLocation.get(j).exercise_muscles.size(); k++) {
                    String s = exercisesForLocation.get(j).exercise_muscles.get(k);
                    if (exercisesForLocation.get(j).exercise_muscles.get(k).contains(body_part))
                        exerciseItems.add(exercisesForLocation.get(j));
                }
            }
            if (exerciseItems.size() > 0) {
                int random = getRandomNumber(exerciseItems.size());
                ExerciseItem exercise = exerciseItems.get(random);
                if (!chosenExercises.contains(exercise)) {
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
                } else i--;
            } else {
                if (sharedPreferences.getString("Workout type", "").equals("Upper body")) {
                    body_parts.set(i, upper_body_parts[getRandomNumber(upper_body_parts.length)]);
                    i--;
                } else {
                    body_parts.set(i, lower_body_parts[getRandomNumber(lower_body_parts.length)]);
                    i--;
                }
            }
        }
    }

    public void selectCardio() {
        ArrayList<ExerciseItem> cardio = new ArrayList<>();
        for (int i = exercisesForLocation.size() - 1; i >= 0; i--) {
            if (exercisesForLocation.get(i).exercise_muscles.get(0).contains("Cardiovascular System"))
                cardio.add(exercisesForLocation.get(i));
        }
        int random = getRandomNumber(cardio.size());
        chosenExercises.add(cardio.get(random));
    }


    public void continueWorkout() {
        boolean completed = sharedPreferences.getBoolean("Completed workout", false);
        if (!completed) {
            new AsyncTask<Void, Void, List<ExerciseItem>>() {

                @Override
                protected List<ExerciseItem> doInBackground(Void... voids) {
                    /*int numberOfExercises = sharedPreferences.getInt("Number of exercises", 6);
                    for (int i = 0; i < numberOfExercises; i++) {
                        ExerciseItem item = database.exerciseItemDao().getExerciseWithID(sharedPreferences.getLong("Exercise " + i, 1));
                        chosenExercises.add(item);
                    }*/
                    Intent exercisesIntent = new Intent(MainActivity.this, ExerciseInfoActivity.class);
                    exercisesIntent.putExtra("exercises", chosenExercises);
                    exercisesIntent.putExtra("equipment", equipmentArrayList);
                    startActivity(exercisesIntent);
                    return chosenExercises;
                }

                /*
                @Override
                protected void onPostExecute(List<ExerciseItem> exerciseItems) {
                    Intent exercisesIntent = new Intent(MainActivity.this, ExerciseInfoActivity.class);
                    exercisesIntent.putExtra("exercises", chosenExercises);
                    exercisesIntent.putExtra("equipment", equipmentArrayList);
                    startActivity(exercisesIntent);
                }
                */

            }.execute();

        }
    }

    private void loadEquipments() {
        equipmentdatabase = Room.databaseBuilder(
                getApplicationContext(),
                EquipmentListDatabase.class,
                "equipments"
        ).build();

        new AsyncTask<Void, Void, List<EquipmentItem>>() {

            @Override
            protected List<EquipmentItem> doInBackground(Void... voids) {
                List<EquipmentItem> equipmentItemList = equipmentdatabase.equipmentItemDao().getAll();
                equipmentArrayList.addAll(equipmentItemList);
                //getAvailableEquipment();
                return equipmentItemList;
            }

        }.execute();
    }

    public void loadExercises() {
        /*
        RoomDatabase.Callback rdc = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                final ArrayList<ExerciseItem> list = fillExerciseList();

                new AsyncTask<Void, Void, List<ExerciseItem>>() {

                    @Override
                    protected List<ExerciseItem> doInBackground(Void... voids) {
                        for (int i = 0; i < list.size(); i++) {
                            database.exerciseItemDao().insert(list.get(i));
                        }
                        return list;
                    }
                }.execute();

            }
        };
        */


        database = Room.databaseBuilder(
                getApplicationContext(),
                ExerciseListDatabase.class,
                "exercises"
        )
                //.addCallback(rdc)
                .build();

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
                for (int i = 0; i < muscles.length; i++) {
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

    public void loadLocations() {
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
    }

    private void loadWarmUpDatabase() {
        RoomDatabase.Callback rdc = new RoomDatabase.Callback() {
            public void onCreate(SupportSQLiteDatabase db) {
                final ArrayList<WarmUpItem> list = fillWarmUpList();

                new AsyncTask<Void, Void, List<WarmUpItem>>() {

                    @Override
                    protected List<WarmUpItem> doInBackground(Void... voids) {
                        for (int i = 0; i < list.size(); i++) {
                            warmupdatabase.warmUpItemDao().insert(list.get(i));
                        }
                        return list;
                    }

                }.execute();
            }
        };

        warmupdatabase = Room.databaseBuilder(
                getApplicationContext(),
                WarmUpListDatabase.class,
                "warm up"
        ).addCallback(rdc).build();
    }

    public ArrayList<WarmUpItem> fillWarmUpList() {
        Resources resources = getResources();
        String str;
        ArrayList<WarmUpItem> warmupItems = new ArrayList<>();

        int resID = resources.getIdentifier("hu.bme.aut.fitnessapp:raw/" + "warmup", null, null);
        InputStream is = resources.openRawResource(resID);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            boolean upper = true;
            boolean lower = true;
            while ((str = br.readLine()) != null) {

                if (str.equals("//lower")) {
                    upper = false;
                    lower = true;
                } else if (str.equals("//upper")) {
                    upper = true;
                    lower = false;
                } else {
                    WarmUpItem newItem = new WarmUpItem();
                    newItem.warmup_name = str;
                    newItem.warmup_lower = lower;
                    newItem.warmup_upper = upper;
                    warmupItems.add(newItem);
                }
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return warmupItems;
    }

    public void getWarmUpList() {
        new AsyncTask<Void, Void, List<WarmUpItem>>() {

            @Override
            protected List<WarmUpItem> doInBackground(Void... voids) {
                warmUpList = warmupdatabase.warmUpItemDao().getAll();
                return warmUpList;
            }
        }.execute();


    }

    private void loadStretchDatabase() {
        RoomDatabase.Callback rdc = new RoomDatabase.Callback() {
            public void onCreate(SupportSQLiteDatabase db) {
                final ArrayList<StretchItem> list = fillStretchList();

                new AsyncTask<Void, Void, List<StretchItem>>() {

                    @Override
                    protected List<StretchItem> doInBackground(Void... voids) {
                        for (int i = 0; i < list.size(); i++) {
                            stretchdatabase.stretchItemDao().insert(list.get(i));
                        }
                        return list;
                    }

                }.execute();
            }
        };

        stretchdatabase = Room.databaseBuilder(
                getApplicationContext(),
                StretchListDatabase.class,
                "stretch"
        ).addCallback(rdc).build();
    }

    public ArrayList<StretchItem> fillStretchList() {
        Resources resources = getResources();
        String str;
        ArrayList<StretchItem> stretchItems = new ArrayList<>();

        int resID = resources.getIdentifier("hu.bme.aut.fitnessapp:raw/" + "stretch", null, null);
        InputStream is = resources.openRawResource(resID);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((str = br.readLine()) != null) {

                    StretchItem newItem = new StretchItem();
                    newItem.stretch_name = str;
                    stretchItems.add(newItem);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stretchItems;
    }

    public void getStretchList() {
        new AsyncTask<Void, Void, List<StretchItem>>() {

            @Override
            protected List<StretchItem> doInBackground(Void... voids) {
                stretchList = stretchdatabase.stretchItemDao().getAll();
                return stretchList;
            }
        }.execute();


    }
}