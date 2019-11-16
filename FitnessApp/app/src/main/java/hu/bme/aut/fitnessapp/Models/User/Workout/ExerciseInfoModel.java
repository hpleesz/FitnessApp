package hu.bme.aut.fitnessapp.Models.User.Workout;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

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

import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Exercise;
import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Entities.WorkoutDetails;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadEquipment;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadUser;
import hu.bme.aut.fitnessapp.Models.DatabaseLoad.LoadWorkoutDetails;
import hu.bme.aut.fitnessapp.R;

import static android.content.Context.MODE_PRIVATE;

public class ExerciseInfoModel implements LoadWorkoutDetails.WorkoutDetailsLoadedListener, LoadUser.UserLoadedListener, LoadEquipment.EquipmentLoadedListener{
    private SharedPreferences sharedPreferences;

    private String userId;
    private DatabaseReference databaseReference;
    private ArrayList<Exercise> exerciseItems;

    public void setEquipmentItems(ArrayList<Equipment> equipmentItems) {
        this.equipmentItems = equipmentItems;
    }

    private ArrayList<Equipment> equipmentItems;

    public void setUser(User user) {
        this.user = user;
    }

    public void setWorkoutDetails(WorkoutDetails workoutDetails) {
        this.workoutDetails = workoutDetails;
    }

    private User user;

    private WorkoutDetails workoutDetails;

    private Context activity;

    public static String EXERCISE_NUMBER = "Exercise Number";

    public interface TimerListener {
        void onTimerCheck();
        void onSetTimer(long time);
    }

    public interface DataReadyListener {
        void onSetTitleAndEquipment(String title, String equipment);
        void onSetDescriptionAndTimer(String desc, String timer);
        void onVideoReady(Uri uri);
    }

    public interface ExercisesEndListener {
        void onExercisesFinished();
    }

    public interface LayoutReadyListener {
        void onLayoutReady();
    }

    private ExerciseInfoModel.TimerListener timerListener;
    private ExerciseInfoModel.DataReadyListener dataReadyListener;
    private ExerciseInfoModel.ExercisesEndListener exercisesEndListener;
    private ExerciseInfoModel.LayoutReadyListener layoutReadyListener;

    public ExerciseInfoModel(Context activity, ArrayList<Exercise> exerciseItems) {
        timerListener = (ExerciseInfoModel.TimerListener)activity;
        dataReadyListener = (ExerciseInfoModel.DataReadyListener)activity;
        exercisesEndListener = (ExerciseInfoModel.ExercisesEndListener)activity;
        layoutReadyListener = (ExerciseInfoModel.LayoutReadyListener)activity;

        this.activity = activity;
        this.exerciseItems = exerciseItems;
        //this.equipmentItems = equipmentItems;

    }

    public void initFirebase() {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = activity.getSharedPreferences(EXERCISE_NUMBER, MODE_PRIVATE);

    }

    public void loadWorkoutDetails() {
        LoadWorkoutDetails loadWorkoutDetails = new LoadWorkoutDetails(this);
        loadWorkoutDetails.loadWorkoutDetails();
        /*
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

                layoutReadyListener.onLayoutReady();
                //((ExerciseInfoActivity)activity).setLayoutElements();
                //((ExerciseInfoActivity)activity).setFloatingActionButtons();
                setExercise();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }

        };
        databaseReference.child("Workout_Details").child(userId).addValueEventListener(eventListener);
         */
    }

    public void loadEquipment() {
        LoadEquipment loadEquipment = new LoadEquipment(this);
        loadEquipment.loadEquipment();
    }


    @Override
    public void onEquipmentLoaded(ArrayList<Equipment> equipment) {
        equipmentItems = equipment;
        setExercise();
    }


    @Override
    public void onWorkoutDetailsLoaded(WorkoutDetails workoutDetails) {
        this.workoutDetails = workoutDetails;
        layoutReadyListener.onLayoutReady();
        //((ExerciseInfoActivity)activity).setLayoutElements();
        //((ExerciseInfoActivity)activity).setFloatingActionButtons();
        //setExercise();
        loadEquipment();
    }

    public void loadUserDetails() {
        LoadUser loadUser = new LoadUser(this);
        loadUser.loadUser();

        /*
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
         */
    }

    @Override
    public void onUserLoaded(User user) {
        this.user = user;
    }


