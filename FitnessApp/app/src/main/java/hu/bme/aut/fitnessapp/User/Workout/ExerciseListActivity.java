package hu.bme.aut.fitnessapp.User.Workout;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Adapters.ExerciseAdapter;
import hu.bme.aut.fitnessapp.Models.Equipment;
import hu.bme.aut.fitnessapp.Models.Exercise;

public class ExerciseListActivity extends AppCompatActivity {

    private ArrayList<Exercise> exerciseItems;
    private ArrayList<Equipment> equipmentItemList;


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
        initRecyclerView();
    }

    public void getExtraFromIntent() {
        Intent i = getIntent();
        exerciseItems = (ArrayList<Exercise>) i.getSerializableExtra("list");
        Log.d("exercise", Integer.toString(exerciseItems.size()));
        equipmentItemList = (ArrayList<Equipment>) i.getSerializableExtra("equipment");
        Log.d("equipment", Integer.toString(equipmentItemList.size()));

    }

    public void setFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_white));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Workout_Details").child(userId).child("In_Progress");
                databaseReference.setValue(true);

                Intent intent = new Intent(ExerciseListActivity.this, ExerciseInfoActivity.class);
                intent.putExtra("exercises", exerciseItems);
                intent.putExtra("equipment", equipmentItemList);
                startActivity(intent);
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.ExerciseRecyclerView);
        ExerciseAdapter adapter = new ExerciseAdapter(exerciseItems, equipmentItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

    }



}
