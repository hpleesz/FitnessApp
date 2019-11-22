package hu.bme.aut.fitnessapp.Models.UserModels.SettingsModels;

import com.google.firebase.auth.FirebaseAuth;

import hu.bme.aut.fitnessapp.Entities.User;

public abstract class UserSettingsModel {

    private boolean male = false;
    private boolean female = false;
    private boolean lose_weight = false;
    private boolean gain_muscle = false;

    private FirebaseAuth mAuth;

    private String userId;

    public UserSettingsModel() {}

    public void maleSetting() {
        male = true;
        female = false;
    }

    public void femaleSetting() {
        male = false;
        female = true;
    }

    public void loseWeightSetting() {
        lose_weight = !lose_weight;
    }

    public void gainMuscleSetting() {
        gain_muscle = !gain_muscle;
    }

    public User newUser(String name, int year, int month, int day, String goal, String height) {
        int gender;
        if(male) gender = 0;
        else gender = 1;
        User user = new User(
                name,
                year,
                month,
                day,
                gain_muscle,
                lose_weight,
                gender,
                Double.parseDouble(goal),
                Double.parseDouble(height)
        );
        return user;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public boolean isFemale() {
        return female;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }

    public boolean isLose_weight() {
        return lose_weight;
    }

    public void setLose_weight(boolean lose_weight) {
        this.lose_weight = lose_weight;
    }

    public boolean isGain_muscle() {
        return gain_muscle;
    }

    public void setGain_muscle(boolean gain_muscle) {
        this.gain_muscle = gain_muscle;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
