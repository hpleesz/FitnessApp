package hu.bme.aut.fitnessapp.Models.DatabaseLoad;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Controllers.User.Workout.WarmUpActivity;
import hu.bme.aut.fitnessapp.Entities.User;

public class LoadWarmUp extends DatabaseConnection{

    public interface WarmUpLoadedListener {
        void onWarmUpLoaded(ArrayList<String> warmUpList);
    }

    private LoadWarmUp.WarmUpLoadedListener warmUpLoadedListener;

    public LoadWarmUp(Object object) {
        super();
        warmUpLoadedListener = (LoadWarmUp.WarmUpLoadedListener)object;
    }

    public void loadWarmUp(final boolean lower) {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> items = new ArrayList<String>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String item = dataSnapshot1.child("Name").getValue(String.class);
                    if(lower) {
                        if(dataSnapshot1.child("Lower").getValue(Boolean.class)) {
                            items.add(item);
                        }
                    }
                    else {
                        if(dataSnapshot1.child("Upper").getValue(Boolean.class)) {
                            items.add(item);
                        }
                    }
                }

                warmUpLoadedListener.onWarmUpLoaded(items);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        getDatabaseReference().child("Warmup").addValueEventListener(eventListener);
    }
}
