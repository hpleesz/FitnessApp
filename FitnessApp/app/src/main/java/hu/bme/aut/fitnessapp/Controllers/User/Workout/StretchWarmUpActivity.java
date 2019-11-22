package hu.bme.aut.fitnessapp.Controllers.User.Workout;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.Models.UserModels.WorkoutModels.StretchWarmUpModel;
import hu.bme.aut.fitnessapp.Models.UserModels.WorkoutModels.VideoModel;
import hu.bme.aut.fitnessapp.R;

public class StretchWarmUpActivity extends InternetCheckActivity implements StretchWarmUpModel.ExercisesEndListener, StretchWarmUpModel.ExerciseListener, StretchWarmUpModel.ExerciseListLoadedListener, VideoModel.VideoLoadedListener {

    private boolean lower = true;
    private ArrayList<String> items;
    private VideoView videoView;
    private TextView titleTextView;
    private int idx = 0;

    private StretchWarmUpModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warm_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
    }

    public void setFloatingActionButtons() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.navigateRight();
            }
        });

        fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_forward_white));

        FloatingActionButton fabLeft = (FloatingActionButton) findViewById(R.id.fabLeft);
        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.navigateLeft();
            }
        });
        fabLeft.setImageDrawable(getDrawable(R.drawable.ic_arrow_back_white));
    }

    public void setExercise() {
        String name = model.getExerciseItem();
        titleTextView.setText(name);

        model.setVideo(name);

    }

    public void startVideo(Uri uri) {
        videoView.setVideoURI(uri);
        videoView.start();

    }

    public void returnToMain() {
        Intent mainIntent = new Intent(StretchWarmUpActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void setModel(StretchWarmUpModel model) {
        this.model = model;
        this.model.loadItems();
    }

    @Override
    public void onExerciseLoaded() {
        setExercise();
    }

    @Override
    public void onVideoReady(Uri uri) {
        startVideo(uri);
    }

    @Override
    public void onExercisesFinished() {
        returnToMain();
    }

    @Override
    public void onExerciseListLoaded() {
        setLayoutElements();
        setExercise();
        setFloatingActionButtons();
    }
}
