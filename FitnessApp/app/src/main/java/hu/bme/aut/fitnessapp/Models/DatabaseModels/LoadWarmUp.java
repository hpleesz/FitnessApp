package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoadWarmUp extends DatabaseConnection{

    public interface WarmUpLoadedListener {
        void onWarmUpLoaded(ArrayList<String> warmUpList);
    }

    private LoadWarmUp.WarmUpLoadedListener warmUpLoadedListener;

    public LoadWarmUp(Object object) {
        super();
        warmUpLoadedListener = (LoadWarmUp.WarmUpLoadedListener)object;
    }

    private ValueEventListener eventListener;

    public void loadWarmUp(final boolean lower) {
        eventListener = new ValueEventListener() {
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
                databaseError.toException().printStackTrace();
            }
        };
        getDatabaseReference().child("Warmup").addValueEventListener(eventListener);
    }

    public void removeListeners() {
        if(eventListener != null) getDatabaseReference().child("Warmup").removeEventListener(eventListener);
    }
}
