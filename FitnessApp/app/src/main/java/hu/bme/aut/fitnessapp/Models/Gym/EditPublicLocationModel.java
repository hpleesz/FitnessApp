package hu.bme.aut.fitnessapp.Models.Gym;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Adapters.EquipmentAdapter;
import hu.bme.aut.fitnessapp.Entities.Equipment;
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
