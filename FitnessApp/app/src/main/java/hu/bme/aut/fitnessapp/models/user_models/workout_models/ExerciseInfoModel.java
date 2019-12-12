package hu.bme.aut.fitnessapp.models.user_models.workout_models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.fitnessapp.entities.Equipment;
import hu.bme.aut.fitnessapp.entities.Exercise;
import hu.bme.aut.fitnessapp.entities.User;
import hu.bme.aut.fitnessapp.entities.WorkoutDetails;
import hu.bme.aut.fitnessapp.models.database_models.LoadEquipment;
import hu.bme.aut.fitnessapp.models.database_models.LoadUser;
import hu.bme.aut.fitnessapp.models.database_models.LoadWorkoutDetails;
import hu.bme.aut.fitnessapp.R;

import static android.content.Context.MODE_PRIVATE;

public class ExerciseInfoModel extends VideoModel implements LoadWorkoutDetails.WorkoutDetailsLoadedListener, LoadUser.UserLoadedListener, LoadEquipment.EquipmentLoadedListener{

    private SharedPreferences sharedPreferences;
    private String userId;

    private List<Exercise> exerciseItems;
    private List<Equipment> equipmentItems;
    private User user;
    private WorkoutDetails workoutDetails;

    private LoadWorkoutDetails loadWorkoutDetails;
    private LoadEquipment loadEquipment;
    private LoadUser loadUser;

    private Context activity;

    private static String EXERCISE_NUMBER = "Exercise Number";

    private ExerciseInfoModel.TimerListener timerListener;
    private ExerciseInfoModel.DataReadyListener dataReadyListener;
    private ExerciseInfoModel.ExercisesEndListener exercisesEndListener;
    private ExerciseInfoModel.LayoutReadyListener layoutReadyListener;

    public interface TimerListener {
        void onTimerCheck();
        void onSetTimer(long time);
    }

    public interface DataReadyListener {
        void onSetTitleAndEquipment(String title, String equipment);
        void onSetDescriptionAndTimer(String desc, String timer);
    }

    public interface ExercisesEndListener {
        void onExercisesFinished();
    }

    public interface LayoutReadyListener {
        void onLayoutReady();
    }

    public ExerciseInfoModel(Object object, List<Exercise> exerciseItems) {
        super(object);
        timerListener = (ExerciseInfoModel.TimerListener)object;
        dataReadyListener = (ExerciseInfoModel.DataReadyListener)object;
        exercisesEndListener = (ExerciseInfoModel.ExercisesEndListener)object;
        layoutReadyListener = (ExerciseInfoModel.LayoutReadyListener)object;

        this.activity = (AppCompatActivity)object;
        this.exerciseItems = exerciseItems;
        Log.d("exercises", Integer.toString(this.exerciseItems.size()));
        sharedPreferences = activity.getSharedPreferences(EXERCISE_NUMBER, MODE_PRIVATE);
    }

