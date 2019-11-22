package hu.bme.aut.fitnessapp.Models.GymModels;

import android.content.Context;

import hu.bme.aut.fitnessapp.Entities.PublicLocation;

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
