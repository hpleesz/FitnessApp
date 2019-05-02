package hu.bme.aut.fitnessapp;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.data.equipment.EquipmentItem;
import hu.bme.aut.fitnessapp.data.exercise.ExerciseItem;
import hu.bme.aut.fitnessapp.data.stretch.StretchItem;
import hu.bme.aut.fitnessapp.data.warmup.WarmUpItem;
import hu.bme.aut.fitnessapp.data.warmup.WarmUpListDatabase;
import hu.bme.aut.fitnessapp.fragments.ExerciseCompletedDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewLocationItemDialogFragment;

public class StretchActivity extends NavigationActivity {

    private ArrayList<StretchItem> items;
    private VideoView videoView;
    private TextView titleTextView;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_warm_up, null, false);
        mDrawerLayout.addView(contentView, 0);
        navigationView.getMenu().getItem(0).setChecked(true);

        Intent i = getIntent();
        items = (ArrayList<StretchItem>) i.getSerializableExtra("list");

        setLayoutElements();
        setExercise();
        setFloatingActionButtons();

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

        sharedPreferences = getSharedPreferences(MainActivity.WORKOUT, MODE_PRIVATE);
    }

    public void setFloatingActionButtons() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idx = sharedPreferences.getInt("Stretch number", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (items.size() > idx + 1) {
                    editor.putInt("Stretch number", idx + 1);
                    editor.apply();
                    setExercise();
                } else {
                    editor.putInt("Stretch number", 0);
                    editor.apply();
                    Intent mainIntent = new Intent(StretchActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            }
        });

        fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_forward_white));

        FloatingActionButton fabLeft = (FloatingActionButton) findViewById(R.id.fabLeft);
        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int idx = sharedPreferences.getInt("Stretch number", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (idx > 0) {
                    editor.putInt("Stretch number", idx - 1);
                    editor.apply();
                    setExercise();
                } else {
                    Intent mainIntent = new Intent(StretchActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            }
        });
        fabLeft.setImageDrawable(getDrawable(R.drawable.ic_arrow_back_white));
    }

    public void setExercise() {
        int idx = sharedPreferences.getInt("Stretch number", 0);
        StretchItem item = items.get(idx);
        titleTextView.setText(item.stretch_name);

        String name = item.stretch_name;
        setVideo(name);

    }

    public void setVideo(String name) {
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

}
