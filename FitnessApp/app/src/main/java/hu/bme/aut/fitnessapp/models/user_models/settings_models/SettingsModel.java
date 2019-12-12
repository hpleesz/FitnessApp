package hu.bme.aut.fitnessapp.models.user_models.settings_models;

import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

import hu.bme.aut.fitnessapp.controllers.user.settings.SettingsActivity;
import hu.bme.aut.fitnessapp.entities.User;
import hu.bme.aut.fitnessapp.models.database_models.LoadUser;

import static android.content.Context.MODE_PRIVATE;

public class SettingsModel extends UserSettingsModel implements LoadUser.UserLoadedListener{

    private SharedPreferences sharedPreferences;
    private String userID;
    private LoadUser loadUser;

    private SettingsModel.SettingsListener settingsListener;

    public interface SettingsListener{
        void onSettingsLoaded(String name, String height, String weight, int year, int month, int day);
    }


    public SettingsModel(Object object) {
        settingsListener = (SettingsModel.SettingsListener)object;

        sharedPreferences = ((SettingsActivity)object).getSharedPreferences(SettingsActivity.NOTIFICATIONS, MODE_PRIVATE);
    }

    public void initFirebase() {
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

    public boolean isValid(int nameLength, int heightLength, int goalLength) {
        return !(nameLength == 0 || heightLength == 0 || goalLength == 0 || (!isFemale() && !isMale()) || (isFemale() && isMale()) || (!isLoseWeight() && !isGainMuscle()));
    }

    public void loadUserdata() {
        loadUser = new LoadUser();
        loadUser.setListLoadedListener(this);
        loadUser.loadUser();
    }

    @Override
    public void onUserLoaded(User user) {
        setMale(user.getGender() != 1);
        setFemale(!isMale());

        setLoseWeight(user.getLoseWeight());
        setGainMuscle(user.getGainMuscle());

        settingsListener.onSettingsLoaded(user.getName(), Double.toString(user.getHeight()), Double.toString(user.getGoalWeight()), user.getYear(), user.getMonth(), user.getDay());
    }

    public void removeListeners() {
        if(loadUser != null) loadUser.removeListeners();
    }

}
