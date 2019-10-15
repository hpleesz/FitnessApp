package hu.bme.aut.fitnessapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String name;
    public Integer year;
    public Integer month;
    public Integer day;
    public Boolean gain_muscle;
    public Boolean lose_weight;
    public Integer gender;
    //public Double starting_weight;
    public Double goal_weight;
    public Double height;
    public Boolean goal_reached;

    public User(){}

    public User(String name, Integer year, Integer month, Integer day, Boolean gain_muscle, Boolean lose_weight, Integer gender, Double goal_weight, Double height) {
        this.name = name;
        this.year = year;
        this.month = month;
        this.day = day;
        this.gain_muscle = gain_muscle;
        this.lose_weight = lose_weight;
        this.gender = gender;
        //this.starting_weight = starting_weight;
        this.goal_weight = goal_weight;
        this.height = height;
        this.goal_reached = false;
    }

}
