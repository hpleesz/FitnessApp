package hu.bme.aut.fitnessapp.controllers.user.workout;

import android.content.Intent;

import android.os.Bundle;

import hu.bme.aut.fitnessapp.models.user_models.workout_models.WarmUpModel;


public class WarmUpActivity extends StretchWarmUpActivity {

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        type = (String) i.getSerializableExtra("type");
    }

    @Override
    protected void onStart() {
        super.onStart();
        setModel(new WarmUpModel(this, type));
    }

    public String getIntentType() {
        return type;
    }


}
