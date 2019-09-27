package hu.bme.aut.fitnessapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Profile {

    public String id;
    public Boolean user;

    public Profile(){}

    public Profile(String id, Boolean user) {
        this.id = id;
        this.user = user;
    }
}
