package hu.bme.aut.fitnessapp.User.Workout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.R;
import hu.bme.aut.fitnessapp.User.NavigationActivity;
import hu.bme.aut.fitnessapp.Models.Equipment;
import hu.bme.aut.fitnessapp.Models.Exercise;
import hu.bme.aut.fitnessapp.Models.User;
import hu.bme.aut.fitnessapp.Models.WorkoutDetails;

public class ExerciseInfoActivity extends NavigationActivity implements ExerciseCompletedDialogFragment.ExerciseCompletedListener {

    private ArrayList<Exercise> exerciseItems;
    private ArrayList<Equipment> equipmentItems;
    private VideoView videoView;
    private TextView titleTextView;
    private TextView detailsTextView;
    private TextView equipmentTextView;
    private TextView timerTextView;
    private SharedPreferences sharedPreferences;
    private static CountDownTimer countdown;

    private String userId;
    private DatabaseReference databaseReference;
    private WorkoutDetails workoutDetails;
    private User user;

    public static String EXERCISE_NUMBER = "Exercise Number";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_exercise_info, null, false);
        mDrawerLayout.addView(contentView, 0);

        navigationView.getMenu().getItem(0).setChecked(true);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences(EXERCISE_NUMBER, MODE_PRIVATE);

        getExtraFromIntent();
        loadWorkoutDetails();
        loadUserDetails();

        if (countdown != null)
            countdown.cancel();
    }

    public void getExtraFromIntent() {
        Intent i = getIntent();
        exerciseItems = (ArrayList<Exercise>) i.getSerializableExtra("exercises");
        equipmentItems = (ArrayList<Equipment>) i.getSerializableExtra("equipment");
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

                int idx = sharedPreferences.getInt(userId, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (exerciseItems.size() > idx + 1) {
                    editor.putInt(userId, idx + 1);
                    editor.apply();
                    setExercise();

                } else {
                    new ExerciseCompletedDialogFragment().show(getSupportFragmentManager(), ExerciseCompletedDialogFragment.TAG);

                }
            }
        });
        fab.setImageDrawable(getDrawable(R.drawable.ic_arrow_forward_white));

        FloatingActionButton fabLeft = (FloatingActionButton) findViewById(R.id.fabLeft);
        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int idx = sharedPreferences.getInt(userId, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (idx > 0) {
                    editor.putInt(userId, idx - 1);
                    editor.apply();
                    setExercise();
                }
            }
        });
        fabLeft.setImageDrawable(getDrawable(R.drawable.ic_arrow_back_white));
    }


    public void setExercise() {
        int idx = sharedPreferences.getInt(userId, 0);
        if (countdown != null)
            countdown.cancel();
        if (idx == exerciseItems.size() - 1)
            setTimer(20 * 60 * 1000);
        else
            setTimer(60 * 1000);

        Exercise item = exerciseItems.get(idx);
        titleTextView.setText(item.name);


        String name = item.name;
        setVideo(name);

        setEquipments(item);
        setDetailsAndTimer(item);
    }

    public void setVideo(String name) {
        name = name.toLowerCase();
        name = name.replace(" ", "_");
        name = name.replace(",", "");
        name = name.replace("-", "_");
        name = name.replace("_/_", "_");
        //int id = getApplicationContext().getResources().getIdentifier(name, "raw", getPackageName());

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

    public void setEquipments(Exercise item) {
        String equipments = "";


        for (int i = 0; i < equipmentItems.size(); i++) {
            for (int j = 0; j < equipmentItems.size(); j++) {
                if (item.equipment1 == equipmentItems.get(i).id && item.equipment2 == equipmentItems.get(j).id)
                    if (i == 0) equipments = equipmentItems.get(j).name;
                    else if (j == 0) equipments = equipmentItems.get(i).name;
                    else
                        equipments = equipmentItems.get(i).name + ", " + equipmentItems.get(j).name;
            }
        }
        equipmentTextView.setText(equipments);
    }

    public void setDetailsAndTimer(Exercise item) {
        String details = "";
        switch (workoutDetails.type) {
            case "Lower body":
            case "Upper body":
                if (item.rep_time == 0) {
                    if (itemUsesWeight(item)) {
                        details = details + getString(R.string.weight_max) + ", ";
                        details = details + getString(R.string.reps_weight);
                    } else {
                        details = details + getString(R.string.reps_no_weight);
                    }
                    detailsTextView.setText(details);
                    timerTextView.setText("");
                    if (countdown != null)
                        countdown.cancel();
                } else {
                    detailsTextView.setText(R.string.timer_short);
                    timerTextView.setText(R.string.timer_start);
                }
                break;
            case "Cardio 1":
            case "Cardio 2":
                if (item.rep_time == 0) {
                    if (itemUsesWeight(item)) {
                        details = details + getString(R.string.weight_70) + ", ";
                        details = details + getString(R.string.reps_cardio);
                    } else {
                        details = details + getString(R.string.reps_no_weight);
                    }
                    detailsTextView.setText(details);
                    timerTextView.setText("");
                    if (countdown != null)
                        countdown.cancel();

                } else {
                    if (item.id == exerciseItems.get(exerciseItems.size() - 1).id)
                        detailsTextView.setText(R.string.timer_long);
                    else
                        detailsTextView.setText(R.string.timer_short);

                    timerTextView.setText(R.string.timer_start);
                }
                break;
        }

    }

    public void setTimer(long millis) {
        countdown = new CountDownTimer(millis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String minutesText = Long.toString(minutes);
                String secondsText = Long.toString(seconds);
                if (minutes < 10) minutesText = "0" + minutesText;
                if (seconds < 10) secondsText = "0" + secondsText;
                timerTextView.setText(minutesText + ":" + secondsText);
            }

            @Override
            public void onFinish() {
                timerTextView.setText(R.string.timer_done);
            }

        };
    }

    public boolean itemUsesWeight(Exercise item) {
        if ((2 < item.equipment1 && item.equipment1 < 8) || item.equipment1 == 20 || item.equipment1 == 22 || item.equipment1 == 25)
            return true;
        if ((2 < item.equipment2 && item.equipment2 < 8) || item.equipment2 == 20 || item.equipment2 == 22 || item.equipment2 == 25)
            return true;
        return false;

    }


    @Override
    public void onExerciseCompleted() {
        databaseReference.child("Workout_Details").child(userId).child("In_Progress").setValue(false);
        databaseReference.child("Workout_Details").child(userId).child("Exercises").removeValue();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(userId, 0);
        editor.apply();
        setWorkoutType();
        Intent intent = new Intent(ExerciseInfoActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void setWorkoutType() {
        boolean muscle = user.gain_muscle;
        boolean weight = user.lose_weight;
        if (muscle) {
            if (weight) {
                switch (workoutDetails.type) {
                    case "Lower body":
                        databaseReference.child("Workout_Details").child(userId).child("Type").setValue("Cardio 1");
                        break;
                    case "Cardio 1":
                        databaseReference.child("Workout_Details").child(userId).child("Type").setValue("Upper body");
                        break;
                    case "Upper body":
                        databaseReference.child("Workout_Details").child(userId).child("Type").setValue("Cardio 2");
                        break;
                    case "Cardio 2":
                        databaseReference.child("Workout_Details").child(userId).child("Type").setValue("Lower body");
                }
            } else {
                switch (workoutDetails.type) {
                    case "Lower body":
                        databaseReference.child("Workout_Details").child(userId).child("Type").setValue("Upper body");
                        break;
                    case "Upper body":
                        databaseReference.child("Workout_Details").child(userId).child("Type").setValue("Lower body");
                        break;
                }
            }
        } else {
            databaseReference.child("Workout_Details").child(userId).child("Type").setValue("Cardio 1");

        }
    }

    public void loadWorkoutDetails() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type = dataSnapshot.child("Type").getValue(String.class);
                boolean in_progress = dataSnapshot.child("In_Progress").getValue(Boolean.class);

                ArrayList<Integer> exercises = new ArrayList<>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.child("Exercises").getChildren()) {
                    int exercise_id = dataSnapshot1.getValue(Integer.class);
                    exercises.add(exercise_id);
                }
                workoutDetails = new WorkoutDetails(type, exercises, in_progress);

                setLayoutElements();
                setFloatingActionButtons();
                setExercise();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Workout_Details").child(userId).addValueEventListener(eventListener);

    }

    public void loadUserDetails() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                user = dataSnapshot.getValue(User.class);
                // [START_EXCLUDE]
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.child("Users").child(userId).addValueEventListener(eventListener);
    }
}
