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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import hu.bme.aut.fitnessapp.models.Equipment;
import hu.bme.aut.fitnessapp.models.Exercise;

public class ExerciseListActivity extends NavigationActivity {

    private ArrayList<Exercise> exerciseItems;
    private ArrayList<Equipment> equipmentItemList;

    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_exercise_list, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(0).setChecked(true);

        PACKAGE_NAME = getApplicationContext().getPackageName();

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
                //Workout started -> not completed
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
