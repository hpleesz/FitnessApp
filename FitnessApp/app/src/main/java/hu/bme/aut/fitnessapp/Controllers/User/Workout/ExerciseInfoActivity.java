package hu.bme.aut.fitnessapp.Controllers.User.Workout;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
//import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Models.User.Workout.ExerciseInfoModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.Controllers.User.NavigationActivity;
import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Exercise;
import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Entities.WorkoutDetails;

public class ExerciseInfoActivity extends NavigationActivity implements ExerciseCompletedDialogFragment.ExerciseCompletedListener, ExerciseInfoModel.LayoutReadyListener, ExerciseInfoModel.ExercisesEndListener, ExerciseInfoModel.DataReadyListener, ExerciseInfoModel.TimerListener {


    private ArrayList<Exercise> exerciseItems;
    private ArrayList<Equipment> equipmentItems;
    private VideoView videoView;
    private TextView titleTextView;
    private TextView detailsTextView;
    private TextView equipmentTextView;
    private TextView timerTextView;
    private static CountDownTimer countdown;

    private String userId;
    private DatabaseReference databaseReference;

    private WorkoutDetails workoutDetails;


    private User user;


    private ExerciseInfoModel exerciseInfoModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_exercise_info, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(0).setChecked(true);
        getExtraFromIntent();

        checkCounter();
    }

    public void getExtraFromIntent() {
        Intent i = getIntent();
        ArrayList<Exercise> exerciseItems = (ArrayList<Exercise>) i.getSerializableExtra("exercises");
        //ArrayList<Equipment> equipmentItems = (ArrayList<Equipment>) i.getSerializableExtra("equipment");
        exerciseInfoModel = new ExerciseInfoModel(this, exerciseItems);
        exerciseInfoModel.initFirebase();
        exerciseInfoModel.loadWorkoutDetails();
        exerciseInfoModel.loadUserDetails();
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
    }

    public void setFloatingActionButtons() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exerciseInfoModel.navigateRight();
            }
        });
        fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_forward_white));

        FloatingActionButton fabLeft = (FloatingActionButton) findViewById(R.id.fabLeft);
        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exerciseInfoModel.navigateLeft();
            }
        });
        fabLeft.setImageDrawable(getDrawable(R.drawable.ic_arrow_back_white));
    }

    public void showCompletedFragment() {
        new ExerciseCompletedDialogFragment().show(getSupportFragmentManager(), ExerciseCompletedDialogFragment.TAG);
    }

    public void setTitleAndEquipment(String name, String equipments) {
        titleTextView.setText(name);
        equipmentTextView.setText(equipments);
    }

    public void setTimer(long millis) {
        countdown = new CountDownTimer(millis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                String text = exerciseInfoModel.calculateDisplayTime(millisUntilFinished);
                timerTextView.setText(text);
            }

            @Override
            public void onFinish() {
                timerTextView.setText(R.string.timer_done);
            }

        };
    }
    public void checkCounter() {
        if (countdown != null)
            countdown.cancel();
    }

    public void startVideo(Uri uri) {
        videoView.setVideoURI(uri);
        videoView.start();

    }

    public void setDetailsAndTimerText(String details, String timer) {
        detailsTextView.setText(details);
        timerTextView.setText(timer);
    }



    @Override
    public void onExerciseCompleted() {
        exerciseInfoModel.completeExercise();
        Intent intent = new Intent(ExerciseInfoActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void setEquipmentItems(ArrayList<Equipment> equipmentItems) {
        this.equipmentItems = equipmentItems;
    }

    public void setWorkoutDetails(WorkoutDetails workoutDetails) {
        this.workoutDetails = workoutDetails;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onTimerCheck() {
        checkCounter();
    }

    @Override
    public void onSetTimer(long time) {
        setTimer(time);
    }

    @Override
    public void onSetTitleAndEquipment(String title, String equipment) {
        setTitleAndEquipment(title, equipment);
    }

    @Override
    public void onSetDescriptionAndTimer(String desc, String timer) {
        setDetailsAndTimerText(desc, timer);
    }

    @Override
    public void onVideoReady(Uri uri) {
        startVideo(uri);
    }

    @Override
    public void onExercisesFinished() {
        showCompletedFragment();
    }

    @Override
    public void onLayoutReady() {
        setLayoutElements();
        setFloatingActionButtons();
    }
}
