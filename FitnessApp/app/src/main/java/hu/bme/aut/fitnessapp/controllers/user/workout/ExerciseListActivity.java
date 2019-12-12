package hu.bme.aut.fitnessapp.controllers.user.workout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.models.user_models.workout_models.ExerciseListModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.adapters.ExerciseAdapter;
import hu.bme.aut.fitnessapp.entities.Exercise;

public class ExerciseListActivity extends InternetCheckActivity implements ExerciseListModel.EquipmentLoadedListener{

    private ExerciseListModel exerciseListModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        Toolbar toolbar = findViewById(R.id.toolbar_back);
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
        exerciseListModel = new ExerciseListModel(this, exerciseItems);
    }

    @Override
    protected void onStart() {
        super.onStart();
        exerciseListModel.loadEquipment();
    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_white));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exerciseListModel.startWorkout();
                Log.d("listexercises", Integer.toString(exerciseListModel.getExerciseItems().size()));
                Intent intent = new Intent(ExerciseListActivity.this, ExerciseInfoActivity.class);
                intent.putExtra("exercises", (ArrayList<Exercise>)exerciseListModel.getExerciseItems());
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

    @Override
    public void onStop() {
        super.onStop();
        exerciseListModel.removeListeners();
    }
}