    public void initFirebase() {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void loadWorkoutDetails() {
        loadWorkoutDetails = new LoadWorkoutDetails();
        loadWorkoutDetails.setListLoadedListener(this);
        loadWorkoutDetails.loadWorkoutDetails();
    }

    public void loadEquipment() {
        loadEquipment = new LoadEquipment(this);
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
        loadEquipment();
    }

    public void loadUserDetails() {
        loadUser = new LoadUser();
        loadUser.setListLoadedListener(this);
        loadUser.loadUser();
    }

    @Override
    public void onUserLoaded(User user) {
        this.user = user;
    }

    @Override
    public void navigateRight() {
        int idx = sharedPreferences.getInt(userId, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (exerciseItems.size() > idx + 1) {
            editor.putInt(userId, idx + 1);
            editor.apply();
            setExercise();

        } else {
            exercisesEndListener.onExercisesFinished();
        }
    }

    @Override
    public void navigateLeft() {
        int idx = sharedPreferences.getInt(userId, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (idx > 0) {
            editor.putInt(userId, idx - 1);
            editor.apply();
            setExercise();
        }
    }

    private void setExercise() {
        int idx = sharedPreferences.getInt(userId, 0);
        timerListener.onTimerCheck();
        if (idx == exerciseItems.size() - 1)
            timerListener.onSetTimer(20 * 60 * 1000L);
        else
            timerListener.onSetTimer(60 * 1000L);

        Exercise item = exerciseItems.get(idx);

        String name = item.getName();
        setVideo(name);

        String equipments = setEquipments(item);

        dataReadyListener.onSetTitleAndEquipment(name, equipments);

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

    public String setEquipments(Exercise item) {
        String equipments = "";


        for (int i = 0; i < equipmentItems.size(); i++) {
            for (int j = 0; j < equipmentItems.size(); j++) {
                if (item.getEquipment1() == equipmentItems.get(i).getId() && item.getEquipment2() == equipmentItems.get(j).getId())
                    if (i == 0) equipments = equipmentItems.get(j).getName();
                    else if (j == 0) equipments = equipmentItems.get(i).getName();
                    else
                        equipments = equipmentItems.get(i).getName() + ", " + equipmentItems.get(j).getName();
            }
        }
        return equipments;
    }

    private void setDetailsAndTimer(Exercise item) {
        String details = "";
        String timer = "";
        switch (workoutDetails.getType()) {
            case "Lower body":
            case "Upper body":
                if (item.getRepTime() == 0) {
                    if (itemUsesWeight(item)) {
                        details = details + activity.getString(R.string.weight_max) + ", ";
                        details = details + activity.getString(R.string.reps_weight);
                    } else {
                        details = details + activity.getString(R.string.reps_no_weight);
                    }
                    timer = "";
                    timerListener.onTimerCheck();
                } else {
                    details = activity.getString(R.string.timer_short);
                    timer = activity.getString(R.string.timer_start);
                }
                break;
            case "Cardio 1":
            case "Cardio 2":
                if (item.getRepTime() == 0) {
                    if (itemUsesWeight(item)) {
                        details = details + activity.getString(R.string.weight_70) + ", ";
                        details = details + activity.getString(R.string.reps_cardio);
                    } else {
                        details = details + activity.getString(R.string.reps_no_weight);
                    }
                    timer = "";
                    timerListener.onTimerCheck();

                } else {
                    if (item.getId().equals(exerciseItems.get(exerciseItems.size() - 1).getId()))
                        details = activity.getString(R.string.timer_long);
                    else
                        details = activity.getString(R.string.timer_short);

                    timer = activity.getString(R.string.timer_start);
                }
                break;
        }
        dataReadyListener.onSetDescriptionAndTimer(details, timer);
    }

    public boolean itemUsesWeight(Exercise item) {
        if ((2 < item.getEquipment1() && item.getEquipment1() < 8) || item.getEquipment1() == 20 || item.getEquipment1() == 22 || item.getEquipment1() == 25)
            return true;
        return (2 < item.getEquipment2() && item.getEquipment2() < 8) || item.getEquipment2() == 20 || item.getEquipment2() == 22 || item.getEquipment2() == 25;

    }

    public void completeExercise() {
        loadWorkoutDetails = new LoadWorkoutDetails();
        loadWorkoutDetails.setProgress(false);
        loadWorkoutDetails.removeExercises();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(userId, 0);
        editor.apply();
        String type = setWorkoutType();
        loadWorkoutDetails.setType(type);
    }

    public String setWorkoutType() {
        boolean muscle = user.getGainMuscle();
        boolean weight = user.getLoseWeight();
        String type = "";
        if (muscle) {
            if (weight) {
                switch (workoutDetails.getType()) {
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
                switch (workoutDetails.getType()) {
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

    public void setEquipmentItems(List<Equipment> equipmentItems) {
        this.equipmentItems = equipmentItems;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setWorkoutDetails(WorkoutDetails workoutDetails) {
        this.workoutDetails = workoutDetails;
    }

    public void removeListeners() {
        if(loadEquipment != null) loadEquipment.removeListeners();
        if(loadWorkoutDetails != null) loadWorkoutDetails.removeListeners();
        if(loadUser != null) loadUser.removeListeners();
    }

}
