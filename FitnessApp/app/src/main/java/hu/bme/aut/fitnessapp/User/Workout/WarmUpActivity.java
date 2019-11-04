package hu.bme.aut.fitnessapp.User.Workout;

import android.content.Intent;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class WarmUpActivity extends StretchActivity {

    private String type;
    private boolean lower = true;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        Intent i = getIntent();
        type = (String) i.getSerializableExtra("type");
        super.onCreate(savedInstanceState);

    }

    @Override
    public void loadItems() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        switch (type) {
            case "Cardio 1":
            case "Cardio 2":
            case "Lower body":
                lower = true;
                break;
            case "Upper body":
                lower = false;
                break;
        }

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setItems(new ArrayList<String>());
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String item = dataSnapshot1.child("Name").getValue(String.class);
                    if(lower) {
                        if(dataSnapshot1.child("Lower").getValue(Boolean.class)) {
                            getItems().add(item);
                        }
                    }
                    else {
                        if(dataSnapshot1.child("Upper").getValue(Boolean.class)) {
                            getItems().add(item);
                        }
                    }
                }
                setLayoutElements();
                setExercise();
                setFloatingActionButtons();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.child("Warmup").addValueEventListener(eventListener);
    }


}
