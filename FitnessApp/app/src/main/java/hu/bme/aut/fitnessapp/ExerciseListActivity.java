package hu.bme.aut.fitnessapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.data.equipment.EquipmentItem;
import hu.bme.aut.fitnessapp.data.equipment.EquipmentListDatabase;
import hu.bme.aut.fitnessapp.data.exercise.ExerciseAdapter;
import hu.bme.aut.fitnessapp.data.exercise.ExerciseItem;
import hu.bme.aut.fitnessapp.data.exercise.ExerciseListDatabase;
import hu.bme.aut.fitnessapp.data.location.LocationAdapter;
import hu.bme.aut.fitnessapp.data.location.LocationListDatabase;
import hu.bme.aut.fitnessapp.fragments.ChooseLocationItemDialogFragment;

public class ExerciseListActivity extends NavigationActivity {

    private ExerciseAdapter adapter;
    private ArrayList<ExerciseItem> exerciseItems;
    private List<EquipmentItem> equipmentItemList;
    private EquipmentListDatabase database;
    private RecyclerView recyclerView;

    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_exercise_list, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(0).setChecked(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_white));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        database = Room.databaseBuilder(
                getApplicationContext(),
                EquipmentListDatabase.class,
                "equipments"
        ).build();
        //loadEquipments();

        Intent i = getIntent();
        exerciseItems = (ArrayList<ExerciseItem>) i.getSerializableExtra("list");
        equipmentItemList = (ArrayList<EquipmentItem>) i.getSerializableExtra("equipment");

        PACKAGE_NAME = getApplicationContext().getPackageName();
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.ExerciseRecyclerView);
        adapter = new ExerciseAdapter(exerciseItems, equipmentItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

    }

    private void loadEquipments() {
        new AsyncTask<Void, Void, List<EquipmentItem>>() {

            @Override
            protected List<EquipmentItem> doInBackground(Void... voids) {
                equipmentItemList = database.equipmentItemDao().getAll();
                return equipmentItemList;
            }
        }.execute();
    }


}
