package hu.bme.aut.fitnessapp.models.user_models.settings_models;

import com.google.firebase.auth.FirebaseAuth;

import hu.bme.aut.fitnessapp.entities.User;

public abstract class UserSettingsModel {

    private boolean male = false;
    private boolean female = false;
    private boolean loseWeight = false;
    private boolean gainMuscle = false;

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
        loseWeight = !loseWeight;
    }

    public void gainMuscleSetting() {
        gainMuscle = !gainMuscle;
    }

    public User newUser(String name, int year, int month, int day, String goal, String height) {
        int gender;
        if(male) gender = 0;
        else gender = 1;
        return new User(
                name,
                year,
                month,
                day,
                gainMuscle,
                loseWeight,
                gender,
                Double.parseDouble(goal),
                Double.parseDouble(height)
        );
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

    public boolean isLoseWeight() {
        return loseWeight;
    }

    public void setLoseWeight(boolean loseWeight) {
        this.loseWeight = loseWeight;
    }

    public boolean isGainMuscle() {
        return gainMuscle;
    }

    public void setGainMuscle(boolean gainMuscle) {
        this.gainMuscle = gainMuscle;
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
