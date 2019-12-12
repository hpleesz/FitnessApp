package hu.bme.aut.fitnessapp.controllers.user.workout;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.models.user_models.workout_models.ExerciseInfoModel;
import hu.bme.aut.fitnessapp.models.user_models.workout_models.VideoModel;
import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.controllers.user.NavigationActivity;
import hu.bme.aut.fitnessapp.entities.Exercise;

public class ExerciseInfoActivity extends NavigationActivity implements ExerciseCompletedDialogFragment.ExerciseCompletedListener, ExerciseInfoModel.LayoutReadyListener, ExerciseInfoModel.ExercisesEndListener, ExerciseInfoModel.DataReadyListener, ExerciseInfoModel.TimerListener, VideoModel.VideoLoadedListener {

    private VideoView videoView;
    private TextView titleTextView;
    private TextView detailsTextView;
    private TextView equipmentTextView;
    private TextView timerTextView;
    private static CountDownTimer countdown;


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
        exerciseInfoModel = new ExerciseInfoModel(this, exerciseItems);
        exerciseInfoModel.initFirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exerciseInfoModel.navigateRight();
            }
        });
        fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_forward_white));

        FloatingActionButton fabLeft = findViewById(R.id.fabLeft);
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

    @Override
    public void onStop() {
        super.onStop();
        exerciseInfoModel.removeListeners();
    }
}
