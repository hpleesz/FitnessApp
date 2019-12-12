package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import hu.bme.aut.fitnessapp.entities.User;

public class LoadUser extends DatabaseConnection {

    private static final String USERS = "Users";

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

                String name = dataSnapshot.child("name").getValue(String.class);
                int year = dataSnapshot.child("year").getValue(Integer.class);
                int month = dataSnapshot.child("month").getValue(Integer.class);
                int day = dataSnapshot.child("day").getValue(Integer.class);
                boolean gainMuscle = dataSnapshot.child("gain_muscle").getValue(Boolean.class);
                boolean loseWeight = dataSnapshot.child("lose_weight").getValue(Boolean.class);
                int gender = dataSnapshot.child("gender").getValue(Integer.class);
                double goalWeight = dataSnapshot.child("goal_weight").getValue(Integer.class);
                double height = dataSnapshot.child("height").getValue(Double.class);
                boolean goalReached = dataSnapshot.child("goal_reached").getValue(Boolean.class);

                User user = new User(name, year, month, day, gainMuscle, loseWeight, gender, goalWeight, height);
                user.setGoalReached(goalReached);

                userLoadedListener.onUserLoaded(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }
        };
        getDatabaseReference().child(USERS).child(getUserId()).addValueEventListener(eventListener);
    }

    public void loadUserSingle() {
        Query lastWaterQuery = getDatabaseReference().child(USERS).child(getUserId());
        lastWaterQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                singleListLoadedListener.onUserLoadedSingle(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error(DB_ERROR, databaseError.toException());
            }
            });
    }
    public void updateItem(User user) {
        getDatabaseReference().child(USERS).child(getUserId()).setValue(user);
    }

    public void removeItem() {
        getDatabaseReference().child(USERS).child(getUserId()).removeValue();
    }

    public void setGoalReached(boolean reached) {
        getDatabaseReference().child(USERS).child(getUserId()).child("goal_reached").setValue(reached);
    }

    public void removeListeners() {
        if(eventListener != null) {
            getDatabaseReference().child(USERS).child(getUserId()).removeEventListener(eventListener);
        }
    }
}
