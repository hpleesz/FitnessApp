package hu.bme.aut.fitnessapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent intent = new Intent(this, MainActivity.class);

        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();

        if(User != null) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }
        else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }

        //startActivity(intent);
        finish();
    }
}
