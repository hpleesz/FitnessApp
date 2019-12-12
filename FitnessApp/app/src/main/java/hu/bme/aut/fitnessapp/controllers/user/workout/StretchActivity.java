package hu.bme.aut.fitnessapp.controllers.user.workout;


import hu.bme.aut.fitnessapp.models.user_models.workout_models.StretchModel;

public class StretchActivity extends StretchWarmUpActivity {

    @Override
    protected void onStart() {
        super.onStart();
        setModel(new StretchModel(this));

    }

}
