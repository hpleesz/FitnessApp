package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;

import hu.bme.aut.fitnessapp.Entities.User;

public class LoadUser extends DatabaseConnection {

    public interface UserLoadedListener {
        void onUserLoaded(User user);
    }

    public interface SingleUserLoadedListener {
        void onUserLoadedSingle(User user);
    }

    private LoadUser.UserLoadedListener userLoadedListener;
    private LoadUser.SingleUserLoadedListener singleListLoadedListener;

    public LoadUser() {
        super();
    }

    public void setListLoadedListener(Object object) {
        userLoadedListener = (LoadUser.UserLoadedListener)object;

    }

    public void setSingleListLoadedListener(Object object) {
        singleListLoadedListener = (LoadUser.SingleUserLoadedListener)object;
    }

    private ValueEventListener eventListener;

    public void loadUser() {
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                userLoadedListener.onUserLoaded(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        };
        getDatabaseReference().child("Users").child(getUserId()).addValueEventListener(eventListener);
    }

    public void loadUserSingle() {
        Query lastWaterQuery = getDatabaseReference().child("Users").child(getUserId());
        lastWaterQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                singleListLoadedListener.onUserLoadedSingle(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
            });
    }
    public void updateItem(User user) {
        getDatabaseReference().child("Users").child(getUserId()).setValue(user);
    }

    public void removeItem() {
        getDatabaseReference().child("Users").child(getUserId()).removeValue();
    }

    public void setGoalReached(boolean reached) {
        getDatabaseReference().child("Users").child(getUserId()).child("goal_reached").setValue(reached);
    }

    public void removeListeners() {
        if(eventListener != null) {
            getDatabaseReference().child("Users").child(getUserId()).removeEventListener(eventListener);
        }
    }
}
