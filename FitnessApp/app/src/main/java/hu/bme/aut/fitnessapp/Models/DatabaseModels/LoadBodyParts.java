package hu.bme.aut.fitnessapp.Models.DatabaseModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoadBodyParts extends DatabaseConnection{

    public interface BodyPartsLoadedListener {
        void onBodyPartsLoaded(ArrayList<String> bodyParts);
    }

    private LoadBodyParts.BodyPartsLoadedListener listLoadedListener;

    public LoadBodyParts(Object object) {
        super();
        listLoadedListener = (LoadBodyParts.BodyPartsLoadedListener)object;
    }

    private ValueEventListener eventListener;

    public void loadBodyParts() {

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> body_parts = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String body_part = (String) dataSnapshot1.getValue();
                    body_parts.add(body_part);
                }

                listLoadedListener.onBodyPartsLoaded(body_parts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }

        };
        getDatabaseReference().child("Body_Parts").addValueEventListener(eventListener);


        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        //this.eventListener = eventListener;
    }

    public void removeListeners() {
        if(eventListener != null) getDatabaseReference().child("Body_Parts").removeEventListener(eventListener);

    }

}