    public void navigateRight() {
        int idx = sharedPreferences.getInt(userId, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (exerciseItems.size() > idx + 1) {
            editor.putInt(userId, idx + 1);
            editor.apply();
            setExercise();

        } else {
            exercisesEndListener.onExercisesFinished();
            //((ExerciseInfoActivity)activity).showCompletedFragment();
        }
    }

    public void navigateLeft() {
        int idx = sharedPreferences.getInt(userId, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (idx > 0) {
            editor.putInt(userId, idx - 1);
            editor.apply();
            setExercise();
        }
    }

    public void setExercise() {
        int idx = sharedPreferences.getInt(userId, 0);
        timerListener.onTimerCheck();
        //((ExerciseInfoActivity)activity).checkCounter();
        if (idx == exerciseItems.size() - 1)
            timerListener.onSetTimer(20 * 60 * 1000);
            //((ExerciseInfoActivity)activity).setTimer(20 * 60 * 1000);
        else
            timerListener.onSetTimer(60 * 1000);
            //((ExerciseInfoActivity)activity).setTimer(60 * 1000);

        Exercise item = exerciseItems.get(idx);

        String name = item.name;
        setVideo(name);

        String equipments = setEquipments(item);

        dataReadyListener.onSetTitleAndEquipment(name, equipments);
        //((ExerciseInfoActivity)activity).setTitleAndEquipment(name, equipments);

        setDetailsAndTimer(item);
    }

    public String calculateDisplayTime(long millisUntilFinished) {
        long minutes = (millisUntilFinished / 1000) / 60;
        long seconds = (millisUntilFinished / 1000) % 60;
        String minutesText = Long.toString(minutes);
        String secondsText = Long.toString(seconds);
        if (minutes < 10) minutesText = "0" + minutesText;
        if (seconds < 10) secondsText = "0" + secondsText;

        return minutesText + ":" + secondsText;
    }

    public void setVideo(String name) {
        name = transformName(name);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        StorageReference pathReference = storageRef.child(name + ".mp4");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                dataReadyListener.onVideoReady(uri);
                //((ExerciseInfoActivity)activity).startVideo(uri);
            }
        });
    }

    public String transformName(String name) {
        name = name.toLowerCase();
        name = name.replace(" ", "_");
        name = name.replace(",", "");
        name = name.replace("-", "_");
        name = name.replace("_/_", "_");
        return name;
    }

    public String setEquipments(Exercise item) {
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
        return equipments;
    }

    public void setDetailsAndTimer(Exercise item) {
        String details = "";
        String timer = "";
        switch (workoutDetails.type) {
            case "Lower body":
            case "Upper body":
                if (item.rep_time == 0) {
                    if (itemUsesWeight(item)) {
                        details = details + activity.getString(R.string.weight_max) + ", ";
                        details = details + activity.getString(R.string.reps_weight);
                    } else {
                        details = details + activity.getString(R.string.reps_no_weight);
                    }
                    timer = "";
                    timerListener.onTimerCheck();
                    //((ExerciseInfoActivity)activity).checkCounter();
                } else {
                    details = activity.getString(R.string.timer_short);
                    timer = activity.getString(R.string.timer_start);
                }
                break;
            case "Cardio 1":
            case "Cardio 2":
                if (item.rep_time == 0) {
                    if (itemUsesWeight(item)) {
                        details = details + activity.getString(R.string.weight_70) + ", ";
                        details = details + activity.getString(R.string.reps_cardio);
                    } else {
                        details = details + activity.getString(R.string.reps_no_weight);
                    }
                    timer = "";
                    timerListener.onTimerCheck();
                    //((ExerciseInfoActivity)activity).checkCounter();

                } else {
                    if (item.id == exerciseItems.get(exerciseItems.size() - 1).id)
                        details = activity.getString(R.string.timer_long);
                    else
                        details = activity.getString(R.string.timer_short);

                    timer = activity.getString(R.string.timer_start);
                }
                break;
        }
        dataReadyListener.onSetDescriptionAndTimer(details, timer);
        //((ExerciseInfoActivity)activity).setDetailsAndTimerText(details, timer);

    }

    public boolean itemUsesWeight(Exercise item) {
        if ((2 < item.equipment1 && item.equipment1 < 8) || item.equipment1 == 20 || item.equipment1 == 22 || item.equipment1 == 25)
            return true;
        if ((2 < item.equipment2 && item.equipment2 < 8) || item.equipment2 == 20 || item.equipment2 == 22 || item.equipment2 == 25)
            return true;
        return false;

    }

    public void completeExercise() {
        databaseReference.child("Workout_Details").child(userId).child("In_Progress").setValue(false);
        databaseReference.child("Workout_Details").child(userId).child("Exercises").removeValue();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(userId, 0);
        editor.apply();
        String type = setWorkoutType();
        databaseReference.child("Workout_Details").child(userId).child("Type").setValue(type);
    }

    public String setWorkoutType() {
        boolean muscle = user.gain_muscle;
        boolean weight = user.lose_weight;
        String type = "";
        if (muscle) {
            if (weight) {
                switch (workoutDetails.type) {
                    case "Lower body":
                        type = "Cardio 1";
                        break;
                    case "Cardio 1":
                        type = "Upper body";
                        break;
                    case "Upper body":
                        type = "Cardio 2";
                        break;
                    case "Cardio 2":
                        type = "Lower body";
                }
            } else {
                switch (workoutDetails.type) {
                    case "Lower body":
                        type = "Upper body";
                        break;
                    case "Upper body":
                        type = "Lower body";
                        break;
                }
            }
        } else {
            type = "Cardio 1";

        }
        return type;
    }
}
