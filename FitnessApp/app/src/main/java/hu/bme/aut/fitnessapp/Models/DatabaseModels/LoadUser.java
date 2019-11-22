package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import hu.bme.aut.fitnessapp.Entities.User;

public class LoadUser extends DatabaseConnection {

    public interface UserLoadedListener {
        void onUserLoaded(User user);
    }

    private LoadUser.UserLoadedListener listLoadedListener;

    public LoadUser() {
        super();
    }

    public void setListLoadedListener(Object object) {
        listLoadedListener = (LoadUser.UserLoadedListener)object;

    }

    public void loadUser() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                listLoadedListener.onUserLoaded(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        getDatabaseReference().child("Users").child(getUserId()).addValueEventListener(eventListener);
    }

    public void updateItem(User user) {
        getDatabaseReference().child("Users").child(getUserId()).setValue(user);
    }

    public void removeItem() {
        getDatabaseReference().child("Users").child(getUserId()).removeValue();
    }
}
