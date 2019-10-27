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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.data.equipment.EquipmentItem;
import hu.bme.aut.fitnessapp.data.exercise.ExerciseItem;
import hu.bme.aut.fitnessapp.data.warmup.WarmUpItem;
import hu.bme.aut.fitnessapp.data.warmup.WarmUpListDatabase;
import hu.bme.aut.fitnessapp.fragments.ExerciseCompletedDialogFragment;
import hu.bme.aut.fitnessapp.fragments.NewLocationItemDialogFragment;

public class WarmUpActivity extends NavigationActivity {

    private ArrayList<WarmUpItem> warmUpItems;
    private ArrayList<String> items;
    private VideoView videoView;
    private TextView titleTextView;
    private SharedPreferences sharedPreferences;
    private String type;
    private boolean lower = true;
    private int idx = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_warm_up, null, false);
        mDrawerLayout.addView(contentView, 0);
        navigationView.getMenu().getItem(0).setChecked(true);

        Intent i = getIntent();
        type = (String) i.getSerializableExtra("type");
        //items = (ArrayList<WarmUpItem>) i.getSerializableExtra("list");

        warmUpItems = new ArrayList<>();
        loadItems();
        /*
        setLayoutElements();
        chooseExercises();
        setExercise();
        setFloatingActionButtons();

         */

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
                //int idx = sharedPreferences.getInt("Warm up number", 0);
                //SharedPreferences.Editor editor = sharedPreferences.edit();
                if (items.size() > idx + 1) {
                    //editor.putInt("Warm up number", idx + 1);
                    //editor.apply();
                    idx = idx + 1;
                    setExercise();
                } else {
                    //editor.putInt("Warm up number", 0);
                    //editor.apply();
                    Intent warmupIntent = new Intent(WarmUpActivity.this, MainActivity.class);
                    startActivity(warmupIntent);
                }
            }
        });

        fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_forward_white));

        FloatingActionButton fabLeft = (FloatingActionButton) findViewById(R.id.fabLeft);
        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int idx = sharedPreferences.getInt("Warm up number", 0);
                //SharedPreferences.Editor editor = sharedPreferences.edit();
                if (idx > 0) {
                    //editor.putInt("Warm up number", idx - 1);
                    //editor.apply();
                    idx = idx -1;
                    setExercise();
                } else {
                    Intent mainIntent = new Intent(WarmUpActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            }
        });
        fabLeft.setImageDrawable(getDrawable(R.drawable.ic_arrow_back_white));
    }

    public void setExercise() {
        //int idx = sharedPreferences.getInt("Warm up number", 0);
        String item = items.get(idx);
        titleTextView.setText(item);

        setVideo(item);
        //this.getActionBar().setTitle(getString(R.string.exercise_number) + idx);

    }

    public void setVideo(String name) {
        name = name.toLowerCase();
        name = name.replace(" ", "_");
        name = name.replace(",", "");
        name = name.replace("-", "_");
        name = name.replace("_/_", "_");
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        StorageReference pathReference = storageRef.child(name + ".mp4");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                videoView.setVideoURI(uri);

            }
        });

        videoView.start();
    }
    /*
        public void chooseExercises() {
            //String type = sharedPreferences.getString("Workout type", "Lower body");
            switch (type) {
                case "Cardio 1":
                case "Cardio 2":
                case "Lower body":
                    getLowerBodyWarmUp();
                    break;
                case "Upper body":
                    getUpperBodyWarmUp();                   break;
            }
        }
    /*
        public void getLowerBodyWarmUp() {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).warmup_lower)
                    warmUpItems.add(items.get(i));
            }
        }

        public void getUpperBodyWarmUp() {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).warmup_upper)
                    warmUpItems.add(items.get(i));
            }
        }

     */
    public void loadItems() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        switch (type) {
            case "Cardio 1":
            case "Cardio 2":
            case "Lower body":
                //getLowerBodyWarmUp();
                break;
            case "Upper body":
                lower = false;
                //getUpperBodyWarmUp();
                break;
        }

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items = new ArrayList<>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String item = dataSnapshot1.child("Name").getValue(String.class);
                    if(lower) {
                        if(dataSnapshot1.child("Lower").getValue(Boolean.class)) {
                            items.add(item);
                        }
                    }
                    else {
                        if(dataSnapshot1.child("Upper").getValue(Boolean.class)) {
                            items.add(item);
                        }
                    }
                }
                setLayoutElements();
                //chooseExercises();
                setExercise();
                setFloatingActionButtons();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.child("Warmup").addValueEventListener(eventListener);
    }


}
