package hu.bme.aut.fitnessapp.Controllers.User.Workout;

import android.content.Intent;
import android.os.Bundle;/*
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
*/
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.Models.UserModels.WorkoutModels.ExerciseListModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Controllers.Adapters.ExerciseAdapter;
import hu.bme.aut.fitnessapp.Entities.Exercise;

public class ExerciseListActivity extends InternetCheckActivity implements ExerciseListModel.EquipmentLoadedListener{

    private ExerciseListModel exerciseListModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getExtraFromIntent();
        setFloatingActionButton();
    }

    public void getExtraFromIntent() {
        Intent i = getIntent();
        ArrayList<Exercise> exerciseItems = (ArrayList<Exercise>) i.getSerializableExtra("list");
        //ArrayList<Equipment> equipmentItemList = (ArrayList<Equipment>) i.getSerializableExtra("equipment");
        exerciseListModel = new ExerciseListModel(this, exerciseItems);
        exerciseListModel.loadEquipment();
    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_white));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exerciseListModel.startWorkout();

                Intent intent = new Intent(ExerciseListActivity.this, ExerciseInfoActivity.class);
                intent.putExtra("exercises", exerciseListModel.getExerciseItems());
                //intent.putExtra("equipment", exerciseListModel.getEquipmentItems());
                startActivity(intent);
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.ExerciseRecyclerView);
        ExerciseAdapter adapter = new ExerciseAdapter(exerciseListModel.getExerciseItems(), exerciseListModel.getEquipmentItems());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onEquipmentLoaded() {
        initRecyclerView();
    }
}
