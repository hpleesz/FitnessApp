package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoadProfile extends DatabaseConnection {

    public interface ProfileLoadedListener {
        void onProfileLoaded(boolean isUser);
    }

    private LoadProfile.ProfileLoadedListener listLoadedListener;

    public LoadProfile() {
        super();
    }

    public void setListLoadedListener(Object object) {
        listLoadedListener = (LoadProfile.ProfileLoadedListener)object;
    }

    private ValueEventListener eventListener;

    public void loadProfile() {
            eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean isUser = (boolean)dataSnapshot.getValue();

                listLoadedListener.onProfileLoaded(isUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        };
        getDatabaseReference().child("Profiles").child(getUserId()).addValueEventListener(eventListener);
    }

    public void addNewItem(boolean isUser) {
        getDatabaseReference().child("Profiles").child(getUserId()).setValue(isUser);
    }

    public void removeItem() {
        getDatabaseReference().child("Profiles").child(getUserId()).removeValue();
    }

    public void removeListeners() {
        if(eventListener != null) {
            getDatabaseReference().child("Profiles").child(getUserId()).removeEventListener(eventListener);
        }
    }
}
