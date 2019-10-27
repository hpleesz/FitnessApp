package hu.bme.aut.fitnessapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser User;
    private boolean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent intent = new Intent(this, MainActivity.class);

        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();



        if(User != null) {
            String userId = mAuth.getCurrentUser().getUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Profiles").child(userId);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI

                    //TODO datasnapshot.get value null mert nem adta hozzá az adatbázishoz.
                    //TODO maybe ha már a regisztrációnál mentek egy user tábla dolgot csak üresen akkor jobb lenne
                    //TODO arra lehet szurni
                    user = (boolean)dataSnapshot.getValue();

                    if(user) {
                        finish();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));

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
            databaseReference.addValueEventListener(eventListener);
        }
        else {
            finish();
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }



        //startActivity(intent);
    }
}
