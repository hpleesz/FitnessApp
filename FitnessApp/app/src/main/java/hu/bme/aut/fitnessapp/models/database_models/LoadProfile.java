package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoadProfile extends DatabaseConnection {

    private static final String PROFILES = "Profiles";

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
                logger.error(DB_ERROR, databaseError.toException());
            }
        };
        getDatabaseReference().child(PROFILES).child(getUserId()).addValueEventListener(eventListener);
    }

    public void addNewItem(boolean isUser) {
        getDatabaseReference().child(PROFILES).child(getUserId()).setValue(isUser);
    }

    public void removeItem() {
        getDatabaseReference().child(PROFILES).child(getUserId()).removeValue();
    }

    public void removeListeners() {
        if(eventListener != null) {
            getDatabaseReference().child(PROFILES).child(getUserId()).removeEventListener(eventListener);
        }
    }
}
