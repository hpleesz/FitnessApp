package hu.bme.aut.fitnessapp.models.user_models.settings_models;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

import hu.bme.aut.fitnessapp.controllers.user.settings.SettingsActivity;
import hu.bme.aut.fitnessapp.entities.User;
import hu.bme.aut.fitnessapp.models.database_models.LoadProfile;
import hu.bme.aut.fitnessapp.models.database_models.LoadUser;
import hu.bme.aut.fitnessapp.models.database_models.LoadWater;
import hu.bme.aut.fitnessapp.models.database_models.LoadWeight;
import hu.bme.aut.fitnessapp.models.database_models.LoadWorkoutDetails;

import static android.content.Context.MODE_PRIVATE;

public class UserModel extends UserSettingsModel {

    private Context activity;
    private LoadUser loadUser;

    private UserModel.RegisterCanceledListener registerCanceledListener;

    public interface RegisterCanceledListener {
        void onRegisterCanceled();
    }

    public UserModel(Object object) {
        registerCanceledListener = (UserModel.RegisterCanceledListener)object;
        this.activity = (AppCompatActivity)object;
    }

    public void removeUser() {
        getmAuth().getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            backPressed();
                            registerCanceledListener.onRegisterCanceled();
                        }
                    }
                });
    }

    public boolean isValid(int nameLength, int heightLength, int goalLength, int weightLength) {
        return !(nameLength == 0 || weightLength == 0 || heightLength == 0 || goalLength == 0 || (!isFemale() && !isMale()) || (isFemale() && isMale()) || (!isLoseWeight() && !isGainMuscle()));
    }

    public void backPressed() {
        LoadProfile loadProfile = new LoadProfile();
        loadProfile.removeItem();

        loadUser = new LoadUser();
        loadUser.removeItem();
    }


    public void writeNewUser(String name, int year, int month, int day, String goal, String height, String weight) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SettingsActivity.NOTIFICATIONS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getUserId(), true);
        editor.apply();

        User user = newUser(name, year, month, day, goal, height);

        loadUser = new LoadUser();
        loadUser.updateItem(user);
        if(goal.equals(weight)) loadUser.setGoalReached(true);

        LoadWorkoutDetails loadWorkoutDetails = new LoadWorkoutDetails();
        loadWorkoutDetails.setProgress(false);

        String type = "";
        if(isGainMuscle()) type = "Lower body";
        else type = "Cardio 1";

        loadWorkoutDetails.setType(type);

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        long date = calendar.getTimeInMillis() / 1000;

        LoadWater loadWater = new LoadWater();
        loadWater.addNewItem(date, 0);


        LoadWeight loadWeight = new LoadWeight();
        loadWeight.addNewItem(date, Double.parseDouble(weight));
    }

}