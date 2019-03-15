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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private ExerciseAdapter adapter;
    private List<ExerciseItem> itemlist;

    public static final String WORKOUT = "workout settings";
    private SharedPreferences sharedPreferences;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(0).setChecked(true);

        button = (Button) findViewById(R.id.chooseLocationButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChooseLocationItemDialogFragment().show(getSupportFragmentManager(), ChooseLocationItemDialogFragment.TAG);
            }
        });
        sharedPreferences = getSharedPreferences(WORKOUT, MODE_PRIVATE);
        checkFirstSignIn();
        initializeDatabase();
        initRecyclerView();

        initLocationDatabase();
    }

    public void checkFirstSignIn() {
        SharedPreferences first = getSharedPreferences(FIRST, MODE_PRIVATE);
        boolean isFirst = first.getBoolean("First", true);
        if(isFirst) {
            Intent userIntent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(userIntent);
        }
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.ExerciseRecyclerView);
        adapter = new ExerciseAdapter();
        loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<ExerciseItem>>() {

            @Override
            protected List<ExerciseItem> doInBackground(Void... voids) {
                return database.exerciseItemDao().getAll();
            }

            @Override
            protected void onPostExecute(List<ExerciseItem> exerciseItemList) {
                adapter.update(exerciseItemList);
            }
        }.execute();
    }


    public void initializeDatabase() {
        RoomDatabase.Callback rdc = new RoomDatabase.Callback() {
            public void onCreate (SupportSQLiteDatabase db) {
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
        };

        database = Room.databaseBuilder(
                getApplicationContext(),
                ExerciseListDatabase.class,
                "exercises"
        ).addCallback(rdc).build();
    }

    public ArrayList<ExerciseItem> fillExerciseList() {
        Resources resources = getResources();
        String str;
        ArrayList<ExerciseItem> exerciseItems = new ArrayList<>();

        EquipmentListDatabase equipmentdatabase = Room.databaseBuilder(
                getApplicationContext(),
                EquipmentListDatabase.class,
                "equipment"
        ).build();

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
    }

    @Override
    public void onLocationItemChosen() {
        new AsyncTask<Void, Void, List<ExerciseItem>>() {

            @Override
            protected List<ExerciseItem> doInBackground(Void... voids) {
                int id = sharedPreferences.getInt("Location", 1);
                LocationItem location = locationdatabase.locationItemDao().getLocationWithID(id);
                ArrayList<EquipmentItem> equipments = location.location_equipmentItems;
                List<ExerciseItem> exercises = database.exerciseItemDao().getAll();
                ArrayList<ExerciseItem> newexercises = new ArrayList<>();
                for(int i = 0; i < equipments.size(); i++){
                    for(int j = 0; j < equipments.size(); j++){
                        for(int k = 0; k < exercises.size(); k++){
                            if(exercises.get(k).equipment1 == equipments.get(i).equipment_id && exercises.get(k).equipment2 == equipments.get(j).equipment_id)
                                newexercises.add(exercises.get(k));
                        }
                    }
                }
                //if(!newexercises.isEmpty())
                    //exercises.addAll(newexercises);
                return newexercises;
            }

            @Override
            protected void onPostExecute(List<ExerciseItem> exerciseItemList) {
                adapter.update(exerciseItemList);
                //loadItemsInBackground();
            }
        }.execute();

        //adapter.update(exercises);
    }
}
