package hu.bme.aut.fitnessapp.Controllers.Startup;

import android.content.Intent;
import android.os.Bundle;

import hu.bme.aut.fitnessapp.Controllers.Gym.GymMainActivity;
import hu.bme.aut.fitnessapp.Controllers.InternetCheckActivity;
import hu.bme.aut.fitnessapp.Controllers.User.Workout.MainActivity;
import hu.bme.aut.fitnessapp.Models.Startup.SplashModel;

public class SplashActivity extends InternetCheckActivity implements SplashModel.ActiveUserListener {



    private SplashModel splashModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
}
