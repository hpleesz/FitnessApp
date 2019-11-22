package hu.bme.aut.fitnessapp.Models.UserModels.SettingsModels;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

import hu.bme.aut.fitnessapp.Controllers.User.Settings.SettingsActivity;
import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadProfile;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadUser;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadWater;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadWeight;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadWorkoutDetails;

import static android.content.Context.MODE_PRIVATE;

public class UserModel extends UserSettingsModel {

    private Context activity;

    public interface RegisterCanceledListener {
        void onRegisterCanceled();
    }

    private UserModel.RegisterCanceledListener registerCanceledListener;

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

    public boolean isValid(int name_length, int height_length, int goal_length, int weight_length) {
        return !(name_length == 0 || weight_length == 0 || height_length == 0 || goal_length == 0 || (!isFemale() && !isMale()) || (isFemale() && isMale()) || (!isLose_weight() && !isGain_muscle()));
    }

    public void backPressed() {
        LoadProfile loadProfile = new LoadProfile();
        loadProfile.removeItem();

        LoadUser loadUser = new LoadUser();
        loadUser.removeItem();
    }


    public void writeNewUser(String name, int year, int month, int day, String goal, String height, String weight) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(SettingsActivity.NOTIFICATIONS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getUserId(), true);
        editor.apply();

        User user = newUser(name, year, month, day, goal, height);

        LoadUser loadUser = new LoadUser();
        loadUser.updateItem(user);

        LoadWorkoutDetails loadWorkoutDetails = new LoadWorkoutDetails();
        loadWorkoutDetails.setProgress(false);

        String type = "";
        if(isGain_muscle()) type = "Lower body";
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