package hu.bme.aut.fitnessapp.Models.UserModels.WorkoutModels;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Equipment;
import hu.bme.aut.fitnessapp.Entities.Exercise;
import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Entities.WorkoutDetails;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadEquipment;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadUser;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadWorkoutDetails;
import hu.bme.aut.fitnessapp.R;

import static android.content.Context.MODE_PRIVATE;

public class ExerciseInfoModel extends VideoModel implements LoadWorkoutDetails.WorkoutDetailsLoadedListener, LoadUser.UserLoadedListener, LoadEquipment.EquipmentLoadedListener{

    private SharedPreferences sharedPreferences;
    private String userId;

    private ArrayList<Exercise> exerciseItems;
    private ArrayList<Equipment> equipmentItems;
    private User user;
    private WorkoutDetails workoutDetails;

    private LoadWorkoutDetails loadWorkoutDetails;
    private LoadEquipment loadEquipment;
    private LoadUser loadUser;

    private Context activity;

    private static String EXERCISE_NUMBER = "Exercise Number";

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

    private ExerciseInfoModel.TimerListener timerListener;
    private ExerciseInfoModel.DataReadyListener dataReadyListener;
    private ExerciseInfoModel.ExercisesEndListener exercisesEndListener;
    private ExerciseInfoModel.LayoutReadyListener layoutReadyListener;

    public ExerciseInfoModel(Object object, ArrayList<Exercise> exerciseItems) {
        super(object);
        timerListener = (ExerciseInfoModel.TimerListener)object;
        dataReadyListener = (ExerciseInfoModel.DataReadyListener)object;
        exercisesEndListener = (ExerciseInfoModel.ExercisesEndListener)object;
        layoutReadyListener = (ExerciseInfoModel.LayoutReadyListener)object;

        this.activity = (AppCompatActivity)object;
        this.exerciseItems = exerciseItems;
        sharedPreferences = activity.getSharedPreferences(EXERCISE_NUMBER, MODE_PRIVATE);
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
            timerListener.onSetTimer(20 * 60 * 1000);
        else
            timerListener.onSetTimer(60 * 1000);

        Exercise item = exerciseItems.get(idx);

        String name = item.name;
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
                if (item.equipment1 == equipmentItems.get(i).id && item.equipment2 == equipmentItems.get(j).id)
                    if (i == 0) equipments = equipmentItems.get(j).name;
                    else if (j == 0) equipments = equipmentItems.get(i).name;
                    else
                        equipments = equipmentItems.get(i).name + ", " + equipmentItems.get(j).name;
            }
        }
        return equipments;
    }

    private void setDetailsAndTimer(Exercise item) {
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

                } else {
                    if (item.id.equals(exerciseItems.get(exerciseItems.size() - 1).id))
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
        if ((2 < item.equipment1 && item.equipment1 < 8) || item.equipment1 == 20 || item.equipment1 == 22 || item.equipment1 == 25)
            return true;
        return (2 < item.equipment2 && item.equipment2 < 8) || item.equipment2 == 20 || item.equipment2 == 22 || item.equipment2 == 25;

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

    public void setEquipmentItems(ArrayList<Equipment> equipmentItems) {
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
