package hu.bme.aut.fitnessapp.Models.DatabaseLoad;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hu.bme.aut.fitnessapp.Controllers.User.Workout.StretchActivity;
import hu.bme.aut.fitnessapp.Entities.User;

public class LoadStretch extends DatabaseConnection{

    public interface StretchLoadedListener {
        void onStretchLoaded(ArrayList<String> stretchList);
    }

    private LoadStretch.StretchLoadedListener stretchLoadedListener;

    public LoadStretch(Object object) {
        super();
        stretchLoadedListener = (LoadStretch.StretchLoadedListener)object;
    }

    public void loadStretch() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> items = new ArrayList<>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String item = dataSnapshot1.getValue(String.class);
                    items.add(item);
                }

                stretchLoadedListener.onStretchLoaded(items);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        getDatabaseReference().child("Stretch").addValueEventListener(eventListener);
    }
}
