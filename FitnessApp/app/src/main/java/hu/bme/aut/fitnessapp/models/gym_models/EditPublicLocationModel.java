package hu.bme.aut.fitnessapp.models.gym_models;

import android.content.Context;

import hu.bme.aut.fitnessapp.entities.PublicLocation;

public class EditPublicLocationModel extends NewPublicLocationModel{

    private PublicLocation publicLocation;


    public EditPublicLocationModel(Context activity, PublicLocation publicLocation) {
        super(activity);
        this.publicLocation = publicLocation;

    }

    public PublicLocation getPublicLocation(){
        return publicLocation;
    }

}
