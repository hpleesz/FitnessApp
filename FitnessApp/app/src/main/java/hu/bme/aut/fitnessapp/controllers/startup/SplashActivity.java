package hu.bme.aut.fitnessapp.controllers.startup;

import android.content.Intent;

import hu.bme.aut.fitnessapp.controllers.gym.GymMainActivity;
import hu.bme.aut.fitnessapp.controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.controllers.user.workout.MainActivity;
import hu.bme.aut.fitnessapp.models.startup_models.SplashModel;

public class SplashActivity extends InternetCheckActivity implements SplashModel.ActiveUserListener {

    private SplashModel splashModel;

    @Override
    protected void onStart() {
        super.onStart();
        splashModel = new SplashModel(this);
        splashModel.checkFirstLogin();
    }

    public void startLogin() {
        finish();
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
    }

    public void startGym() {
        finish();
        startActivity(new Intent(SplashActivity.this, GymMainActivity.class));
    }

    public void startUser() {
        finish();
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }

    @Override
    public void onNoActiveUser() {
        startLogin();
    }

    @Override
    public void onUserActive() {
        startUser();
    }

    @Override
    public void onGymActive() {
        startGym();
    }

    @Override
    public void onStop() {
        splashModel.removeListeners();
        super.onStop();
    }
}
