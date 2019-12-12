package hu.bme.aut.fitnessapp.models.database_models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoadWarmUp extends DatabaseConnection{

    private static final String WARMUP = "Warmup";

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
                ArrayList<String> items = new ArrayList<>();
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
                logger.error(DB_ERROR, databaseError.toException());
            }
        };
        getDatabaseReference().child(WARMUP).addValueEventListener(eventListener);
    }

    public void removeListeners() {
        if(eventListener != null) getDatabaseReference().child(WARMUP).removeEventListener(eventListener);
    }
}
