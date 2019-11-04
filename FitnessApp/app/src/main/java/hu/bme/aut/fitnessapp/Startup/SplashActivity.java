package hu.bme.aut.fitnessapp.Startup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hu.bme.aut.fitnessapp.Gym.GymMainActivity;
import hu.bme.aut.fitnessapp.User.Workout.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private boolean user;
    private String userId;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFirstLogin();
    }

    public void checkFirstLogin() {
        SharedPreferences first = getSharedPreferences(LoginActivity.FIRST, MODE_PRIVATE);
        boolean first_login = first.getBoolean("First login", true);

        if(first_login) {
            finish();
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        else {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user1 = mAuth.getCurrentUser();

            if(user1 != null) {
                getUser();
            }
            else {
                finish();
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
        }
    }

    public void getUser() {
        userId = mAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user = (boolean)dataSnapshot.getValue();

                if(user) {
                    getDetails();
                    }
                else {
                    finish();
                    startActivity(new Intent(SplashActivity.this, GymMainActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.child("Profiles").child(userId).addValueEventListener(eventListener);
    }

    public void getDetails() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null) {
                    mAuth.getCurrentUser().delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        databaseReference.child("Profiles").child(userId).removeValue();
                                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    }
                                }
                            });
                }
                else {
                    finish();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.child("Users").child(userId).addValueEventListener(eventListener);
    }
}
