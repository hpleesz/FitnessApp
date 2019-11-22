package hu.bme.aut.fitnessapp.Controllers.User.Workout;

import android.content.Intent;

import android.os.Bundle;

import hu.bme.aut.fitnessapp.Models.UserModels.WorkoutModels.WarmUpModel;


public class WarmUpActivity extends StretchWarmUpActivity {

    private String type;

    private boolean lower = true;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        type = (String) i.getSerializableExtra("type");


        setModel(new WarmUpModel(this, type));

    }

    public String getIntentType() {
        return type;
    }





    public void setType(String type) {
        this.type = type;
    }

    public boolean isLower() {
        return lower;
    }

}
