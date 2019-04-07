package hu.bme.aut.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.data.equipment.EquipmentItem;
import hu.bme.aut.fitnessapp.data.exercise.ExerciseItem;
import hu.bme.aut.fitnessapp.fragments.ExerciseCompletedDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewLocationItemDialogFragment;

public class ExerciseInfoActivity extends NavigationActivity implements ExerciseCompletedDialogFragment.ExerciseCompletedListener {

    private ArrayList<ExerciseItem> exerciseItems;
    private ArrayList<EquipmentItem> equipmentItems;
    private VideoView videoView;
    private TextView titleTextView;
    private TextView detailsTextView;
    private TextView equipmentTextView;
    private TextView timerTextView;
    private SharedPreferences sharedPreferences;
    private CountDownTimer countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_exercise_info, null, false);
        mDrawerLayout.addView(contentView, 0);

        Intent i = getIntent();
        exerciseItems = (ArrayList<ExerciseItem>) i.getSerializableExtra("exercises");
        equipmentItems = (ArrayList<EquipmentItem>) i.getSerializableExtra("equipment");

        setLayoutElements();
        setFloatingActionButtons();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Number of exercises", exerciseItems.size());
        for(int j = 0; j < exerciseItems.size(); j++){
            editor.putLong("Exercise " + j, exerciseItems.get(j).exercise_id);
        }
        editor.apply();

        setTimer();
        setExercise();

    }

    public void setLayoutElements() {
        videoView = findViewById(R.id.videoView);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        titleTextView = findViewById(R.id.ExerciseTitleTextView);
        detailsTextView = findViewById(R.id.ExerciseDetailsTextView);
        equipmentTextView = findViewById(R.id.ExerciseEquipmentTextView);
        timerTextView = findViewById(R.id.ExerciseTimerTextView);
        timerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdown.start();
            }
        });
        sharedPreferences = getSharedPreferences(MainActivity.WORKOUT, MODE_PRIVATE);
    }

    public void setFloatingActionButtons() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idx = sharedPreferences.getInt("Exercise number", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(exerciseItems.size() > idx+1) {
                    editor.putInt("Exercise number", idx+1);
                    editor.apply();
                }
                else {
                    new ExerciseCompletedDialogFragment().show(getSupportFragmentManager(), ExerciseCompletedDialogFragment.TAG);

                }
                setExercise();
            }
        });
        fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_forward_white));

        FloatingActionButton fabLeft = (FloatingActionButton) findViewById(R.id.fabLeft);
        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idx = sharedPreferences.getInt("Exercise number", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(idx > 0) editor.putInt("Exercise number", idx-1);
                editor.apply();
                setExercise();
            }
        });
        fabLeft.setImageDrawable(getDrawable(R.drawable.ic_arrow_back_white));
    }

    public void setExercise() {
        int idx = sharedPreferences.getInt("Exercise number", 0);
        ExerciseItem item = exerciseItems.get(idx);
        titleTextView.setText(item.exercise_name);

        String name = item.exercise_name;
        setVideo(name);

        setEquipments(item);
        setDetailsAndTimer(item);
    }

    public void setVideo(String name){
        name = name.toLowerCase();
        name = name.replace(" ", "_");
        name = name.replace(",", "");
        name = name.replace("-", "_");
        name = name.replace("_/_", "_");
        int id = getApplicationContext().getResources().getIdentifier(name, "raw", getPackageName());

        String videoPath = "android.resource://" + getPackageName() + "/" + id;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.start();
    }

    public void setEquipments(ExerciseItem item) {
        String equipments = "";


        for(int i = 0; i < equipmentItems.size(); i++){
            for(int j = 0; j < equipmentItems.size(); j++){
                if(item.equipment1 == equipmentItems.get(i).equipment_id && item.equipment2 == equipmentItems.get(j).equipment_id)
                    if(i == 0) equipments = equipmentItems.get(j).equipment_name;
                    else if(j == 0) equipments = equipmentItems.get(i).equipment_name;
                    else equipments = equipmentItems.get(i).equipment_name + ", " + equipmentItems.get(j).equipment_name;
            }
        }
        equipmentTextView.setText(equipments);
    }

    public void setDetailsAndTimer(ExerciseItem item) {
        String type = sharedPreferences.getString("Workout type", "Lower body");
        String details = "";
        switch(type) {
            case "Lower body" :
            case "Upper body" :
                if(item.reps_time == 0){
                    if(itemUsesWeight(item))
                        details = details + "max weight, ";
                    details = details + "3x6 reps";
                    detailsTextView.setText(details);
                    timerTextView.setText("");
                    countdown.cancel();
                }
                else{
                    detailsTextView.setText("30 seconds");
                    timerTextView.setText("Press to start timer");
                    //setTimer();
                }
                break;
            case "Cardio 1" :
            case "Cardio 2" :
                if(item.reps_time == 0){
                    if(itemUsesWeight(item))
                        details = details + "70% weight, ";
                    details = details + "3x15 reps";
                    detailsTextView.setText(details);
                    timerTextView.setText("");
                    countdown.cancel();
                }
                else{
                    detailsTextView.setText("30 seconds");
                    timerTextView.setText("Press to start timer");
                    //setTimer();
                }
                break;
        }

    }
    public void setTimer() {
        countdown = new CountDownTimer(30000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                //make long into date time
                timerTextView.setText("Time left:" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                timerTextView.setText("Done!");
            }
        };
    }

    public boolean itemUsesWeight(ExerciseItem item) {
        if((2 < item.equipment1 && item.equipment1 < 8) || item.equipment1 == 20 || item.equipment1 == 22 || item.equipment1 == 25)
            return true;
        if((2 < item.equipment2 && item.equipment2 < 8) || item.equipment2 == 20 || item.equipment2 == 22 || item.equipment2 == 25)
            return true;
        return false;

    }


    @Override
    public void onExerciseCompleted() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Completed workout", true);
        editor.putInt("Exercise number", 0);
        editor.apply();
        Intent intent = new Intent(ExerciseInfoActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
