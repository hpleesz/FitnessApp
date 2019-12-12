package hu.bme.aut.fitnessapp.entities;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    private String name;
    private Integer year;
    private Integer month;
    private Integer day;
    private Boolean gainMuscle;
    private Boolean loseWeight;
    private Integer gender;
    private Double goalWeight;
    private Double height;
    private Boolean goalReached;

    public User(){}

    public User(String name, Integer year, Integer month, Integer day, Boolean gainMuscle, Boolean loseWeight, Integer gender, Double goalWeight, Double height) {
        this.name = name;
        this.year = year;
        this.month = month;
        this.day = day;
        this.gainMuscle = gainMuscle;
        this.loseWeight = loseWeight;
        this.gender = gender;
        this.goalWeight = goalWeight;
        this.height = height;
        this.goalReached = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Boolean getGainMuscle() {
        return gainMuscle;
    }

    public void setGainMuscle(Boolean gainMuscle) {
        this.gainMuscle = gainMuscle;
    }

    public Boolean getLoseWeight() {
        return loseWeight;
    }

    public void setLoseWeight(Boolean loseWeight) {
        this.loseWeight = loseWeight;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Double getGoalWeight() {
        return goalWeight;
    }

    public void setGoalWeight(Double goalWeight) {
        this.goalWeight = goalWeight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Boolean getGoalReached() {
        return goalReached;
    }

    public void setGoalReached(Boolean goalReached) {
        this.goalReached = goalReached;
    }


}
