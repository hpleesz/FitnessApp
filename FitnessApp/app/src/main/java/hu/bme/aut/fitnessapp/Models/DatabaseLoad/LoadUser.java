package hu.bme.aut.fitnessapp.Models.DatabaseLoad;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Entities.Exercise;
import hu.bme.aut.fitnessapp.Entities.User;

public class LoadUser extends DatabaseConnection {

    public interface UserLoadedListener {
        void onUserLoaded(User user);
    }

    private LoadUser.UserLoadedListener listLoadedListener;

    public LoadUser(Object object) {
        super();
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
}
