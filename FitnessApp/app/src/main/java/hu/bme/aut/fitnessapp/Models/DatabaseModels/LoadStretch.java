package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoadStretch extends DatabaseConnection{

    public interface StretchLoadedListener {
        void onStretchLoaded(ArrayList<String> stretchList);
    }

    private LoadStretch.StretchLoadedListener stretchLoadedListener;

    public LoadStretch(Object object) {
        super();
        stretchLoadedListener = (LoadStretch.StretchLoadedListener)object;
    }

    private ValueEventListener eventListener;

    public void loadStretch() {
        eventListener = new ValueEventListener() {
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
                databaseError.toException().printStackTrace();
            }
        };
        getDatabaseReference().child("Stretch").addValueEventListener(eventListener);
    }

    public void removeListeners() {
        if(eventListener != null) getDatabaseReference().child("Stretch").removeEventListener(eventListener);
    }
}
