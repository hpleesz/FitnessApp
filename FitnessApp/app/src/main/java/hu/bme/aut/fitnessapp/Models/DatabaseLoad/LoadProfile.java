package hu.bme.aut.fitnessapp.Models.DatabaseLoad;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Exercise;
import hu.bme.aut.fitnessapp.Entities.User;

public class LoadProfile extends DatabaseConnection {

    public interface ProfileLoadedListener {
        void onProfileLoaded(boolean isUser);
    }

    private LoadProfile.ProfileLoadedListener listLoadedListener;

    public LoadProfile(Object object) {
        super();
        listLoadedListener = (LoadProfile.ProfileLoadedListener)object;
    }

    public void loadProfile() {
            ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean isUser = (boolean)dataSnapshot.getValue();

                listLoadedListener.onProfileLoaded(isUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        getDatabaseReference().child("Profiles").child(getUserId()).addValueEventListener(eventListener);
    }
}
