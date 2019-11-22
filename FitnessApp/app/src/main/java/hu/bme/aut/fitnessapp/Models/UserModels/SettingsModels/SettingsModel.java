package hu.bme.aut.fitnessapp.Models.UserModels.SettingsModels;

import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

import hu.bme.aut.fitnessapp.Controllers.User.Settings.SettingsActivity;
import hu.bme.aut.fitnessapp.Entities.User;
import hu.bme.aut.fitnessapp.Models.DatabaseModels.LoadUser;

import static android.content.Context.MODE_PRIVATE;

public class SettingsModel extends UserSettingsModel implements LoadUser.UserLoadedListener{

    private SharedPreferences sharedPreferences;
    private String userID;
    private LoadUser loadUser;

    public interface SettingsListener{
        void onSettingsLoaded(String name, String height, String weight, int year, int month, int day);
    }

    private SettingsModel.SettingsListener settingsListener;

    public SettingsModel(Object object) {
        settingsListener = (SettingsModel.SettingsListener)object;

        sharedPreferences = ((SettingsActivity)object).getSharedPreferences(SettingsActivity.NOTIFICATIONS, MODE_PRIVATE);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }


    public boolean isChecked() {
        return sharedPreferences.getBoolean(userID, true);
    }
    public void checkChanged(boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isChecked) {
            editor.putBoolean(userID, true);
        } else {
            editor.putBoolean(userID, false);
        }
        editor.apply();
    }


    public void saveUserData(String name, int year, int month, int day, String goal, String height) {
        User user = newUser(name, year, month, day, goal, height);
        loadUser.updateItem(user);
    }

    public boolean isValid(int name_length, int height_length, int goal_length) {
        return !(name_length == 0 || height_length == 0 || goal_length == 0 || (!isFemale() && !isMale()) || (isFemale() && isMale()) || (!isLose_weight() && !isGain_muscle()));
    }

    public void loadUserdata() {
        loadUser = new LoadUser();
        loadUser.setListLoadedListener(this);
        loadUser.loadUser();
    }

    @Override
    public void onUserLoaded(User user) {
        setMale(user.gender != 1);
        setFemale(!isMale());

        setLose_weight(user.lose_weight);
        setGain_muscle(user.gain_muscle);

        settingsListener.onSettingsLoaded(user.name, Double.toString(user.height), Double.toString(user.goal_weight), user.year, user.month, user.day);

    }

}
